# Table dorée — critères d'acceptation de la V1

Date : 2026-11-22. Statut : questions Q-1 à Q-12 tranchées le 2026-11-22. Relu par deux relecteurs le 2026-11-22 ; leurs points encore ouverts sont en section 14. Les « choix annoncés » sont des choix réversibles faits sans question, à contester si besoin.

Chaque ligne est un critère vérifiable. Les tests de la phase 2 en partent, et une tranche verticale de la phase 3 n'est finie que lorsque ses lignes passent.

## Préséance des sources

1. Les décisions de `docs/suivi.md`, section Fait (écarts 1 à 12, points 17 à 20), l'emportent sur tout le reste.
2. Viennent ensuite `docs/design/README.md` et `docs/design/tokens.json`. Les renvois au README désignent la version du commit b515f31.
3. Les maquettes `docs/maquettes/*.dc.html` viennent en dernier. Quand une ligne de maquette contredit une décision, la règle cite la décision et signale la ligne périmée (colonne Source, « périmé : »).
4. `docs/cadrage.md` fixe la synchro, les erreurs, les rappels, Open Food Facts (OFF) et l'outillage de test.

Les états qu'aucune maquette ne dessine sont marqués « non dessiné » dans la colonne Écran, avec la décision en renvoi.

Les exemples reprennent les données de démonstration des maquettes, qui sont fictives (`Main.dc.html:109-122`, `Recettes.dc.html:104-116`). Aucune donnée personnelle ne doit entrer dans ce fichier ni dans les jeux de test, puisque le dépôt est public (`docs/suivi.md`, Parking, « Dépôt public »).

## Niveaux de test

| Code | Niveau | Outil | Source |
|---|---|---|---|
| U | Unitaire, module partagé | `kotlin.test` en commonTest, `kotlinx-coroutines-test` et Turbine pour les Flow | `docs/cadrage.md:378-379` |
| C | Convergence de synchro : deux clients simulés et le serveur | Côté client, commonTest. Côté Worker, outil à choisir en phase 2 | `docs/cadrage.md:245` |
| W | Worker seul (API, authentification) | Outil à choisir en phase 2 | `docs/cadrage.md:237-245` |
| E | De bout en bout, deux plateformes | Maestro, sélecteurs par identifiants d'accessibilité communs | `docs/cadrage.md:382`, `:390`, `:394` |
| A | Accessibilité automatisée | Compose `enableAccessibilityChecks()` et `FontScale` ; XCUITest `performAccessibilityAudit` | `docs/cadrage.md:383-384` |
| N | Écran natif isolé | Compose UI Test, XCUITest | `docs/cadrage.md:392`, `:394` |
| M | Manuel, sur appareil | — | Voir chaque ligne |

Un critère d'état d'écran (ce que l'écran affiche, ce qu'une intention change) se teste en U sur le présentateur partagé, puisque les UI ne sont que des fonctions de cet état (`docs/cadrage.md:74-75`). Le niveau E ne couvre que les parcours critiques (`docs/cadrage.md:394`).

Le scan par caméra n'est pas pilotable en simulateur avec certitude (`docs/cadrage.md:428`, `:479`). Les lignes E du scan passent donc par « Saisir le code-barres » (SCN-16), qui suit le même parcours qu'un code lu. La lecture par la caméra elle-même reste un test manuel (SCN-02).

