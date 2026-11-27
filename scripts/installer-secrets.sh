#!/usr/bin/env bash
# Range les secrets du projet hors du dépôt. À lancer soi-même (Git Bash sous Windows) :
#   bash scripts/installer-secrets.sh
#
# Secrets gérés (chaque tâche qui en introduit un l'ajoute ici, docs/suivi.md) :
#   veganbrain.offContact : adresse de contact du User-Agent Open Food Facts (OFF-02),
#                           rangée dans ~/.gradle/gradle.properties, lue par shared/build.gradle.kts.
#
# Garanties :
#   - saisie masquée, jamais affichée, jamais écrite dans un journal ni passée en argument de commande ;
#   - refus d'écrire dans un fichier suivi par Git (un fichier ignoré ou hors dépôt est accepté) ;
#   - relançable : seule la ligne de la clé est remplacée, les autres lignes restent intactes.
#
# Le dossier cible suit GRADLE_USER_HOME s'il est défini (c'est aussi ce que lit Gradle),
# sinon ~/.gradle.

set -euo pipefail
set +x
umask 077

dossier_gradle="${GRADLE_USER_HOME:-$HOME/.gradle}"
cible="$dossier_gradle/gradle.properties"

dire() { printf '%s\n' "$*" >&2; }
echouer() { dire "Erreur : $*"; exit 1; }

# Refuse un fichier suivi ou suivable par Git. Hors d'un dépôt, git renvoie 128 : accepté.
verifier_hors_suivi() {
    local fichier="$1" dossier
    dossier="$(dirname "$fichier")"
    [ -d "$dossier" ] || return 0
    command -v git > /dev/null 2>&1 || echouer "git introuvable : impossible de vérifier que $fichier est hors suivi."
    if git -C "$dossier" rev-parse --is-inside-work-tree > /dev/null 2>&1; then
        if git -C "$dossier" ls-files --error-unmatch -- "$fichier" > /dev/null 2>&1; then
            echouer "$fichier est suivi par Git : refus d'y écrire un secret."
        fi
        if ! git -C "$dossier" check-ignore -q -- "$fichier"; then
            echouer "$fichier est dans un dépôt Git sans être ignoré : refus d'y écrire un secret."
        fi
    fi
}

# Lit une valeur masquée deux fois et la place dans la variable nommée par $1.
lire_secret() {
    local __nom="$1" libelle="$2" motif="$3" premiere seconde
    while true; do
        IFS= read -rs -p "$libelle : " premiere || echouer "saisie interrompue."
        dire ""
        IFS= read -rs -p "Confirmer : " seconde || echouer "saisie interrompue."
        dire ""
        premiere="${premiere%$'\r'}"
        seconde="${seconde%$'\r'}"
        if [ "$premiere" != "$seconde" ]; then
            dire "Les deux saisies diffèrent, on recommence."
        elif [[ ! "$premiere" =~ $motif ]]; then
            dire "Forme refusée, on recommence."
        else
            printf -v "$__nom" '%s' "$premiere"
            return 0
        fi
        [ -t 0 ] || echouer "saisie invalide et entrée non interactive."
    done
}

# Remplace (ou ajoute) la ligne « cle=valeur » de $cible sans toucher aux autres lignes.
ranger_propriete() {
    local cle="$1" valeur="$2" temporaire motif_cle
    mkdir -p "$dossier_gradle"
    verifier_hors_suivi "$cible"
    temporaire="$(mktemp "$dossier_gradle/.gradle.properties.XXXXXX")"
    trap 'rm -f "$temporaire"' EXIT
    # Forme « cle=valeur », « cle:valeur » ou « cle valeur » (toutes valides en .properties).
    motif_cle="^[[:space:]]*${cle//./\\.}([[:space:]]|[=:])"
    if [ -f "$cible" ]; then
        # -U : garde les fins de ligne CRLF des autres lignes telles quelles (grep de Git Bash).
        grep -U -v -E "$motif_cle" "$cible" > "$temporaire" || true
        # Ajoute un saut de ligne final s'il manquait, pour ne pas coller la nouvelle ligne.
        if [ -s "$temporaire" ] && [ -n "$(tail -c 1 "$temporaire")" ]; then
            printf '\n' >> "$temporaire"
        fi
    fi
    printf '%s=%s\n' "$cle" "$valeur" >> "$temporaire"
    mv -f "$temporaire" "$cible"
    trap - EXIT
}

deja_present() {
    local cle="$1"
    [ -f "$cible" ] && grep -q -E "^[[:space:]]*${cle//./\\.}([[:space:]]|[=:])" "$cible"
}

installer_contact_off() {
    local cle="veganbrain.offContact" adresse reponse
    dire "Adresse de contact Open Food Facts (OFF-02), rangée dans $cible."
    mkdir -p "$dossier_gradle"
    verifier_hors_suivi "$cible"
    if deja_present "$cle"; then
        IFS= read -r -p "Une valeur existe déjà. La remplacer ? [o/N] " reponse || reponse=""
        case "${reponse%$'\r'}" in
            o|O|oui|Oui) ;;
            *) dire "Inchangée."; return 0 ;;
        esac
    fi
    lire_secret adresse "Adresse" '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]+$'
    ranger_propriete "$cle" "$adresse"
    unset adresse
    dire "Rangée."
}

installer_contact_off
dire "Terminé. Vérification sans afficher de valeur : git status doit rester propre."
