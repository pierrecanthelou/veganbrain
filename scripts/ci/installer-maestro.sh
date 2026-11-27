#!/usr/bin/env bash
# Installe Maestro CLI dans la CI (tâche 11), même version que le spike (spikes/maestro/README.md) :
# maestro.zip de la release cli-2.12.1, empreinte vérifiée contre checksums_sha256.txt de la release.
# Maestro demande Java 17 ou plus (https://docs.maestro.dev/maestro-cli/how-to-install-maestro-cli),
# fourni par actions/setup-java.

set -euo pipefail

version="2.12.1"
empreinte="54dbb0716425626604973cbb374c585ca30272375d9ce15177a1152765ddd9d7"
dossier="${RUNNER_TEMP:?RUNNER_TEMP absent : script prévu pour GitHub Actions}/maestro-${version}"

mkdir -p "$dossier"
curl -fsSL -o "$dossier/maestro.zip" "https://github.com/mobile-dev-inc/maestro/releases/download/cli-${version}/maestro.zip"
echo "${empreinte}  $dossier/maestro.zip" | sha256sum -c -
unzip -q "$dossier/maestro.zip" -d "$dossier"
echo "$dossier/maestro/bin" >> "$GITHUB_PATH"

# Statistiques d'usage et vérification de mise à jour coupées, comme au spike
# (https://docs.maestro.dev/maestro-cli/environment-variables).
{
    echo "MAESTRO_CLI_NO_ANALYTICS=true"
    echo "MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED=true"
    echo "MAESTRO_DISABLE_UPDATE_CHECK=true"
} >> "$GITHUB_ENV"

"$dossier/maestro/bin/maestro" --version