Les lignes E qui passent par OFF s'appuient sur un faux serveur OFF, branché dans un build de test (choix annoncé). Le risque nommé en `docs/cadrage.md:396` (mode avion, boîtes d'autorisation, caméra, notification sous Maestro) est levé sur Android par le spike `spikes/maestro/README.md` (2026-11-27). Les choix qui en découlent (réponse du 2026-11-27) laissent la colonne Niveau inchangée. Autorisations système (RAP-04, RAP-12, SCN-12) : Maestro, chaque autorisation nommée dans `launchApp` (`notifications`, `camera`), puisque la clé `all` et le défaut sont sans effet ; le M reste jusqu'au volet iOS (tâche 14). Hors réseau (CHR-01, SCN-04, SCN-09, FPR-08) : Maestro, `setAirplaneMode` sur Android ; sur iOS, dont les simulateurs n'ont pas de mode avion, une branche du flux (`runFlow` avec `when: platform: iOS`, https://docs.maestro.dev/maestro-flows/flow-control-and-logic/conditions) active une coupure réseau du build de test. Notification touchée (RAP-07) : Maestro, volet ouvert par glissement. Photo (FPR-09) : Maestro ; la prise d'une photo en émulateur, non essayée, se vérifie quand l'écran existe, avec repli sur « Choisir » ou sur M. Paysage (A11-09) : Maestro sur un AVD tablette pour la rotation et l'usage, contrôle visuel manuel pour les éléments coupés.

## 1. Listes et navigation

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| LIS-01 | La liste des produits montre une ligne par produit non supprimé : vignette, nom, « marque · catégorie », pastille de note. | Produit {nom « Steak végétal au poivre », marque « Les Prés Verts », catégorie « Simili-carné », note Super} → ligne « Steak végétal au poivre », sous-titre « Les Prés Verts · Simili-carné », pastille Super. | Main | U, E | `Main.dc.html:56-80`, `:110` |
| LIS-02 | La note d'une ligne est une pastille de 36 px avec l'icône seule (étoile, coche, croix). Son nom accessible est le libellé de la note. | Note Bon → pastille coche, nom accessible « Bon ». | Main, Recettes | U, A | `README.md:8`, `Main.dc.html:65-75`, `Recettes.dc.html:65-75` |
| LIS-03 | Un produit sans note porte la pastille « À noter » (icône horloge, contour en pointillés). | Produit sans note → pastille horloge, nom accessible « À noter ». | Main | U, A | `README.md:18`, `Main.dc.html:80` |
| LIS-04 | Un produit sans note issu d'un scan montre « marque · scanné en magasin » à la place de la catégorie. Pour un produit créé à la main sans note, le texte est à fixer à l'implémentation. | Produit scanné {marque « Atelier du Blé », sans note} → « Atelier du Blé · scanné en magasin ». | Main | U | `Main.dc.html:109` |
| LIS-05 | La liste des recettes montre le nom, l'icône de la source, un sous-titre de source et la pastille de note. Livre : « Livre · page N ». Insta : « Insta · lien enregistré ». Adaptée : « Adaptée · » suivi de la recette d'origine. | Recette {source Livre, page « 84 »} → « Livre · page 84 ». Recette {source Insta, lien rempli} → « Insta · lien enregistré ». | Recettes | U | `Recettes.dc.html:51-61`, `:105-107` |
| LIS-06 | Produits : les « À noter » d'abord, puis les autres ; dans chaque groupe, du plus récemment ajouté au plus ancien. Recettes (toujours notées) : du plus récemment ajouté au plus ancien. « Ajouté » veut dire la date de création, quel que soit le téléphone ; modifier une fiche ne la déplace pas. À date égale, l'ordre alphabétique départage (choix annoncé). Noter un produit le fait passer dans le second groupe. | Produits ajoutés dans l'ordre A (Bon), B (À noter), C (Super) → B, C, A. Noter B Bon → C, B, A. | Main, Recettes | U | Réponse Q-7 « À noter, puis récents » (2026-11-22) ; données `Main.dc.html:109` (produit à noter en tête) |
| LIS-07 | Toucher une ligne ouvre la fiche de l'élément. | Toucher « Dahl de lentilles corail » → fiche recette de cette recette. | Main, Recettes | E | `Main.dc.html:56`, `Recettes.dc.html:48` |
| LIS-08 | La barre d'onglets mène à Produits et à Recettes ; l'onglet de l'écran courant est marqué comme courant. | Sur Recettes → onglet Recettes à l'état « courant » (`aria-current`). | Main, Recettes | U, A | `Main.dc.html:87-94`, `Recettes.dc.html:82-89` |
| LIS-09 | Le « + » de Produits et le bouton de scan de l'en-tête ouvrent le scan. | Toucher « Ajouter un produit » → écran « Identifier un produit ». | Main | E | `Main.dc.html:44`, `:98` |
| LIS-10 | Le « + » de Recettes ouvre la feuille vide « Nouvelle recette » (voir RCT-01). | Toucher « Ajouter une recette » → feuille « Nouvelle recette », tous champs vides. | Nouvelle recette (non dessiné) | E | `docs/suivi.md`, écart 10 ; périmé : `Recettes.dc.html:93` ouvre une fiche remplie |
| LIS-11 | Liste vide, ou recherche sans résultat : le texte est à fixer à l'implémentation. | — | Non dessiné | U | Non tranché |

## 2. Recherche et filtres

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| CHR-01 | La recherche tourne sur le téléphone, dans les données synchronisées : elle marche hors réseau. | Mode avion, Produits, saisie « poivre » → Steak végétal au poivre. Mode avion, Recettes, saisie « poivre » → Burgers maison, sauce au poivre. | Main, Recettes | U, E | `docs/suivi.md`, écart 11 ; données `Main.dc.html:110`, `Recettes.dc.html:111` |
| CHR-02 | Produits : la recherche porte sur le nom, la marque et la catégorie. | « Avoine du Nord » → Boisson avoine barista, Crème d'avoine cuisine. « Desserts » → Yaourt coco vanille, Glace vanille coco. | Main | U | `docs/suivi.md`, écart 11 ; données `Main.dc.html:112`, `:114`, `:116`, `:119` |
| CHR-03 | Recettes : la recherche porte sur le nom seulement, ni la source ni les ajustements. | « lentilles » → Dahl de lentilles corail, Lasagnes aux lentilles. « page » → aucune recette. | Recettes | U | `docs/suivi.md`, écart 11 ; données `Recettes.dc.html:105-108` |
| CHR-04 | La recherche ignore la casse et les accents, et traite « œ » comme « oe » et « æ » comme « ae ». Elle trouve le texte saisi n'importe où dans le champ, et l'apostrophe droite vaut l'apostrophe typographique (choix annoncés). | « creme » → Crème d'avoine cuisine. « BLE » → Steak de seitan grillé et Nuggets de blé (marque Atelier du Blé). Produits, « oeuf » → Mayonnaise sans œuf. Recettes, « oeuf » → Quiche aux poireaux sans œuf, Crêpes sans œuf. | Main, Recettes | U | Réponse Q-8 « Ignorer casse et accents » (2026-11-22) ; données `Main.dc.html:109-122`, `Recettes.dc.html:104-116` |
| CHR-05 | La recherche se combine avec le filtre de note : un élément s'affiche s'il passe les deux. | Produits, filtre Super, saisie « Simili » → Steak végétal au poivre seul. | Main, Recettes | U | `docs/suivi.md`, écart 11 ; données `Main.dc.html:109-122` |
| CHR-06 | Filtres des produits : Tous, Super, Bon, Pas bon, À noter (icône horloge). Un seul filtre est actif à la fois ; Tous est actif à l'ouverture. | Filtre À noter → Steak de seitan grillé seul (seul produit sans note de la démo). | Main | U | `README.md:67`, `docs/suivi.md`, écart 4, `Main.dc.html:107`, `:125` ; périmé : `Main.dc.html:127-131` n'a pas « À noter » |
| CHR-07 | Filtres des recettes : Toutes, Super, Bon, Pas bon, sans « À noter ». Toutes est actif à l'ouverture. | Filtre Pas bon → Tofu brouillé, Crêpes sans œuf. | Recettes | U | `README.md:67`, `Recettes.dc.html:102`, `:119`, `:125-130` |
| CHR-08 | Le filtre actif expose son état sélectionné aux lecteurs d'écran. | Filtre Bon actif → état « sélectionné » sur Bon, pas sur les autres. | Main, Recettes | A, N | `Main.dc.html:50` (`aria-pressed`) |

## 3. Scan

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| SCN-01 | Le scan lit les codes-barres seulement : pas de mode Photo en V1. | Écran de scan → aucun choix « Code-barres / Photo ». | Scan | N | `docs/suivi.md`, écart 1 ; périmé : `Scan.dc.html:29-32`, `:41-46` |
| SCN-02 | Formats lus : EAN-13 et EAN-8. | Code EAN-13 lu → recherche du produit. | Scan | M | `docs/cadrage.md:40-41` |
| SCN-03 | La fiche s'ouvre dès que le code est reconnu, sans geste de plus. Le bouton « Capturer » de la maquette devient « Saisir le code-barres » (SCN-16). | Code reconnu → écran suivant (SCN-04 à SCN-09). | Scan | M | `Scan.dc.html:39` ; réponse Q-11 « Saisir le code » (2026-11-22) ; périmé : « Capturer » `Scan.dc.html:51` |
| SCN-04 | Code présent dans le carnet, sur un produit non supprimé : l'écran « Déjà dans le carnet » montre le nom, « marque · catégorie », la note et l'avis. La recherche est locale, donc l'écran marche aussi hors réseau. | Code de « Steak végétal au poivre », hors réseau → « Déjà dans le carnet », note Super. | ScanConnu | U, E | `ScanConnu.dc.html:22`, `:34-47` ; `docs/cadrage.md:190-193` (base locale) |
| SCN-05 | Sur « Déjà dans le carnet », « Scanner un autre produit » rouvre le scan et « Voir ou modifier la fiche » ouvre la fiche produit. | Toucher « Voir ou modifier la fiche » → fiche de ce produit. | ScanConnu | E | `ScanConnu.dc.html:51-52` |
| SCN-06 | Produit sans note retrouvé au scan : « Déjà dans le carnet » montre la pastille « À noter » (icône horloge) à la place de la note, et les mêmes boutons que SCN-05. | Rescan du code de « Steak de seitan grillé » → « Déjà dans le carnet », pastille À noter. | ScanConnu (variante non dessinée) | U | Réponse Q-6 « Pastille + rappel modifiable » (2026-11-22), `README.md:18` |
| SCN-07 | Code inconnu du carnet, réseau présent, produit trouvé par OFF : le formulaire « Produit scanné » s'ouvre, prérempli (nom, marque, catégorie), sans note. Un encart dit « Trouvé dans Open Food Facts, vérifiez » et porte le lien « Données Open Food Facts, ODbL ». Le code-barres est gardé avec le produit. | Code inconnu, OFF renvoie un produit → titre « Produit scanné », encart et lien présents, note vide. | ScanInconnu | U, E | `docs/suivi.md`, écarts 1 et 8, `README.md:43`, `ScanInconnu.dc.html:44`, `:116` (données préremplies), `:119` ; périmé : texte de l'encart `ScanInconnu.dc.html:47` |
| SCN-08 | Code inconnu du carnet et inconnu d'OFF : l'écran Erreur s'affiche (voir ERR-01). | OFF répond « produit inconnu » → écran Erreur. | Erreur | U, E | `docs/suivi.md`, écart 2 |
| SCN-09 | Code inconnu du carnet, sans réseau (ou OFF indisponible, ou limite de débit atteinte) : le formulaire « Produit scanné » s'ouvre vide, sans note, le code-barres gardé. Un encart dit que nom, marque et catégorie seront complétés au retour du réseau (texte exact à fixer à l'implémentation). Dans ce cas, comme sur la fiche d'un produit resté sans nom, le nom est facultatif (exceptions de FPR-06, SCN-15). La photo et le bloc de rappel restent, sans délai présélectionné (RAP-01, RAP-02). | Mode avion, code inconnu → « Produit scanné », champs vides, encart hors réseau ; « Ajouter au carnet » actif sans nom. | ScanInconnu (variante non dessinée) | U, E | `docs/suivi.md`, écart 2, `docs/cadrage.md:466`, `:468`, `:288-293` ; réponse Q-2 « Formulaire, nom facultatif » (2026-11-22) |
| SCN-14 | Au retour du réseau, le téléphone qui a scanné interroge OFF pour ce code, après une réception de la synchro. Seuls les champs encore vides sont remplis ; ce qui a été tapé, ici ou par l'autre personne, est gardé. L'image d'OFF ne s'ajoute que si la fiche n'a pas de photo (FPR-09). Le lien ODbL s'ajoute à la fiche si un champ vient d'OFF (FPR-13). Si OFF ne connaît pas le code, la fiche reste telle quelle (choix annoncé). Mécanisme : une valeur venue d'OFF est marquée comme telle ; elle ne s'écrit que dans un champ vide, et elle cède devant toute saisie du même champ, quelle que soit l'HLC, côté serveur comme à la réception sur le téléphone (exception à SYN-05, choix annoncé). | Gardé hors réseau avec la marque « Atelier du Blé » tapée, sans nom ; OFF renvoie le nom « Steak de seitan grillé » et la marque « Atelier Blé » → nom « Steak de seitan grillé », marque « Atelier du Blé » inchangée. A scanne pendant qu'OFF est indisponible (ERR-02) et envoie le produit sans nom ; B le reçoit, passe hors réseau et tape le nom « Steak au blé » ; OFF revient et A préremplit le nom ; puis B envoie → après synchro, nom « Steak au blé » chez A et chez B. | — | U, C | Réponse Q-2 (2026-11-22) ; ordre après réception et mécanisme : choix annoncés |
| SCN-15 | Un produit sans nom s'affiche dans la liste sous le nom « Produit à compléter », jusqu'au préremplissage ou à la saisie du nom ; son sous-titre suit LIS-01 ou LIS-04, et vaut « scanné en magasin » seul s'il n'a pas de marque (choix annoncé). | Produit gardé hors réseau, sans nom, sans marque, sans note → nom « Produit à compléter », sous-titre « scanné en magasin », pastille À noter. Même produit avec la marque « Atelier du Blé » → sous-titre « Atelier du Blé · scanné en magasin ». | Main (non dessiné) | U | Réponse Q-2 (2026-11-22) ; libellé : choix annoncé |
| SCN-10 | Code d'un produit supprimé (pierre tombale) : traité comme un code inconnu du carnet, et le nouveau produit reçoit un nouvel identifiant. Choix annoncé, non validé. | Scan du code de « Nuggets de blé » après sa suppression → parcours SCN-07, SCN-08 ou SCN-09. | ScanInconnu | U | Choix de conception (2026-11-22) |
| SCN-11 | « Saisir le nom à la main » ouvre la création à la main, sans code-barres. | Toucher le lien → « Nouveau produit », champs vides. | Scan, Creation | E | `Scan.dc.html:48`, `Creation.dc.html:44` |
| SCN-12 | Caméra refusée : un écran explique pourquoi la caméra sert, propose un lien vers les réglages et la saisie manuelle du code-barres. La saisie manuelle est celle de SCN-16. | Autorisation caméra refusée → écran d'explication, lien vers les réglages, « Saisir le code-barres ». | Non dessiné | E, M | `docs/cadrage.md:469` |
| SCN-13 | « Fermer » du scan ramène à la liste des produits. | Toucher « Fermer » → « Nos produits ». | Scan | E | `Scan.dc.html:24` |
| SCN-16 | « Saisir le code-barres », à la place de « Capturer » sur le scan et sur l'écran caméra refusée, ouvre un champ à clavier numérique. Le code est accepté s'il a 8 ou 13 chiffres et une clé de contrôle juste (EAN-8, EAN-13 ; algorithme à citer depuis la documentation GS1 en phase 2) ; sinon le bouton de validation est grisé et un message dit pourquoi (texte à fixer). Un code accepté suit le même parcours qu'un code lu (SCN-04 à SCN-09). Règles de validation : choix annoncé. | « 2000000000008 » → accepté, même suite qu'un scan. « 2000000000009 » → bouton désactivé, message « clé de contrôle ». « 20000004 » (EAN-8) → accepté. « 12345 » → bouton désactivé. | Scan, caméra refusée (saisie non dessinée) | U, E | Réponse Q-11 « Saisir le code » (2026-11-22), `docs/cadrage.md:40-41`, `:469` |

