# Inventaire des écrans

Dernière mise à jour : 2026-11-22. Relu par le sous-agent relecteur ; ses corrections ont été intégrées après que j'ai relu leurs preuves.

Sources :
- maquettes : artifact Design https://claude.ai/artifact/JwYfF1ykTi6mgnT9gJRC3g, copie locale dans `docs/maquettes/` (index `canvas.json`, un fichier `.dc.html` par écran de 390 × 844) ;
- design system « Carnet vegan » : artifact https://claude.ai/artifact/PYW9YG1w1FCwhYomsFtSXW, version 1795333659-1878, copie dans `docs/design/` (`tokens.json` sha256 6281bf84…, `README.md` sha256 fb0b5016…).
- renvois de ligne : `docs/design/README.md:n` désigne la version du commit 116b417 (sha256 fb0b5016) ; plusieurs passages cités ont été réécrits à la tâche 2c. `docs/design/tokens.json:38` désigne la copie d'origine (sha256 6281bf84), qui n'a jamais été commitée : dans le commit 116b417, l'usage de `filigrane` est à la ligne 46.

J'ai aussi lu dans l'artifact `design-system.json`, `assets/Filigranes/README.md` et `components/Cover/preview.html`, sans les copier : ils n'ajoutent aucune règle d'écran. Les filigranes sont « conservés ici mais plus utilisés sur les écrans pour l'instant » (`assets/Filigranes/README.md` de l'artifact), ce que confirment `docs/design/README.md:28` et `docs/design/tokens.json:38`.

Les 12 fichiers ont été lus en entier. Les numéros de ligne renvoient aux fichiers de `docs/maquettes/`.

## Écrans

