"""Résumé des rapports JUnit de la CI Linux (tâche 11), écrit dans le résumé du job GitHub Actions.

Un job vert ne prouve pas que les tests attendus ont tourné : ce script échoue si l'une des suites
exigées manque, ou si une suite trouvée compte un échec ou une erreur.

Suites exigées (docs/suivi.md, tâche 11) :
- OngletsPresenterTest : LIS-08 (jvmTest)
- ContrastesTest : A11-01 (jvmTest)
- ThemeSystemeTest : A11-08 (connectedDebugAndroidTest)
- flux Maestro « onglets » (spikes/maestro/flows/onglets.yaml)
"""

import glob
import os
import sys
import xml.etree.ElementTree as ET

RAPPORTS = [
    ("jvmTest", "shared/build/test-results/jvmTest/*.xml"),
    ("émulateur", "androidApp/build/outputs/androidTest-results/connected/**/*.xml"),
    ("Maestro", "build/maestro/rapport.xml"),
]

EXIGEES = {
    "OngletsPresenterTest": "LIS-08",
    "ContrastesTest": "A11-01",
    "ThemeSystemeTest": "A11-08",
    "onglets": "flux Maestro",
}


def suites(chemin):
    racine = ET.parse(chemin).getroot()
    return [racine] if racine.tag == "testsuite" else list(racine.iter("testsuite"))


def nom_court(suite):
    return suite.get("name", "?").rsplit(".", 1)[-1]


def main():
    lignes = ["| Niveau | Suite | Tests | Échecs | Erreurs | Ignorés |", "|---|---|---|---|---|---|"]
    vues = set()
    en_echec = []
    for niveau, motif in RAPPORTS:
        for chemin in sorted(glob.glob(motif, recursive=True)):
            for suite in suites(chemin):
                nom = nom_court(suite)
                compte = {k: int(suite.get(k, "0")) for k in ("tests", "failures", "errors", "skipped")}
                lignes.append(
                    f"| {niveau} | {nom} | {compte['tests']} | {compte['failures']} | {compte['errors']} | {compte['skipped']} |"
                )
                vues.add(nom)
                if compte["failures"] or compte["errors"]:
                    en_echec.append(nom)
                # Maestro nomme la suite « Test Suite » et chaque flux par un testcase.
                for cas in suite.iter("testcase"):
                    vues.add(cas.get("name", ""))
                    vues.add(cas.get("classname", "").rsplit(".", 1)[-1])

    manquantes = [f"{nom} ({critere})" for nom, critere in EXIGEES.items() if nom not in vues]
    lignes.append("")
    lignes.append("Suites exigées absentes : " + (", ".join(manquantes) if manquantes else "aucune"))
    lignes.append("Suites en échec : " + (", ".join(en_echec) if en_echec else "aucune"))

    texte = "\n".join(lignes)
    print(texte)
    resume = os.environ.get("GITHUB_STEP_SUMMARY")
    if resume:
        with open(resume, "a", encoding="utf-8") as f:
            f.write("## Tests de la CI Linux\n\n" + texte + "\n")
    return 1 if manquantes or en_echec else 0


if __name__ == "__main__":
    sys.exit(main())