Les codes des exemples sont fictifs : clé de contrôle calculée, aucun produit réel visé.

## 4. Formulaire produit

Un seul formulaire, trois usages : fiche existante, produit scanné, création à la main (`README.md:43`).

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| FPR-01 | Ordre des champs : photo, nom, marque et catégorie, note, avis, lieu d'achat. Sur la fiche existante, la section « Utilisé dans nos recettes » suit. | — | Produit, ScanInconnu, Creation | N | `README.md:43`, `Produit.dc.html:95` |
| FPR-02 | Création à la main : titre « Nouveau produit », champs vides, aucune note, bouton « Ajouter au carnet », pas de « Supprimer ». | Ouverture → tous champs vides, aucune note pressée. | Creation | U | `Creation.dc.html:44`, `:98`, `:107`, `:110` |
| FPR-03 | Fiche existante : titre « Fiche produit », champs remplis et modifiables, « Supprimer » en haut à gauche, « Fermer » en haut à droite, bouton « Enregistrer ». | Fiche de « Steak végétal au poivre » → note Super pressée, avis rempli. | Produit | U | `README.md:41`, `Produit.dc.html:39-45`, `:107`, `:116-119` |
| FPR-04 | Une seule note à la fois ; toucher la note déjà choisie la retire. | Note Bon, toucher Bon → aucune note. Note Bon, toucher Super → Super. | Produit, ScanInconnu, Creation | U | `ScanInconnu.dc.html:120`, `Produit.dc.html:120`, `Creation.dc.html:111` |
| FPR-05 | La note d'un produit est facultative, au scan comme à la création. Un produit enregistré sans note est « À noter » (LIS-03). | « Ajouter au carnet » sans note → produit ajouté, filtre À noter le montre. | ScanInconnu, Creation | U | `ScanInconnu.dc.html:71`, `Creation.dc.html:70`, `README.md:18` |
| FPR-06 | Le nom est le seul champ obligatoire du produit. Tant qu'il est vide, « Ajouter au carnet » ou « Enregistrer » est grisé, et les lecteurs d'écran l'annoncent comme désactivé. Un nom fait seulement d'espaces compte comme vide (choix annoncé). Deux exceptions : le produit scanné hors réseau (SCN-09), et la fiche d'un produit dont le nom était déjà vide à l'ouverture, qui s'enregistre sans nom (SCN-15). | Création, nom vide → bouton désactivé ; saisie « Tofu fumé » → bouton actif. Fiche existante, nom effacé → « Enregistrer » désactivé. Fiche d'un « Produit à compléter », nom vide, note Bon choisie → « Enregistrer » actif ; liste : « Produit à compléter », pastille Bon. | Produit, ScanInconnu, Creation | U, A | Réponse Q-1 « Bouton grisé » (2026-11-22) ; réponse Q-12 « Actif, nom laissé vide » (2026-11-22) |
| FPR-07 | L'avis est unique et commun au carnet. | A écrit l'avis, B ouvre la fiche après synchro → même avis. | Produit | C | `docs/suivi.md`, écart 3, `README.md:43` |
| FPR-08 | « Ajouter au carnet » ou « Enregistrer » écrit en local, hors réseau compris, puis ramène à la liste des produits. « Enregistrer » n'écrit que les champs qui diffèrent de l'état à l'ouverture, pour ne pas écraser un champ changé entre-temps sur l'autre téléphone (SYN-06, choix annoncé). | Mode avion, « Ajouter au carnet » → produit dans la liste, marqué non synchronisé (ERR-03). A ouvre une fiche et change l'avis ; B change le lieu d'achat et synchronise ; A enregistre → avis de A et lieu d'achat de B gardés. | ScanInconnu, Creation, Produit | U, C, E | `ScanInconnu.dc.html:107`, `Creation.dc.html:98`, `Produit.dc.html:107`, `docs/cadrage.md:191`, `:211` |
| FPR-09 | Produit trouvé par OFF : si OFF fournit une image (`image_front_url`), elle devient la photo de la fiche, avec la mention « Photo Open Food Facts, CC BY-SA » (texte exact à fixer). « Changer la photo » la remplace par une photo prise ou choisie, et la mention disparaît. L'image est copiée dans R2, pour qu'on la voie hors réseau et sur l'autre téléphone. La fiche garde l'origine de sa photo (OFF ou vous) : la mention suit l'image tant qu'elle reste. | OFF renvoie une image → photo affichée, mention CC BY-SA visible ; « Changer la photo » puis photo prise → mention absente. A ajoute un produit avec l'image d'OFF ; après synchro, B voit la photo et la mention. OFF sans image → zone « Prendre une photo / Choisir » (FPR-10). | ScanInconnu, Produit | U, C, E | Réponse Q-3 « Image OFF proposée » (2026-11-22), `ScanInconnu.dc.html:52`, `docs/cadrage.md:258`, `:262` ; périmé : « Photo prise au scan » `ScanInconnu.dc.html:51` |
| FPR-10 | Sans photo, une zone en pointillés propose « Prendre une photo » et « Choisir ». Avec photo, un bloc de 150 px montre la photo et le bouton « Changer la photo ». | Création → zone « Aucune photo pour l'instant ». | Creation, Produit | N | `README.md:49`, `Creation.dc.html:45-51`, `Produit.dc.html:49` |
| FPR-11 | « Fermer » avec des modifications non enregistrées ouvre une boîte de confirmation « Abandonner les modifications ? » (texte exact à fixer), sur le modèle de la boîte de suppression : alerte modale, « Continuer » à gauche (retour à la fiche, rien de perdu), « Abandonner » à droite (fermeture sans enregistrer). Une modification, c'est un champ qui diffère de l'état à l'ouverture, préremplissage compris : changer un champ puis le remettre comme avant n'en est pas une. Le geste retour d'Android et le glissement de la feuille vers le bas sur iOS suivent la même règle (choix annoncé). | Fiche « Steak végétal au poivre », avis modifié, « Fermer » → boîte ; « Continuer » → fiche avec l'avis modifié ; « Abandonner » → liste, avis d'origine. Fiche sans modification, « Fermer » → liste, sans boîte. | Produit, ScanInconnu, Creation, Recette, Nouvelle recette (boîte non dessinée) | U, E | Réponse Q-5 « Boîte Abandonner ? » (2026-11-22), `README.md:61` (modèle de boîte) |
| FPR-12 | « Fermer » sans modification, ou « Abandonner » dans la boîte de FPR-11 : la fiche ramène à la liste des produits ; le produit scanné et la création ramènent au scan. | Fiche produit, « Fermer » → « Nos produits ». | Produit, ScanInconnu, Creation | E | `Produit.dc.html:40`, `ScanInconnu.dc.html:39`, `Creation.dc.html:39` |
| FPR-13 | Le lien « Données Open Food Facts, ODbL » n'apparaît que sur un produit prérempli par OFF : au scan (SCN-07), puis sur sa fiche, y compris après un préremplissage différé (SCN-14). La fiche Produit est un choix annoncé. Sa cible exacte est à fixer à l'implémentation. | Création à la main → pas de lien ODbL. Produit prérempli par OFF, rouvert depuis la liste → lien présent. | ScanInconnu, Produit (lien non dessiné sur Produit) | U | `docs/suivi.md`, écart 8, `docs/cadrage.md:282` |