| N° | Fichier | Titre (`<title>`, ligne 5) | Contenu | Va vers |
|---|---|---|---|---|
| 1 | Main.dc.html | Nos produits | Recherche « Chercher dans nos notes » (l. 42) et bouton de scan. Filtres Tous, Super, Bon, Pas bon (l. 128-131). Chaque ligne : vignette, nom, « marque · catégorie », pastille de note. Un produit en attente affiche la pastille « À noter » (l. 80) et « scanné en magasin » à la place de la catégorie (l. 109). Barre d'onglets. | Scan (l. 44, puis l. 98 : « + »), Produit (l. 56), Recettes (l. 94) |
| 2 | Scan.dc.html | Scanner ou photographier un produit | Titre visible « Identifier un produit » (l. 27). Sélecteur Code-barres / Photo (l. 31-32), code par défaut (l. 65). Consigne en mode code (l. 39). Consigne en mode photo : « Le nom et la marque sont proposés » (l. 46). Lien « Saisir le nom à la main » (l. 48). | Main (l. 24), Creation (l. 48), ScanConnu (l. 51 : capture en mode code, toujours), ScanInconnu (l. 56 : capture en mode photo, seulement) |
| 3 | ScanConnu.dc.html | Scan en magasin : produit déjà noté | « Déjà dans le carnet » (l. 22). Aperçu caméra. Carte : nom, « marque · catégorie », « Notre note » en grand (Super), « Notre avis » en un seul texte (l. 46-47). | Main (l. 19), Scan « Scanner un autre produit » (l. 51), Produit « Voir ou modifier la fiche » (l. 52) |
| 4 | ScanInconnu.dc.html | Scan en magasin : produit à compléter | Feuille « Produit scanné ». Encart « Reconnu par le scan… la reconnaissance peut se tromper » (l. 47). Photo avec « Changer la photo » (l. 52). Nom, marque, catégorie. Note « · facultatif » (l. 71), vide par défaut (l. 119). Bloc « Me rappeler de le noter » : Ce soir, Demain, Dans 3 jours, Pas de rappel (l. 87-94 et 132). Il s'affiche sans condition, avec « Ce soir » par défaut (l. 131). « Notre avis » (l. 96). « Acheté chez ». | Scan (l. 39, Fermer), Main (l. 107, « Ajouter au carnet ») |
| 5 | Produit.dc.html | Fiche produit | Feuille, « Supprimer » en haut à gauche (l. 39). Photo avec « Changer la photo » (l. 49). Nom, marque, catégorie. Note : Super dans la démo (l. 119), retirée si on touche de nouveau le même choix (l. 120). « Notre avis » (l. 85), « Acheté chez ». « Utilisé dans nos recettes » (l. 95), « Lier une recette » (l. 103). | ConfirmProduit (l. 39), Main (l. 40 Fermer, l. 107 « Enregistrer »), Recette (l. 96) |
| 6 | Creation.dc.html | Créer un produit à la main | Feuille « Nouveau produit » (l. 44). Zone vide de photo avec « Prendre une photo » et « Choisir » (l. 45-51). Nom, marque, catégorie facultative. Note « facultatif, vous pourrez noter plus tard » (l. 70), vide par défaut (l. 110). « Notre avis » (l. 87), « Acheté chez » (l. 92). Pas de rappel, pas de champ code-barres (données l. 107). | Scan (l. 39, Fermer), Main (l. 98, « Ajouter au carnet ») |
| 7 | ListeGlisser.dc.html | Liste : glisser pour supprimer | Variante d'état de Main : la ligne « Râpé végétal façon emmental » (l. 134) est glissée de −104 px (l. 30, 32) et révèle « Supprimer ». Aucun lien ne mène à cet écran. | ConfirmProduit (l. 61), Produit (l. 63), Scan, Main, Recettes |
| 8 | ConfirmProduit.dc.html | Confirmer la suppression d'un produit | Fond « Fiche produit » (l. 21), boîte `alertdialog` sur voile à 45 % (l. 22-23). Titre « Supprimer ce produit ? ». Texte (l. 26) : « Steak végétal au poivre », « Sa note, votre avis et sa photo seront effacés », « retiré des recettes qui l'utilisent », « définitive ». | Produit « Annuler » (l. 28), Main « Supprimer » (l. 29) |
| 9 | Erreur.dc.html | Erreur : produit non identifié | « Le produit n'a pas pu être identifié » (l. 25). « Vérifiez la connexion du téléphone, ou réessayez avec plus de lumière » (l. 26). **Aucun lien ne mène à cet écran.** Recherche de `Erreur.dc.html` dans les 12 fichiers : 0 résultat. | Main (l. 19, Fermer), Scan « Réessayer » (l. 29), Creation « Saisir le produit à la main » (l. 30) |
| 10 | Recettes.dc.html | Nos recettes | Recherche « Chercher une recette » (l. 38). Filtres Toutes, Super, Bon, Pas bon (l. 125-130). Chaque ligne : nom, icône de source et texte (l. 52-61), pastille de note. Pas de variante de glissement. Barre d'onglets. | Recette (l. 48 depuis une ligne, l. 93 avec « + »), Main (l. 85) |
| 11 | Recette.dc.html | Fiche recette | Feuille, « Supprimer » (l. 39). Nom. « D'où vient-elle ? » : Insta, Livre ou Adaptée (l. 52-58 et 148). Selon la source : lien du post (l. 63-64), livre et page (l. 71-76), recette d'origine (l. 83-84). « Nos ajustements » (l. 89). « Notre note » sans mention « facultatif » (l. 93) : Super par défaut (l. 134), retirée si on touche de nouveau le même choix (l. 135). « Produits utilisés » (l. 110), « Lier un produit » (l. 118). Pas de photo. | ConfirmRecette (l. 39), Recettes (l. 40 Fermer, l. 122 « Enregistrer »), Produit (l. 111) |
| 12 | ConfirmRecette.dc.html | Confirmer la suppression d'une recette | « Supprimer cette recette ? ». Texte (l. 26) : « Sa source, vos ajustements et sa note seront effacés », « Les produits qu'elle utilise restent dans le carnet », « définitive ». | Recette « Annuler » (l. 28), Recettes « Supprimer » (l. 29) |

## Écarts entre maquettes, design system et décisions

Ces écarts sont à trancher avec toi avant d'écrire la table dorée. Ils sont classés par effet sur la V1.

1. **Mode Photo, reconnaissance et scan inconnu.**
   - Le cadrage validé dit : « V1 sans reconnaissance par photo. Le scan se fait par code-barres » (`docs/cadrage.md:304-306`). L'OCR est reporté en V2 (`docs/suivi.md`, Parking).
   - Les maquettes contredisent ce choix :
     - Scan propose un mode Photo qui « propose le nom et la marque » (Scan.dc.html:46) ;
     - ScanInconnu dit « la reconnaissance peut se tromper » (ScanInconnu.dc.html:47) ;
     - le README du design system parle de « champs préremplis par la reconnaissance » (`docs/design/README.md:38`).
   - **Conséquence** : dans les maquettes, un scan de code-barres mène toujours à ScanConnu (Scan.dc.html:51). Seul le mode Photo mène à ScanInconnu (l. 56). Si l'on retire le mode Photo, aucun chemin ne va plus d'un code-barres inconnu au formulaire « produit à compléter ». Il faut décider ce chemin.

