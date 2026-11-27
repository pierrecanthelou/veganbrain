#!/usr/bin/env bash
# Tests sur émulateur de la CI Linux (tâche 11), lancés par android-emulator-runner une fois l'émulateur prêt.
# Tourne aussi en local sous Git Bash, avec un émulateur ouvert et `maestro` dans le PATH.
#
# 1. Tests instrumentés (niveau N) : A11-08 (ThemeSystemeTest).
# 2. App de debug installée, puis flux Maestro technique du squelette (niveau E, docs/suivi.md :
#    aucun critère E n'est faisable sur des onglets vides). connectedDebugAndroidTest désinstalle l'app
#    à la fin, d'où l'installation qui suit.

set -euo pipefail

./gradlew :androidApp:connectedDebugAndroidTest
./gradlew :androidApp:installDebug

mkdir -p build/maestro
maestro test --no-ansi \
    --format JUNIT --output build/maestro/rapport.xml \
    --test-output-dir build/maestro/sortie \
    spikes/maestro/flows/onglets.yaml