## 5. Rappel « Me rappeler de le noter »

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| RAP-01 | Le bloc de rappel s'affiche tant qu'aucune note n'est choisie : au scan, à la création à la main, et sur la fiche d'un produit existant sans note (RAP-10). | Note vide → bloc visible. Choisir Bon → bloc masqué. Retirer Bon → bloc de retour. | ScanInconnu, Creation, Produit (bloc non dessiné sur Creation et Produit) | U | `docs/suivi.md`, écart 5, `README.md:45`, `ScanInconnu.dc.html:87-94` |
| RAP-02 | Choix : Ce soir, Demain, Dans 3 jours, Pas de rappel. Aucun n'est présélectionné ; sans choix, aucun rappel n'est programmé. | Ouverture → aucun délai pressé ; enregistrer sans choix → aucune notification programmée. | ScanInconnu, Creation, Produit | U | `docs/suivi.md`, écart 5, `ScanInconnu.dc.html:132` ; périmé : `ScanInconnu.dc.html:131` présélectionne « Ce soir » |
| RAP-03 | Les rappels sonnent à 19 h, heure locale du téléphone : le jour même (« Ce soir »), le lendemain (« Demain ») ou trois jours plus tard (« Dans 3 jours »). Le jour compté est celui de l'enregistrement de la fiche (choix annoncé). Sur Android, l'alarme est inexacte : à partir d'Android 12, elle part dans l'heure, sauf en économie de batterie ou en Doze, où aucun délai n'est garanti (RAP-09). | Mardi 11 h, « Demain » → rappel mercredi 19 h. Mardi 11 h, « Dans 3 jours » → vendredi 19 h. | ScanInconnu, Creation, Produit | U | Réponse Q-4 « 19 h, avant le dîner » (2026-11-22), `docs/cadrage.md:343`, `:365` |
| RAP-11 | « Ce soir » est grisé à partir de 18 h, et les lecteurs d'écran l'annoncent comme désactivé. | Mardi 17 h 59 → « Ce soir » actif. Mardi 18 h → « Ce soir » désactivé. | ScanInconnu, Creation, Produit | U, A | Réponse Q-4 (2026-11-22) |
| RAP-12 | Notifications refusées : aucune notification ne part, un message propose d'ouvrir les réglages (texte à fixer), et le produit reste dans le filtre « À noter ». | Autorisation refusée, « Demain » → message avec lien vers les réglages ; aucune notification programmée. | ScanInconnu, Creation, Produit (non dessiné) | E, M | Réponse Q-4 (2026-11-22) |
| RAP-04 | L'autorisation de notifier est demandée au premier choix d'un délai, jamais au lancement. | Premier toucher de « Demain » → demande système ; second produit, « Demain » → pas de nouvelle demande si déjà accordée. | ScanInconnu, Creation, Produit | E, M | `docs/suivi.md`, écart 5, `docs/cadrage.md:342`, `:363` |
| RAP-05 | Le rappel est annulé dès que le produit est noté sur ce téléphone. | Rappel « Demain » posé, note Bon choisie sur la fiche le soir même → plus aucune notification programmée pour ce produit. | Produit | U | `docs/suivi.md`, écart 5, `docs/cadrage.md:360` |
| RAP-06 | Le rappel est aussi annulé quand la note arrive par la synchro, venant de l'autre personne. | A pose un rappel ; B note le produit ; A synchronise → rappel de A annulé. | — | C | `docs/cadrage.md:360` |
| RAP-07 | Ouvert depuis le rappel : l'app synchronise d'abord ; si le produit a été noté entre-temps, elle affiche un message « déjà noté » (texte à fixer à l'implémentation). | B a noté le produit, le rappel de A sonne avant synchro → A touche la notification → synchro puis message « déjà noté ». | Non dessiné | C, E | `docs/cadrage.md:364` |
| RAP-08 | Le rappel d'un produit supprimé est annulé. Choix annoncé, non validé. | Produit « À noter » supprimé → sa notification est retirée. | — | U | Choix de conception (2026-11-22) |
| RAP-09 | Android : le rappel est programmé par alarme inexacte, sans permission d'alarme exacte, et reprogrammé au redémarrage du téléphone. | « Demain » → alarme `setAndAllowWhileIdle()` ; après redémarrage, rappel toujours programmé. | — | M | `docs/cadrage.md:343`, `:365` |
| RAP-10 | La fiche d'un produit existant encore sans note montre le bloc de rappel, sans délai pressé. Si ce téléphone a un rappel programmé pour ce produit, une ligne l'annonce (« Rappel prévu : mercredi 19 h », texte exact à fixer). Choisir un délai remplace ce rappel ; « Pas de rappel » l'annule. Le rappel est local : la ligne ne montre pas celui de l'autre téléphone (choix annoncé). | Rappel prévu mercredi 19 h, choix « Dans 3 jours » mardi → rappel vendredi 19 h seulement. « Pas de rappel » → plus aucun rappel, ligne masquée. | Produit (variante non dessinée) | U | Réponse Q-6 (2026-11-22), `docs/cadrage.md:334` (notifications locales) |

## 6. Recettes

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| RCT-01 | Nouvelle recette : feuille « Nouvelle recette », champs vides, aucune source ni note présélectionnée, pas de « Supprimer », bouton « Ajouter au carnet ». Une section « Produits utilisés », vide, propose « Lier un produit » (LIE-07). | Toucher « + » de Recettes → aucune source pressée, aucune note pressée, aucun produit lié. | Nouvelle recette (non dessiné) | U, E | `docs/suivi.md`, écart 10, `README.md:47` ; périmé : `Recette.dc.html:134` (Super) et `:147` (Adaptée) présélectionnés |
| RCT-02 | Une recette exige un nom, une source et une note. Tant que l'un manque, « Ajouter au carnet » ou « Enregistrer » est grisé, et les lecteurs d'écran l'annoncent comme désactivé. Les champs propres à la source (lien, livre, page, recette d'origine) restent facultatifs (choix annoncé). | Nouvelle recette, nom « Chili sin carne » et source Livre, sans note → bouton désactivé ; choix de Bon → bouton actif. | Nouvelle recette, Recette | U, A, E | `docs/suivi.md`, écarts 4 et 10 ; réponse Q-1 « Bouton grisé » (2026-11-22) |
| RCT-03 | Source au choix : Insta, Livre, Adaptée ; une seule à la fois. Insta montre « Lien du post », Livre montre « Livre » et « Page », Adaptée montre « Recette d'origine ». | Choisir Livre → champs « Livre » et « Page » visibles, « Lien du post » masqué. | Recette | U | `Recette.dc.html:52-84`, `:147-149` |
| RCT-04 | « Page » n'accepte que des chiffres et ouvre le clavier numérique ; un caractère non numérique tapé n'est pas inséré, et un texte collé qui contient autre chose que des chiffres est refusé en entier, pour ne pas fabriquer « 8485 » à partir de « 84-85 » (choix annoncé). « Lien du post », s'il n'est pas vide, doit commencer par `http://` ou `https://`, majuscules permises et espaces autour retirés. Sinon « Ajouter au carnet » ou « Enregistrer » est grisé, et un message sous le champ dit pourquoi (texte à fixer). Les deux champs restent facultatifs (RCT-02). | Page : taper « 84 » → « 84 » ; taper « 84- » → « 84 » ; coller « 84-85 » → champ inchangé. Lien « instagram.com/p/abc » → bouton désactivé, message ; « https://www.instagram.com/p/abc » → bouton actif ; lien vide → bouton actif. | Recette, Nouvelle recette (message non dessiné) | U, A | Réponse Q-9 « Page en chiffres, lien web » (2026-11-22), `Recette.dc.html:64`, `:76` |
| RCT-05 | « Nos ajustements » est un texte unique et commun au carnet. | A écrit les ajustements, B les voit après synchro. | Recette | C | `docs/suivi.md`, écart 3, `Recette.dc.html:89-90` |
| RCT-06 | Fiche existante : « Supprimer » en haut à gauche, « Fermer » ramène à la liste des recettes, « Enregistrer » en violette ramène à la liste des recettes. | Fiche « Blanquette de seitan », « Enregistrer » → « Nos recettes ». | Recette | E | `Recette.dc.html:39-40`, `:122` |
| RCT-07 | Toucher la note choisie la retire ; une seule note à la fois. Retirer la note d'une recette existante grise « Enregistrer » (RCT-02). | Note Super, toucher Super → aucune note, « Enregistrer » désactivé. | Recette | U | `Recette.dc.html:135` |