2. **Pas de réseau et scan sans résultat.**
   - Le cadrage prévoit qu'hors réseau, le scan soit enregistré (code-barres, photo, rappel) et que les champs soient préremplis au retour du réseau (`docs/cadrage.md:276-283` et `:435`). Les maquettes ne dessinent pas ce cas.
   - L'écran Erreur dit au contraire « Vérifiez la connexion du téléphone » (Erreur.dc.html:26). Et aucun lien n'y mène.
   - Pour un « scan sans résultat », le cadrage prévoit un « Formulaire vide avec le code-barres » (`docs/cadrage.md:436`). Erreur mène à Creation (Erreur.dc.html:30), qui ne garde pas le code-barres (Creation.dc.html:107).
   - Deux états du cadrage ne sont pas dessinés :
     - caméra refusée, avec saisie manuelle du code-barres (`docs/cadrage.md:438`). Scan.dc.html:48 propose de saisir le nom, pas le code ;
     - l'indicateur « non synchronisé » (`docs/cadrage.md:439`).

3. **Avis.**
   - La décision validée est « un avis par personne, une note commune » (`docs/cadrage.md:14`).
   - Les maquettes n'ont qu'un champ « Notre avis » : Produit.dc.html:85, Creation.dc.html:87, ScanInconnu.dc.html:96, ScanConnu.dc.html:46. ConfirmProduit dit « votre avis » (ConfirmProduit.dc.html:26).
   - Le design system lui-même ne prévoit qu'un avis (`docs/design/README.md:38`).
   - **Question ouverte** : faut-il aussi un texte par personne pour « Nos ajustements » (Recette.dc.html:89) ? Le cadrage citait « avis, ajustements » comme textes longs à risque de conflit (`docs/cadrage.md:204`), mais la décision ne vise que l'avis.

4. **Filtre « À noter ».**
   - Le cahier des charges cite « À noter » comme filtre.
   - Les maquettes n'ont que les filtres Tous, Super, Bon, Pas bon (Main.dc.html:128-131). Le design system en fait une règle : « Les deux listes ont les mêmes filtres, par note », avec trois icônes (`docs/design/README.md:58`).
   - « À noter » n'existe que comme pastille, réservée à « un produit ajouté sans note » (`docs/design/README.md:17`, Main.dc.html:80).
   - Côté recette :
     - la note n'est pas marquée facultative (Recette.dc.html:93) ;
     - Super est sélectionné par défaut (l. 134).

5. **Rappel.**
   - Le cadrage dit que le rappel est « annulé dès que le produit est noté » (`docs/cadrage.md:350`) et que l'autorisation est « demandée au moment où l'on choisit « me rappeler » » (`:353`).
   - Dans ScanInconnu :
     - le bloc de rappel s'affiche même quand une note est choisie (ScanInconnu.dc.html:87-94) ;
     - « Ce soir » est présélectionné (l. 131), ce qui déclencherait la demande d'autorisation sans choix explicite.
   - Creation ne propose aucun rappel, même sans note.

6. **Mode sombre.** `docs/design/tokens.json` n'a qu'un thème, « clair ». Le mode sombre est une exigence.

7. **Tablette.** Toutes les maquettes font 390 × 844. Aucune ne montre la disposition liste/détail.

8. **Mention ODbL.** Le cadrage prévoit la mention « Données Open Food Facts, ODbL », avec un lien, sur la fiche préremplie (`docs/cadrage.md:272`). J'ai cherché « ODbL » et « Open Food » dans les 12 maquettes : aucun résultat.

9. **Sélecteur de lien.** Les boutons « Lier une recette » (Produit.dc.html:103) et « Lier un produit » (Recette.dc.html:118) n'ouvrent aucun écran. Le sélecteur n'est pas dessiné.

10. **Nouvelle recette.**
    - Le « + » de Recettes ouvre la fiche Recette (Recettes.dc.html:93), remplie avec les données de démonstration et avec « Supprimer » (Recette.dc.html:39).
    - Le design system réserve « Supprimer » à une « fiche existante » (`docs/design/README.md:36`). Il manque donc une fiche « nouvelle recette », comme Creation pour les produits.

11. **Recherche.** Les deux listes ont un champ de recherche (Main.dc.html:42, Recettes.dc.html:38). Les exigences que tu m'as données n'en parlent pas. C'est un ajout des maquettes, à garder ou non.

12. **Points mineurs, à corriger dans les maquettes ou à préciser dans la table dorée.**
    - Suppression par glissement :
      - depuis le glissement (ListeGlisser.dc.html:61), « Annuler » ramène à la fiche produit (ConfirmProduit.dc.html:28) au lieu de la liste ;
      - la boîte nomme « Steak végétal au poivre » (l. 26), alors que la ligne glissée est « Râpé végétal façon emmental » (ListeGlisser.dc.html:134).
    - Le glissement n'est pas dessiné sur la liste des recettes (Recettes.dc.html:47-79). Le design system le prévoit « dans une liste » (`docs/design/README.md:50`).
    - Aucun style de focus dans les 12 fichiers (recherche « focus » : 0 résultat), et la recherche a `outline: 0` (Main.dc.html:42). Le design system décrit pourtant un état actif pour les champs (`docs/design/README.md:16`).
