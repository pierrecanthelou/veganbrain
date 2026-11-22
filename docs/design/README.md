# Carnet vegan

Le carnet d'un couple vegan pour noter les produits et les recettes testés. En thème clair, des verts pastel en dégradés doux et un texte vert sombre. Dans les deux thèmes, deux couleurs de section : potiron pour les produits, violette pour les recettes. Un thème sombre reprend les mêmes noms de tokens (voir « Thème sombre »).

## Principes

- **Doux, pas fade.** Les fonds sont pastel en clair, très sombres en sombre ; le texte reste en `encre`, la couleur la plus contrastée avec eux, pour rester lisible.
- **Trois notes, jamais par la couleur seule.** Super, Bon, Pas bon ont chacune une couleur et une icône. Dans les listes, la note est une pastille ronde de 36 px avec l'icône seule, sans mot ; les filtres, la fiche et le résultat de scan gardent le libellé et servent de légende.
- **Le décor reste derrière.** Les dégradés sont lents et discrets ; ils ne passent jamais sous un texte au point de le gêner.

## Couleurs

- Le fond couvre tout l'écran, en-tête compris : un dégradé vertical de `fond-haut` à `fond-bas`, un halo de section dans l'angle bas, du côté de son onglet (`potiron` à 32 % en bas à gauche sur les écrans Produits ; en bas à droite sur les écrans Recettes, un violet plus saturé que `violette`, rgb 155 120 235 à 42 %, parce que `violette` s'éteint sur le vert), pour accompagner l'onglet sélectionné, et un halo `menthe` à 55 % dans l'angle haut opposé (ces opacités sont celles du thème clair ; celles du sombre sont dans `color.derived`) ; `fond` sert de repli. `surface` porte notamment les champs, les cartes, les lignes de lien, la boîte de confirmation, le bouton de scan, le bouton « Changer la photo » et la pastille « À noter » ; à 60 %, c'est le verre de l'en-tête. Les pastilles au repos ont leur propre fond (voir plus bas) et la barre d'onglets est une piste teintée.
- Sur `menthe`, `potiron`, `violette` et les trois couleurs de note, le texte et les icônes sont en `encre-sur-couleur` : même valeur qu'`encre` en clair, mais `encre-sur-couleur` reste foncé en sombre, parce que ces fonds restent clairs. `encre-douce` sert au texte secondaire, sur `fond`, `surface` et `vignette` seulement (exceptions à trancher, `docs/suivi.md` point 19).
- Deux couleurs de section, les seules couleurs vives du thème : `potiron` pour Produits, `violette` pour Recettes. Chacune marque l'action principale de sa section (cercle « + », Enregistrer), une seule fois par écran, texte en `encre-sur-couleur`. `potiron-clair` et `violette-clair` en sont les versions pastel.
- `menthe` marque l'élément sélectionné (filtre, source) et les actions secondaires. Les pastilles de filtre et de note n'ont pas de contour, au repos comme sélectionnées ; seule la pastille de statut « À noter » des lignes de liste en porte un, en pointillés (voir plus bas) ; le filtre « À noter » suit la règle des autres filtres. Le focus ajoute son propre contour. Au repos : fond `repos` (`encre` à 7 %), libellé `encre-douce`. Sélectionnée : fond plein, libellé `encre-sur-couleur`, de même graisse qu'au repos ; un filtre de note prend la couleur de sa note (`note-super`, `note-bon`, `note-pas-bon`), sur Produits comme sur Recettes ; « Tous », « Toutes » et les autres pastilles prennent `menthe`. Aucun contour en `encre`, nulle part. Les grands boutons de note et le choix de source des fiches suivent la même règle que les pastilles, avec en plus une ombre douce sous l'élément sélectionné (2 px de décalage, 10 px de flou, `ombre-douce` : `ombre` à 18 % en clair, à 50 % en sombre). La barre d'onglets est une piste arrondie de 48 px de haut, sans contour, teintée sur toute sa largeur par la couleur de la section non sélectionnée, presque transparente (`violette` à 18 % quand on est sur Produits, `potiron` à 16 % quand on est sur Recettes). Le bouton Ajouter est un simple « + » dans un cercle de 60 px, fixe au centre, plus grand que la capsule (48 px) qu'il coiffe. Une capsule englobe l'onglet en cours et ce cercle : elle s'étend vers la gauche sur Produits (`potiron-clair`, cercle `potiron`), vers la droite sur Recettes (`violette-clair`, cercle `violette`), et dit ainsi ce qu'on ajoute. L'onglet en cours est libellé en `encre` gras ; l'autre reste hors de la capsule, en `encre-douce`, sur la piste. Au changement d'onglet, la capsule glisse d'un côté à l'autre en 0,45 s en changeant de couleur, le cercle change de couleur sans bouger ; sans animation si l'utilisateur a demandé de réduire les mouvements.
- **Focus.** Tout élément interactif qui a le focus, et le champ en cours de saisie, porte un contour de 2 px en `encre-douce`, détaché de l'élément par un espace de 2 px, sans halo, dans les deux thèmes. Il s'ajoute au contour éventuel de l'élément sans le remplacer. Détaché, il ne touche que le fond autour de l'élément : au moins 3,6:1 sur tous les fonds mesurés, sauf sur l'encart du scan en sombre s'il garde 55 % (2,4:1, à trancher), pour 3:1 demandé par le critère WCAG 1.4.11 (`contrastes.md`). Il remplace l'ancien contour fin `note-super` et son halo `menthe`, trop pâles en clair (1,99:1 sur `surface`).
- Un produit ajouté sans note (scanné en magasin, à noter plus tard) porte, sur sa ligne de liste, une pastille « À noter » : fond `surface`, contour `encre-douce` en pointillés, icône horloge. Un produit scanné hors réseau est gardé en « À noter » avec son code-barres et un rappel ; il est prérempli au retour du réseau.
- `note-super`, `note-bon`, `note-pas-bon` ne servent qu'aux notes (exceptions à trancher, `docs/suivi.md` point 19). Elles diffèrent en clarté, et l'icône (étoile, coche, croix) accompagne toujours la couleur.
- `bordure` trace le contour de 1 px des champs (recherche comprise), des cartes, des lignes de lien, du bouton de scan et du bouton « Voir ou modifier la fiche », tous à fond `surface`. Les pastilles, la barre d'onglets, le bouton « Changer la photo » et les boutons Enregistrer, Lier, Annuler et ceux de l'écran d'erreur n'en ont pas. Les lignes horizontales des listes sont des filets gravés : un trait de 1 px en `bordure`, doublé juste en dessous d'un trait de 1 px en `separateur` ; le premier trait est le plus sombre en clair, le plus clair en sombre. `vignette` remplit les emplacements de photo.
- `filigrane` est un ton plus sombre que `fond` en clair (plus clair en sombre), réservé aux illustrations. Illustrations et filets de liste partagent le même principe gravé à deux traits, mais pas le même premier trait (`filigrane` pour les unes, `bordure` pour les autres).

## Thème sombre

Les tokens gardent leurs noms ; `tokens.json` donne à chacun une valeur `sombre`. Les fonds passent en tons très sombres et le texte en clair. `menthe`, les couleurs de section et les couleurs de note restent claires, avec un texte `encre-sur-couleur` foncé. `ombre` (noir en sombre) colore le voile et les ombres. Les opacités tokenisées sont dans `color.derived`, avec une valeur par thème. D'autres n'existent encore que dans les maquettes, en clair (voile et ombre des fiches, poignée, ombres de « Changer la photo » et de la boîte de confirmation, encart du scan) : leur valeur en sombre est à trancher (`docs/suivi.md`). Les contrastes, mesurés sur le fond réellement rendu, sont dans `contrastes.md` ; en sombre, toutes les paires exigées par WCAG AA atteignent leur seuil, sauf l'encart du scan s'il garde 55 % (son texte et le focus du lien qu'il porte).

## Typographie

Space Grotesk pour les titres (`titre-ecran`, `titre-fiche`, `titre-barre`), Inter pour le reste (`corps`, `corps-fort`, `secondaire`, `etiquette`). Les deux viennent de Google Fonts.

## Illustrations

Les écrans ne portent plus d'illustration de fond. Le groupe d'assets **Filigranes** garde les huit dessins gravés (trait `filigrane` doublé d'un trait `separateur`), au cas où ils resserviraient.

## En-tête

Chaque écran s'ouvre sur un panneau en verre dépoli (`surface` à 60 %, flou de 18 px) qui part du bord haut de l'écran, avec `space-4` au-dessus du titre. Dans l'app, la zone réservée du téléphone (barre d'état, encoche) s'ajoute à cette marge et le panneau s'étend dessous ; la maquette ne la dessine pas, et se termine par un bord droit, sans angles arrondis, souligné d'un filet gravé (`bordure` puis `separateur`). Il laisse passer le dégradé du fond et les listes défilent dessous. Il porte le titre et, sur les écrans de liste, la recherche et les filtres. Sur les deux écrans de liste, un petit trait de 36 × 5 px sous le titre reprend la couleur de la section. En bas des écrans de liste, il n'y a ni pied ni séparation : la liste elle-même devient progressivement transparente sur une quarantaine de pixels juste au-dessus de la barre d'onglets, et laisse voir le fond de l'écran tel quel. Rien n'est peint par-dessus le dégradé.

## Fiches

Une fiche (produit, recette) n'est pas un écran à part : c'est une feuille qui monte du bas en 0,42 s et s'arrête à 72 px du haut, angles hauts en 28 px, avec une ombre portée vers le haut. L'écran d'où l'on vient reste visible au-dessus, assombri par un voile `ombre` à 30 % (valeur en sombre pas encore fixée). La feuille n'a pas d'en-tête : une poignée centrée, une croix de fermeture en haut à droite et, sur une fiche existante, « Supprimer » en `danger` en haut à gauche, loin du bouton principal. Le contenu défile ; le bouton principal reste fixe en bas.

Un seul formulaire produit sert à trois usages : la fiche d'un produit existant (champs remplis, modifiables), le résultat d'un scan dont le code-barres n'est pas encore dans le carnet (champs préremplis par Open Food Facts, à vérifier) et la création à la main (champs vides). Au scan, un encart `menthe` à 55 % en clair (valeur en sombre à trancher) dit « Trouvé dans Open Food Facts, vérifiez » et porte le lien « Données Open Food Facts, ODbL ». Champs, dans l'ordre : photo, nom, marque et catégorie, note, avis, lieu d'achat. L'avis est unique et commun au carnet.

Tant qu'aucune note n'est choisie, au scan comme à la création à la main, un bloc propose un rappel. Aucun délai n'est présélectionné ; l'autorisation de notifier est demandée au premier choix ; le rappel est annulé dès que le produit est noté.

Le « + » de Recettes ouvre une feuille « Nouvelle recette » vide : sans « Supprimer », sans source ni note présélectionnée, avec le bouton « Ajouter au carnet ». Une recette s'enregistre toujours avec une note.

Photo : quand il y en a une, elle occupe un bloc de 150 px de haut avec un bouton « Changer la photo » posé en bas à droite. Quand il n'y en a pas, une zone en pointillés `note-super` propose « Prendre une photo » (`menthe`) et « Choisir » (fond `repos`).

## Liens entre produits et recettes

La fiche recette liste les « Produits utilisés », la fiche produit liste « Utilisé dans nos recettes ». Chaque lien est une ligne sur `surface` avec le nom, une précision et la pastille de note, et ouvre la fiche liée. Un bouton « Lier un produit » ou « Lier une recette », à fond `repos`, suit la liste. Il ouvre une feuille de choix : une recherche, puis une liste aux mêmes lignes que les écrans de liste ; on peut lier plusieurs éléments. Un lien se retire par une croix sur sa ligne, sans confirmation.

## Danger, suppression et erreur

`danger` est réservé à la suppression. Trois règles :

- Dans une liste, produits comme recettes, glisser une ligne vers la gauche révèle l'action « Supprimer » sur fond `danger`, texte `surface`.
- Dans une fiche, « Supprimer » est en haut à gauche, en `danger`, sans fond.
- Aucune suppression n'est immédiate : elle ouvre toujours une boîte de confirmation centrée sur un voile (`ombre` à 45 % en clair, à 60 % en sombre), qui nomme l'élément réellement visé, dit ce qui sera effacé et propose « Annuler » (fond `repos`) à gauche et « Supprimer » (`danger` plein) à droite. « Annuler » ramène d'où l'on vient : à la liste après un glissement, à la fiche sinon. En sombre, la boîte porte un contour `bordure` de 1 px, car l'ombre ne se voit plus sur le voile.

Un écran d'erreur dit ce qui n'a pas marché et ce qu'on peut faire : une icône d'alerte `danger` sur pastille `danger-clair`, un titre, une phrase, puis l'action pour réessayer (couleur de section) et une issue de secours (fond `repos`). Le bouton pour réessayer n'est jamais en `danger`. Pour un code-barres inconnu d'Open Food Facts, l'écran ne parle pas de connexion, et « Saisir le produit à la main » garde le code-barres.

## Iconographie

Icônes au trait de 2 px, bouts arrondis, couleur du texte voisin. Pas d'emoji. Les deux listes ont les mêmes filtres, par note, avec l'icône à gauche du libellé : étoile, coche et croix. Seule exception : la liste des produits a en plus un filtre « À noter » (icône horloge), parce qu'un produit peut attendre sa note alors qu'une recette en a toujours une. La source d'une recette (lien, livre, flèches croisées) n'est pas un filtre : elle s'affiche sur chaque ligne et sur la fiche. Les pastilles « Tous » et « Toutes » n'en ont pas. Une seule micro-animation d'icône : sur le bouton de scan, un trait `potiron` balaie le code-barres de haut en bas en 1,3 s, en boucle ; il reste immobile si l'utilisateur a demandé de réduire les mouvements.

## Mouvement

Des micro-animations courtes (0,28 à 0,45 s), jamais décoratives : la capsule de la barre d'onglets glisse vers l'onglet choisi, et le contenu qui change apparaît en fondu avec 6 px de montée. Toute sélection (filtre, délai de rappel, note, source) change de fond et de couleur en fondu sur 0,22 s, et l'élément se contracte légèrement sous le doigt. Tout mouvement est coupé si l'utilisateur a demandé de réduire les animations.

## Espacements et rayons

Marge latérale `space-4`, blocs séparés de `space-3`. Champs et boutons en `radius-md`, pastilles en `radius-pill`. Cibles tactiles d'au moins 44 px. Sur tablette, la V1 affiche l'interface du téléphone, étirée, en portrait comme en paysage.