## 7. Liens entre produits et recettes

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| LIE-01 | La fiche produit liste « Utilisé dans nos recettes » ; la fiche recette liste « Produits utilisés ». Chaque ligne montre le nom, une précision et la pastille de note, et ouvre la fiche liée. | Fiche « Blanquette de seitan » → ligne « Crème d'avoine cuisine », « Avoine du Nord · Crèmes », pastille Super ; toucher → fiche de ce produit. | Produit, Recette | U, E | `README.md:53`, `Produit.dc.html:95-101`, `Recette.dc.html:110-117` |
| LIE-02 | Un lien est visible des deux côtés. | Lier « Tofu fumé au bois de hêtre » depuis « Blanquette de seitan » → la fiche de ce produit liste « Blanquette de seitan ». | Produit, Recette | U | `README.md:53` ; données `Main.dc.html:117` |
| LIE-03 | « Lier une recette » et « Lier un produit » ouvrent une feuille de choix : une recherche, puis une liste aux mêmes lignes que les écrans de liste. On peut choisir plusieurs éléments. | Feuille ouverte, choix de deux produits, validation → deux nouvelles lignes sur la fiche, écrites à l'enregistrement de la fiche (LIE-07). | Feuille de choix (non dessiné) | U, E | `docs/suivi.md`, écart 9, `README.md:53`, `Produit.dc.html:103`, `Recette.dc.html:118` |
| LIE-04 | Dans la feuille de choix, les éléments déjà liés n'apparaissent pas ; la sélection se valide par un bouton en bas de la feuille. Proposition annoncée, non dessinée, non validée. | Recette déjà liée à 2 produits → la feuille liste les autres produits seulement. | Feuille de choix (non dessiné) | U | Choix de conception (2026-11-22) |
| LIE-05 | Un lien se retire par une croix sur sa ligne, sans confirmation. | Sur « Blanquette de seitan », toucher la croix de « Crème d'avoine cuisine » → lien retiré, les deux éléments restent. | Produit, Recette (croix non dessinée) | U, E | `docs/suivi.md`, écart 9, `README.md:53` |
| LIE-07 | Les liens ajoutés ou retirés sur une fiche s'écrivent avec « Enregistrer » ou « Ajouter au carnet », comme les autres champs, et comptent comme des modifications pour FPR-11 ; « Abandonner » les annule. On peut donc lier des produits à une nouvelle recette avant de l'ajouter (choix annoncé). | « Blanquette de seitan », croix de « Crème d'avoine cuisine », « Fermer » → boîte ; « Abandonner » → lien toujours présent. | Produit, Recette, Nouvelle recette | U, E | `docs/suivi.md`, écart 9 ; moment d'écriture : choix annoncé |
| LIE-06 | Supprimer un produit le retire des recettes qui l'utilisent ; supprimer une recette laisse ses produits dans le carnet. | Supprimer « Steak végétal au poivre » → absent de « Produits utilisés » de toutes les recettes. Supprimer « Blanquette de seitan » → ses produits restent dans la liste. | Produit, Recette | U, C | `ConfirmProduit.dc.html:26`, `ConfirmRecette.dc.html:26`, `docs/cadrage.md:219-220` |

## 8. Suppression

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| SUP-01 | Dans les deux listes, glisser une ligne vers la gauche révèle « Supprimer » sur fond `danger`. | Glisser « Nuggets de blé » → action « Supprimer » visible. | ListeGlisser, Recettes (non dessiné) | E | `README.md:59`, `docs/suivi.md`, écart 12, `ListeGlisser.dc.html:30-32`, `:61` |
| SUP-02 | Aucune suppression n'est immédiate : « Supprimer », depuis le glissement ou la fiche, ouvre toujours la boîte de confirmation. | Toucher « Supprimer » → boîte ouverte, produit encore dans la base. | ConfirmProduit, ConfirmRecette | U, E | `README.md:61` |
| SUP-03 | La boîte nomme l'élément réellement visé et dit ce qui sera effacé. Produit : « Supprimer ce produit ? », puis le nom, « Sa note, votre avis et sa photo seront effacés du carnet. Il sera aussi retiré des recettes qui l'utilisent. Cette action est définitive. » Recette : « Supprimer cette recette ? », puis le nom, « Sa source, vos ajustements et sa note seront effacés du carnet. Les produits qu'elle utilise restent dans le carnet. Cette action est définitive. » L'effacement de la photo stockée dans R2 est **en attente** : c'est un point ouvert de la synchro. | Glisser « Nuggets de blé » → la boîte nomme « Nuggets de blé. », pas un autre produit. | ConfirmProduit, ConfirmRecette | U, E | `docs/suivi.md`, écart 12, `ConfirmProduit.dc.html:25-26`, `ConfirmRecette.dc.html:25-26` ; photo R2 : `docs/cadrage.md:235`, `docs/suivi.md`, Parking, « Points ouverts de synchro » |
| SUP-04 | « Annuler », à gauche, ramène d'où l'on vient : à la liste après un glissement, à la fiche sinon. Rien n'est effacé. | Glissement puis « Annuler » → liste des produits, produit présent. | ConfirmProduit, ConfirmRecette | U, E | `docs/suivi.md`, écart 12, `README.md:61` ; périmé : `ConfirmProduit.dc.html:28` et `ConfirmRecette.dc.html:28` ramènent toujours à la fiche |
| SUP-05 | « Supprimer », à droite, pose une pierre tombale (`deleted_at`) et ramène à la liste. L'élément disparaît de la liste et de la recherche. | Confirmer → « Nos produits » sans « Nuggets de blé » ; recherche « Nuggets » → aucun résultat. | ConfirmProduit, ConfirmRecette | U, E | `ConfirmProduit.dc.html:29`, `ConfirmRecette.dc.html:29`, `docs/cadrage.md:217` |
| SUP-06 | Une fiche nouvelle (création, produit scanné, nouvelle recette) n'a pas de « Supprimer ». | Création à la main → aucun bouton « Supprimer ». | Creation, ScanInconnu, Nouvelle recette | U | `README.md:41`, `:47` |

