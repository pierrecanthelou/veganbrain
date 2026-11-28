#!/usr/bin/env bash
# Scan de secrets de la CI (tâche 11). Lancé par .github/workflows/ci-linux.yml, sous Linux x64.
#
# 1. Installe gitleaks, version fixée, empreinte vérifiée contre gitleaks_8.30.1_checksums.txt
#    de la release (https://github.com/gitleaks/gitleaks/releases/tag/v8.30.1). Licence MIT.
# 2. Témoin : dans un dépôt jetable hors du dépôt, un faux jeton fabriqué à l'exécution doit faire
#    échouer le scan avec la règle github-pat (config/gitleaks.toml:2159-2163 de la v8.30.1 :
#    le préfixe des jetons personnels classiques de GitHub suivi de 36 caractères alphanumériques,
#    entropie 3 au moins). Aucun faux secret n'est
#    écrit dans le dépôt : le préfixe est assemblé en deux morceaux pour que ce script ne déclenche
#    pas la règle, et le jeton est tiré au hasard à chaque passage.
# 3. Scan de tout l'historique du dépôt (`gitleaks git`, checkout avec fetch-depth: 0) : échec si
#    une fuite est trouvée. Les valeurs trouvées sont masquées (--redact).

set -euo pipefail

version="8.30.1"
archive="gitleaks_${version}_linux_x64.tar.gz"
empreinte="551f6fc83ea457d62a0d98237cbad105af8d557003051f41f3e7ca7b3f2470eb"

outils="$(mktemp -d)"
curl -fsSL -o "$outils/$archive" "https://github.com/gitleaks/gitleaks/releases/download/v${version}/${archive}"
echo "${empreinte}  $outils/$archive" | sha256sum -c -
tar -xzf "$outils/$archive" -C "$outils" gitleaks
gitleaks="$outils/gitleaks"
"$gitleaks" version

echo "::group::Témoin : un jeton github-pat doit faire échouer le scan"
temoin="$(mktemp -d)"
git -C "$temoin" init -q
git -C "$temoin" config user.name "temoin"
git -C "$temoin" config user.email "temoin@exemple.invalid"
prefixe="gh""p_"
corps="$(openssl rand -hex 18)" # 36 caractères hexadécimaux, tirés au hasard
printf 'jeton = "%s%s"\n' "$prefixe" "$corps" > "$temoin/config.txt"
git -C "$temoin" add config.txt
git -C "$temoin" commit -q -m "témoin"

set +e
"$gitleaks" git --no-banner --redact --report-format json --report-path "$outils/temoin.json" "$temoin"
code=$?
set -e
regles="$(jq -r '[.[].RuleID] | unique | join(",")' "$outils/temoin.json")"
echo "Témoin : code de sortie ${code}, règles ${regles}"
if [ "$code" -ne 1 ] || [ "$regles" != "github-pat" ]; then
    echo "::error::Le témoin n'a pas fait échouer le scan comme attendu (code ${code}, règles '${regles}')."
    exit 1
fi
rm -rf "$temoin"
echo "::endgroup::"

echo "Scan de tout l'historique du dépôt"
"$gitleaks" git --no-banner --redact -v .