## 9. Erreurs

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| ERR-01 | Code inconnu d'OFF : l'écran Erreur montre le titre « Le produit n'a pas pu être identifié » et une phrase qui ne parle pas de connexion (texte à fixer à l'implémentation). « Réessayer » rouvre le scan ; « Saisir le produit à la main » ouvre la création avec le code-barres gardé ; « Fermer » ramène à la liste des produits. | OFF répond « produit inconnu » pour le code X → Erreur ; « Saisir le produit à la main » → « Nouveau produit », produit enregistré avec le code X. | Erreur | U, E | `docs/suivi.md`, écart 2, `README.md:63`, `Erreur.dc.html:19`, `:25`, `:29-30` ; périmé : phrase `Erreur.dc.html:26` |
| ERR-02 | OFF indisponible, ou limite de 15 requêtes par minute atteinte : traité comme « pas de réseau » pour ce produit (SCN-09), nouvel essai plus tard. | OFF répond 5xx ou ne répond pas → parcours hors réseau. | Non dessiné | U | `docs/cadrage.md:259`, `:468` |
| ERR-03 | Échec de synchro ou d'envoi de photo : les données restent en file locale, un nouvel essai part tout seul, et un indicateur discret « non synchronisé » s'affiche. | Push en échec → ligne d'outbox gardée, indicateur visible ; push suivant réussi → indicateur retiré. | Non dessiné | U, C | `docs/cadrage.md:470` |
| ERR-04 | Erreur serveur (Worker ou D1), y compris l'erreur 1103 du plafond de dépense : traitée comme un échec de synchro, nouvel essai avec un délai croissant. Les valeurs du délai sont à fixer en phase 2. | Trois réponses 1103 de suite → trois délais strictement croissants entre les essais. | — | U | `docs/cadrage.md:93`, `:471` |

## 10. Synchronisation et convergence

Le modèle est décrit dans `docs/cadrage.md:188-245`. Les lignes C font tourner deux clients simulés contre le serveur, puis vérifient que les deux bases locales et le serveur sont identiques.

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| SYN-01 | Les identifiants (UUID) sont créés sur le téléphone ; on crée hors réseau sans attendre le serveur. | Création hors réseau → produit avec un UUID, visible tout de suite dans la liste. | — | U | `docs/cadrage.md:190` |
| SYN-02 | Chaque modification locale et sa ligne d'outbox sont écrites dans la même transaction : l'une n'existe jamais sans l'autre. | Échec simulé de l'écriture de l'outbox → la modification n'est pas appliquée. | — | U | `docs/cadrage.md:191` |
| SYN-03 | L'envoi (`POST /sync/push`) est idempotent par identifiant de mutation. La ligne d'outbox n'est supprimée qu'après l'accusé de réception. | Lot L envoyé, réponse perdue, L renvoyé → appliqué une seule fois côté serveur ; outbox vidée après le second accusé. | — | C, W | `docs/cadrage.md:192` |
| SYN-04 | La réception (`GET /sync/pull?since=<curseur>`) renvoie les changements dont la séquence serveur dépasse le curseur ; le client garde le dernier curseur reçu. | Curseur 41, serveur à 43 → changements 42 et 43, curseur 43. | — | C, W | `docs/cadrage.md:193` |
| SYN-05 | Même champ modifié des deux côtés : la valeur la plus récente selon l'horloge logique hybride (HLC) gagne, quel que soit l'ordre d'arrivée au serveur. Seule exception : la valeur venue d'OFF (SCN-14). | A change l'avis hors réseau à 10 h, envoie à 12 h ; B change l'avis à 11 h, envoie à 11 h → l'avis de B gagne. | — | C | `docs/cadrage.md:205-206`, `:211` |
| SYN-06 | Champs différents modifiés des deux côtés : les deux modifications sont gardées. | A met la note Bon, B met le lieu d'achat « Marché du samedi » → fiche avec Bon et « Marché du samedi ». | — | C | `docs/cadrage.md:211` |
| SYN-07 | L'HLC d'un appareil ne recule jamais, même si son horloge système recule. Propriété de conception : l'HLC n'est pas documentée dans les sources officielles consultées. | Horloge système reculée d'une heure → l'horodatage suivant reste supérieur au précédent. | — | U | `docs/cadrage.md:206` |
| SYN-08 | La suppression gagne sur une modification concurrente. | A supprime un produit, B change sa note en même temps → après synchro, produit supprimé chez A et chez B. | — | C | `docs/cadrage.md:218` |
| SYN-09 | Supprimer un produit pose une pierre tombale sur ses liens, côté client puis rejouée côté serveur. Un lien arrivé ensuite vers ce produit est ignoré et marqué supprimé. | A supprime P ; B, hors réseau, lie P à la recette R ; synchro → R ne liste pas P, chez A comme chez B. | — | C | `docs/cadrage.md:219` |
| SYN-10 | Supprimer une recette pose une pierre tombale sur ses liens ; les produits restent. | A supprime R liée à P → après synchro, P existe sans lien vers R. | — | C | `docs/cadrage.md:220` |
| SYN-11 | La synchro part au retour au premier plan, au retour du réseau et par une tâche périodique. | App remise au premier plan → un pull et un push partent. | — | N, M | `docs/cadrage.md:194-197` |
| SYN-12 | Une photo prise hors réseau est gardée en fichier local et mise en file (`photo_upload`). Elle part au retour du réseau ; la fiche reçoit la clé R2 après l'accusé, et le fichier local reste en cache. | Photo hors réseau → fiche affiche la photo locale ; retour du réseau → envoi, clé R2 enregistrée. | — | U, M | `docs/cadrage.md:228-234` |
| SYN-13 | Les données sont rangées par carnet ; un appareil ne reçoit que les données des carnets dont son utilisateur est membre. | Appareil du carnet K1 → aucun changement du carnet K2 dans le pull. | — | W | `docs/cadrage.md:15` |
| SYN-14 | Une requête sans jeton d'appareil valide est refusée. | `Authorization` absent ou jeton révoqué → refus, aucune donnée renvoyée. | — | W | `docs/cadrage.md:129` |

## 11. Open Food Facts

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| OFF-01 | La lecture passe par `GET /api/v3/product/{ean}.json`, en ne demandant que les champs utiles. | Code X → requête `/api/v3/product/X.json?fields=…`. | — | U | `docs/cadrage.md:257-258` |
| OFF-02 | Chaque requête porte un User-Agent « AppName/Version (ContactEmail) ». L'adresse de contact est une adresse dédiée à l'app, que tu crées. Elle est rangée dans un fichier local non versionné et injectée au build. Ni elle ni une adresse personnelle n'est écrite dans le dépôt : les tests utilisent une adresse fictive en `.invalid`. | Version 1.0.0, contact fictif `contact@exemple.invalid` → en-tête `User-Agent: VeganBrain/1.0.0 (contact@exemple.invalid)` (nom de l'app à confirmer). | — | U | `docs/cadrage.md:260` ; réponse Q-10 « Adresse dédiée à l'app » (2026-11-22) |
| OFF-03 | Correspondance des champs : `product_name` vers le nom, `brands` vers la marque, `categories` vers la catégorie. Choix du champ localisé, format des listes et cas des champs vides : à vérifier au client OFF, dans la documentation officielle. | — | — | U | `docs/cadrage.md:258` ; à vérifier |
| OFF-04 | Le taux « scan trouvé / scan inconnu » est journalisé sur le téléphone. | Deux scans trouvés, un inconnu → compteurs 2 et 1. | — | U | `docs/cadrage.md:275` |
| OFF-05 | Les données OFF ne sont pas redistribuées hors du carnet ; la mention ODbL est sur la fiche préremplie (FPR-13). Seule copie : l'image d'OFF gardée dans R2 pour les membres du carnet, sous CC BY-SA, avec sa mention (FPR-09). | — | — | M | `docs/cadrage.md:282-283` ; réponse Q-3 (2026-11-22) |

## 12. Accessibilité, thème et mise en page

| Id | Règle | Exemple entrée → sortie | Écran | Niveau | Source |
|---|---|---|---|---|---|
| A11-01 | Chaque paire exigée de `contrastes.md` (lignes « Texte (1.4.3) », « Contour ou état (1.4.11) » et « Retenu (focus) ») est recalculée depuis `tokens.json` et atteint son seuil (4,5:1 texte, 3:1 composants et focus). Ne comptent pas : les versions écartées marquées « ancien » (`contrastes.md:57-59`, écartées en `:85`) et les repères visuels non exigés (`:63-64`). Restent **en attente**, sans seuil annoncé comme atteint : `bordure` sur `surface` (`:60`, point 13), `bordure` sur `fond-haut` (`:61`, rattachée au point 13, qui ne nomme que `surface` dans `docs/suivi.md`, À trancher), la pastille choisie (`:62`, point 14), le texte secondaire au cœur des halos (`:44-45`, point 15). | `encre` sur `surface`, clair → 12,09:1 ≥ 4,5:1 (`contrastes.md:28`). | — | U | `docs/design/contrastes.md`, `docs/suivi.md`, À trancher, points 13 à 15 |
| A11-02 | Une note n'est jamais donnée par la couleur seule : icône partout, libellé dans les filtres, la fiche et le résultat de scan, nom accessible sur les pastilles de liste. | Pastille Pas bon → icône croix et nom accessible « Pas bon ». | Toutes | A | `README.md:8`, `:19` |
| A11-03 | Toute cible tactile mesure au moins 44 px. | Filtres à 44 px de haut, « Fermer » à 44 × 44. | Toutes | A | `README.md:75`, `Main.dc.html:50`, `Erreur.dc.html:19` |
| A11-04 | Tout élément interactif qui a le focus porte un contour de 2 px `encre-douce`, détaché de 2 px ; la recherche le porte sur son cadre ; « Supprimer » révélé au glissement a un contour intérieur `surface`. | Focus clavier sur un filtre → contour détaché visible. | Toutes | N, M | `README.md:17`, `docs/suivi.md`, Fait, point 20 |
| A11-05 | Si l'utilisateur réduit les animations, tout mouvement est coupé : la ligne glissée est directement décalée, les fondus et la montée des feuilles disparaissent. | Réduction des animations active, glissement → « Supprimer » visible sans animation. | Toutes | N | `README.md:71`, `ListeGlisser.dc.html:29`, `:32`, `ConfirmProduit.dc.html:17` |
| A11-06 | Grande taille de texte : les écrans restent lisibles et utilisables. Android : test automatisé avec `FontScale`. iOS : audit `.dynamicType`, et contrôle manuel des tailles, faute de méthode officielle pour forcer Dynamic Type dans un test. | Taille de police 200 % → aucun libellé de bouton tronqué. | Toutes | A, M | `docs/cadrage.md:383-384`, `:478` |
| A11-07 | La boîte de confirmation de suppression et la boîte « Abandonner les modifications ? » (FPR-11) sont des alertes modales, nommées par leur titre et décrites par leur texte. | Lecteur d'écran sur la boîte → « Supprimer ce produit ? » puis le texte. Lecteur d'écran sur la boîte d'abandon → « Abandonner les modifications ? » (texte exact à fixer, FPR-11) puis le texte. | ConfirmProduit, ConfirmRecette, boîte d'abandon (non dessinée) | A, N | `ConfirmProduit.dc.html:23` ; réponse Q-5 (2026-11-22) |
| A11-08 | Le thème suit le réglage du système, clair ou sombre, avec les valeurs `sombre` de `tokens.json`. | Système en sombre → fond `fond` sombre. | Toutes | N | `docs/suivi.md`, écart 6, `docs/cadrage.md:437`, `README.md:25` |
| A11-09 | Sur tablette, l'interface du téléphone s'étire, en portrait comme en paysage, sans élément coupé. | Tablette Android en paysage → listes et feuilles utilisables. | Toutes | E, M | `docs/suivi.md`, écart 7, `README.md:75`, `docs/cadrage.md:45` |
| A11-10 | Affichage bord à bord : l'en-tête s'étend sous la barre d'état, le contenu reste hors des zones réservées. | Téléphone à encoche → le fond de l'en-tête passe sous la barre d'état ; le titre commence après la zone réservée, sans être recouvert. | Toutes | N, M | `docs/cadrage.md:436`, `README.md:37` |
| A11-11 | Différence visible entre pastille choisie et pastille au repos (1,38:1) : **en attente du point 14**. | — | Main, Recettes, formulaires | A | `docs/suivi.md`, À trancher, point 14 |

## 13. Identifiants d'accessibilité communs

Les mêmes identifiants servent de `testTag` (Compose) et d'`accessibilityIdentifier` (SwiftUI), pour que les flux Maestro aient les mêmes sélecteurs sur les deux plateformes (`docs/cadrage.md:69`, `:390`). Forme choisie : `écran.élément`, en minuscules, sans accents, mots séparés par des tirets. Sont listés les éléments qu'un test pilote ou lit à l'écran : ceux des lignes E, et ceux que désignent des lignes U, A ou N. Un élément absent de la table se vise par son texte.

Sur Android, Maestro lit un `testTag` par le sélecteur `id:` quand la racine Compose pose `Modifier.semantics { testTagsAsResourceId = true }` (https://docs.maestro.dev/reference/selectors/core-selectors) : c'est fait dans `MainActivity.kt`, vérifié par `spikes/maestro/flows/onglets.yaml` (`spikes/maestro/README.md`, point 1). Sur iOS, `id:` vise l'`accessibilityIdentifier` d'après la même page : à vérifier à la tâche 14.

| Identifiant | Élément | Lignes |
|---|---|---|
| `onglets.produits`, `onglets.recettes` | Onglets de la barre | LIS-08 |
| `produits.ajouter` | « + » de Produits | LIS-09 |
| `produits.scanner` | Bouton de scan de l'en-tête | LIS-09 |
| `produits.recherche`, `recettes.recherche` | Champs de recherche | CHR-01 |
| `produits.filtre-tous`, `produits.filtre-super`, `produits.filtre-bon`, `produits.filtre-pas-bon`, `produits.filtre-a-noter` | Filtres des produits | CHR-05, CHR-06, CHR-08 |
| `recettes.filtre-toutes`, `recettes.filtre-super`, `recettes.filtre-bon`, `recettes.filtre-pas-bon` | Filtres des recettes | CHR-07, CHR-08 |
| `produits.ligne`, `recettes.ligne` | Lignes de liste ; l'élément visé se choisit ensuite par son texte | LIS-07, SUP-01 |
| `liste.supprimer` | « Supprimer » révélé au glissement | SUP-01 |
| `recettes.ajouter` | « + » de Recettes | LIS-10 |
| `scan.fermer`, `scan.saisir-nom`, `scan.saisir-code` | Fermer, « Saisir le nom à la main », « Saisir le code-barres » | SCN-11, SCN-13, SCN-16 |
| `saisie-code.champ`, `saisie-code.valider`, `saisie-code.message` | Saisie manuelle du code-barres et son message d'erreur | SCN-16 |
| `scan-connu.autre`, `scan-connu.voir-fiche` | Boutons de « Déjà dans le carnet » | SCN-05 |
| `fiche.fermer`, `fiche.supprimer`, `fiche.valider` | Fermer, Supprimer, bouton principal (« Enregistrer » ou « Ajouter au carnet ») de toutes les feuilles | FPR-06, FPR-08, FPR-12, RCT-02, RCT-04, RCT-06, RCT-07 |
| `fiche.encart-off`, `fiche.lien-odbl` | Encart et lien OFF du produit prérempli | SCN-07, FPR-13 |
| `fiche.encart-hors-reseau` | Encart du produit scanné hors réseau | SCN-09 |
| `fiche.nom`, `fiche.avis` | Champs nom et avis | FPR-06, FPR-11, RCT-02 |
| `fiche.photo`, `fiche.photo-changer`, `fiche.photo-prendre`, `fiche.photo-choisir`, `fiche.mention-photo` | Photo, ses boutons, mention « Photo Open Food Facts, CC BY-SA » | FPR-09, FPR-10 |
| `fiche.rappel-prevu`, `fiche.rappel-message`, `fiche.rappel-reglages` | Ligne « Rappel prévu », message « notifications refusées » et son lien | RAP-10, RAP-12 |
| `fiche.lien-ligne` | Ligne d'un lien ; l'élément visé se choisit par son texte | LIE-01, LIE-05, LIE-07 |
| `scan-connu.titre`, `scan-connu.note` | Titre et note (ou pastille À noter) de « Déjà dans le carnet » | SCN-04, SCN-06 |
| `message.deja-note` | Message « déjà noté » à l'ouverture depuis le rappel | RAP-07 |
| `fiche.note-super`, `fiche.note-bon`, `fiche.note-pas-bon` | Boutons de note | FPR-04, RCT-01 |
| `fiche.rappel-ce-soir`, `fiche.rappel-demain`, `fiche.rappel-3-jours`, `fiche.rappel-aucun` | Délais du rappel | RAP-02, RAP-04, RAP-11 |
| `fiche.source-insta`, `fiche.source-livre`, `fiche.source-adaptee` | Sources de recette | RCT-01 |
| `fiche.lier`, `fiche.lien-retirer` | « Lier… », croix d'un lien | LIE-03, LIE-05, LIE-07 |
| `fiche.page`, `fiche.lien-post`, `fiche.lien-message` | Champs « Page » et « Lien du post », message d'un lien invalide | RCT-04 |
| `choix.recherche`, `choix.ligne`, `choix.valider` | Feuille de choix ; une ligne se choisit par son texte | LIE-03 |
| `confirmation.annuler`, `confirmation.supprimer` | Boîte de confirmation | SUP-02 à SUP-05 |
| `abandon.continuer`, `abandon.abandonner` | Boîte « Abandonner les modifications ? » | FPR-11 |
| `erreur.reessayer`, `erreur.saisir`, `erreur.fermer` | Écran Erreur | ERR-01 |
| `camera-refusee.reglages`, `camera-refusee.saisir-code` | Écran caméra refusée | SCN-12 |

## 14. Relevés à la contre-revue, à fixer en phase 2

Relevés par les deux relectures du 2026-11-22. Ce ne sont pas encore des règles. Chaque point sera tranché, et la ligne citée complétée, avant d'écrire le test correspondant.

1. Liens : doublon du même lien créé hors réseau sur les deux téléphones ; relier après avoir délié, face à « la suppression gagne » (`docs/cadrage.md:218`) ; lien vers une recette supprimée entre-temps. (LIE-07, SYN-09)
2. Deux produits créés hors réseau avec le même code-barres, un sur chaque téléphone. (SCN-10, SYN-01)
3. Rappels : « Ce soir » choisi avant 18 h mais fiche enregistrée après 19 h ; changement d'heure ; changement de fuseau ; téléphone éteint à 19 h. (RAP-03, RAP-11)
4. Préremplissage différé : déclencheur quand OFF reste indisponible, persistance de la file, produit supprimé ou complété entre-temps, photo prise hors réseau, formulaire fermé sans rien taper. (SCN-09, SCN-14)
5. Recette : lien invalide resté dans une source qu'on quitte ; champs de l'ancienne source gardés ou effacés quand la source change. (RCT-03, RCT-04)
6. Sous-titres de liste quand la marque, la catégorie, la page ou le lien sont vides ; définition d'un produit « issu d'un scan ». (LIS-01, LIS-04, LIS-05)
7. Date de création de LIS-06 : horloge qui la donne, champ jamais modifié. Départage de l'HLC à égalité, avance à la réception, horloge très décalée (`docs/cadrage.md:206`). (LIS-06, SYN-05, SYN-07)
8. Ouverture depuis le rappel : hors réseau ; produit supprimé par l'autre personne (ligne C) ; limite iOS de 64 notifications en attente (`docs/cadrage.md:342`). (RAP-07, RAP-08)
9. RAP-04 et RAP-12 : « Pas de rappel » déclenche-t-il la demande d'autorisation ; peut-on dépresser un délai choisi.
10. Scan : attente d'OFF et délai maximal, même code resté dans le cadre, autres formats lus, appareil sans scanner compatible (`docs/cadrage.md:41`), message pour « 12345 » ou « 0000000000000 », rôle de « Fermer » sur « Déjà dans le carnet » (`ScanConnu.dc.html:19`). Que le préfixe 2 des codes d'exemple soit réservé à l'usage interne : non vérifié. (SCN-05, SCN-16)
11. ERR-04 : erreurs à ne pas rejouer (jeton révoqué), plafond du délai, remise à zéro après un succès.
12. Synchro : ordre des envois dans un lot ; premier pull et pagination ; ordre push puis pull ; tâche périodique sans garantie sur iOS (`docs/cadrage.md:197`). (SYN-04, SYN-09, SYN-11)
13. SYN-13 : étendre les lignes W au push, à l'envoi de photo vers R2 et à la validation des données côté serveur.
14. Accessibilité : annonce des messages d'erreur par le lecteur d'écran ; critères qualitatifs à mesurer (A11-06, A11-09, A11-10) ; taille de cible de 48 dp que demanderait le vérificateur Android : non vérifié. (A11-03)
15. RCT-04 : chiffres ASCII seulement, zéros en tête, longueur maximale, espaces autour d'un texte collé.
16. Retours : « Fermer » d'une fiche ouverte depuis « Déjà dans le carnet » ou depuis une ligne de lien ; la fiche garde-t-elle ses modifications non enregistrées après « Annuler » dans la boîte de suppression. (FPR-12, SUP-04)
17. Format du journal d'OFF-04 ; délais d'ERR-04 ; textes encore « à fixer » (LIS-04, LIS-11, RAP-07, RAP-10, RAP-12, SCN-09, SCN-16, RCT-04).

## Questions ouvertes

Posées une par une, dans cet ordre. Chaque réponse complète les lignes citées.

| Question | Lignes |
|---|---|
| ~~Q-1. Comment l'enregistrement d'une recette sans note est empêché ; champs obligatoires du produit et de la recette.~~ Tranchée : bouton grisé ; produit : nom ; recette : nom, source, note. | FPR-06, RCT-02, RCT-07 |
| ~~Q-2. Scan hors réseau : écran affiché, pose du rappel, champs remplis au retour du réseau.~~ Tranchée : formulaire vide, nom facultatif, seuls les champs vides préremplis. | SCN-09, SCN-14, SCN-15 |
| ~~Q-3. Photo du produit scanné : photo d'OFF ou photo prise sur place.~~ Tranchée : image d'OFF proposée, copiée dans R2, avec la mention CC BY-SA. | FPR-09, OFF-05 |
| ~~Q-4. Heure de chaque délai de rappel, « Ce soir » choisi tard, autorisation refusée.~~ Tranchée : 19 h ; « Ce soir » grisé dès 18 h ; refus → message vers les réglages. | RAP-03, RAP-11, RAP-12 |
| ~~Q-5. « Fermer » avec des modifications non enregistrées.~~ Tranchée : boîte « Abandonner les modifications ? ». | FPR-11 |
| ~~Q-6. Produit sans note : affichage au scan, bloc de rappel sur sa fiche.~~ Tranchée : pastille « À noter » au scan ; bloc de rappel modifiable sur la fiche. | SCN-06, RAP-10 |
| ~~Q-7. Ordre de tri des listes.~~ Tranchée : « À noter » d'abord, puis du plus récent au plus ancien. | LIS-06 |
| ~~Q-8. Accents et casse dans la recherche.~~ Tranchée : casse et accents ignorés. | CHR-04 |
| ~~Q-9. Validation du champ « Page » et du lien Instagram.~~ Tranchée : page en chiffres, lien en `http(s)://`. | RCT-04 |
| ~~Q-10. Adresse de contact du User-Agent OFF.~~ Tranchée : adresse dédiée à l'app, hors dépôt. | OFF-02 |
| ~~Q-11. Rôle du bouton « Capturer » en mode code-barres.~~ Tranchée : il devient « Saisir le code-barres ». | SCN-03, SCN-12, SCN-16 |
| ~~Q-12. Produit resté sans nom : « Enregistrer » sur sa fiche.~~ Tranchée : actif tant que le nom était déjà vide à l'ouverture. | FPR-06, SCN-15 |
