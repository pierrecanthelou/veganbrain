# Carnet vegan

Le carnet d'un couple vegan pour noter les produits et les recettes testés. Des verts pastel en dégradés doux, un texte vert sombre, et deux couleurs de section : potiron pour les produits, violette pour les recettes.

## Principes

- **Doux, pas fade.** Les fonds sont pastel ; le texte reste en `encre`, sombre, pour rester lisible.
- **Trois notes, jamais par la couleur seule.** Super, Bon, Pas bon ont chacune une couleur et une icône. Dans les listes, la note est une pastille ronde de 36 px avec l'icône seule, sans mot ; les filtres, la fiche et le résultat de scan gardent le libellé et servent de légende.
- **Le décor reste derrière.** Les dégradés sont lents et clairs ; ils ne passent jamais sous un texte au point de le gêner.

## Couleurs

- Le fond couvre tout l'écran, en-tête compris : un dégradé vertical de `fond-haut` à `fond-bas`, un halo de section dans l'angle bas, du côté de son onglet (`potiron` à 32 % en bas à gauche sur les écrans Produits ; en bas à droite sur les écrans Recettes, un violet plus saturé que `violette`, rgb 155 120 235 à 42 %, parce que `violette` s'éteint sur le vert), pour accompagner l'onglet sélectionné, et un halo `menthe` à 55 % dans l'angle haut opposé ; `fond` sert de repli. `surface` porte les champs, les pastilles au repos et la barre d'onglets.
- `encre` est la seule couleur de texte sur `menthe` et sur les trois couleurs de note. `encre-douce` sert au texte secondaire, sur `fond`, `surface` et `vignette` seulement.
- Deux couleurs de section, les seules couleurs vives du thème : `potiron` pour Produits, `violette` pour Recettes. Chacune marque l'action principale de sa section (cercle « + », Enregistrer), une seule fois par écran, texte en `encre`. `potiron-clair` et `violette-clair` en sont les versions pastel.
- `menthe` marque l'élément sélectionné (filtre, source) et les actions secondaires. Les pastilles n'ont jamais de contour. Au repos : fond `encre` à 7 %, libellé `encre-douce`. Sélectionnée : fond plein, libellé `encre` plus gras ; un filtre de note prend la couleur de sa note (`note-super`, `note-bon`, `note-pas-bon`), sur Produits comme sur Recettes ; « Tous », « Toutes » et les autres pastilles prennent `menthe`. Aucun contour en `encre`, nulle part. Les grands boutons de note et le choix de source des fiches suivent la même règle que les pastilles, avec en plus une ombre douce sous l'élément sélectionné (2 px de décalage, 10 px de flou, `encre` à 18 %). Un champ actif se signale par un contour fin `note-super` et un halo `menthe` de 3 px. La barre d'onglets est une piste arrondie de 48 px de haut, sans contour, teintée sur toute sa largeur par la couleur de la section non sélectionnée, presque transparente (`violette` à 18 % quand on est sur Produits, `potiron` à 16 % quand on est sur Recettes). Le bouton Ajouter est un simple « + » dans un cercle de 60 px, fixe au centre, plus grand que la capsule (48 px) qu'il coiffe. Une capsule englobe l'onglet en cours et ce cercle : elle s'étend vers la gauche sur Produits (`potiron-clair`, cercle `potiron`), vers la droite sur Recettes (`violette-clair`, cercle `violette`), et dit ainsi ce qu'on ajoute. L'onglet en cours est libellé en `encre` gras ; l'autre reste hors de la capsule, en `encre-douce`, sur la piste. Au changement d'onglet, la capsule glisse d'un côté à l'autre en 0,45 s en changeant de couleur, le cercle change de couleur sans bouger ; sans animation si l'utilisateur a demandé de réduire les mouvements.
- Un produit ajouté sans note (scanné en magasin, à noter plus tard) porte une pastille « À noter » : fond `surface`, contour `encre-douce` en pointillés, icône horloge.
- `note-super`, `note-bon`, `note-pas-bon` ne servent qu'aux notes. Elles diffèrent en clarté, et l'icône (étoile, coche, croix) accompagne toujours la couleur.
- `bordure` trace les contours des champs et des pastilles. Les lignes horizontales des listes sont des filets gravés : un trait de 1 px en `bordure`, doublé juste en dessous d'un trait de 1 px en `separateur`. `vignette` remplit les emplacements de photo.
- `filigrane` est un ton plus sombre que `fond`, réservé aux illustrations. Illustrations et filets de liste partagent le même principe gravé à deux traits, mais pas le même trait sombre (`filigrane` pour les unes, `bordure` pour les autres).

## Typographie

Space Grotesk pour les titres (`titre-ecran`, `titre-fiche`, `titre-barre`), Inter pour le reste (`corps`, `corps-fort`, `secondaire`, `etiquette`). Les deux viennent de Google Fonts.

## Illustrations

Les écrans ne portent plus d'illustration de fond. Le groupe d'assets **Filigranes** garde les huit dessins gravés (trait `filigrane` doublé d'un trait `separateur`), au cas où ils resserviraient.

## En-tête

Chaque écran s'ouvre sur un panneau en verre dépoli (`surface` à 60 %, flou de 18 px) qui part du bord haut de l'écran, avec `space-4` au-dessus du titre. Dans l'app, la zone réservée du téléphone (barre d'état, encoche) s'ajoute à cette marge et le panneau s'étend dessous ; la maquette ne la dessine pas, et se termine par un bord droit, sans angles arrondis, souligné d'un filet gravé (`bordure` puis `separateur`). Il laisse passer le dégradé du fond et les listes défilent dessous. Il porte le titre et, sur les écrans de liste, la recherche et les filtres. Sur les deux écrans de liste, un petit trait de 36 × 5 px sous le titre reprend la couleur de la section. En bas des écrans de liste, il n'y a ni pied ni séparation : la liste elle-même devient progressivement transparente sur une quarantaine de pixels juste au-dessus de la barre d'onglets, et laisse voir le fond de l'écran tel quel. Rien n'est peint par-dessus le dégradé.

## Fiches

Une fiche (produit, recette) n'est pas un écran à part : c'est une feuille qui monte du bas en 0,42 s et s'arrête à 72 px du haut, angles hauts en 28 px, avec une ombre portée vers le haut. L'écran d'où l'on vient reste visible au-dessus, assombri par un voile `encre` à 30 %. La feuille n'a pas d'en-tête : une poignée centrée, une croix de fermeture en haut à droite et, sur une fiche existante, « Supprimer » en `danger` en haut à gauche, loin du bouton principal. Le contenu défile ; le bouton principal reste fixe en bas.

Un seul formulaire produit sert à trois usages : la fiche d'un produit existant (champs remplis, modifiables), le résultat d'un scan pas encore dans le carnet (champs préremplis par la reconnaissance, à corriger, avec un encart qui le rappelle et le choix d'un rappel) et la création à la main (champs vides). Champs, dans l'ordre : photo, nom, marque et catégorie, note, avis, lieu d'achat.

Photo : quand il y en a une, elle occupe un bloc de 150 px de haut avec un bouton « Changer la photo » posé en bas à droite. Quand il n'y en a pas, une zone en pointillés `note-super` propose « Prendre une photo » (`menthe`) et « Choisir ».

## Liens entre produits et recettes

La fiche recette liste les « Produits utilisés », la fiche produit liste « Utilisé dans nos recettes ». Chaque lien est une ligne sur `surface` avec le nom, une précision et la pastille de note, et ouvre la fiche liée. Un bouton en voile « Lier un produit » ou « Lier une recette » suit la liste.

## Danger, suppression et erreur

`danger` est réservé à la suppression. Trois règles :

- Dans une liste, glisser une ligne vers la gauche révèle l'action « Supprimer » sur fond `danger`, texte `surface`.
- Dans une fiche, « Supprimer » est en haut à gauche, en `danger`, sans fond.
- Aucune suppression n'est immédiate : elle ouvre toujours une boîte de confirmation centrée sur un voile `encre` à 45 %, qui nomme l'élément, dit ce qui sera effacé et propose « Annuler » (voile) à gauche et « Supprimer » (`danger` plein) à droite.

Un écran d'erreur dit ce qui n'a pas marché et ce qu'on peut faire : une icône d'alerte `danger` sur pastille `danger-clair`, un titre, une phrase, puis l'action pour réessayer (couleur de section) et une issue de secours (voile). Le bouton pour réessayer n'est jamais en `danger`.

## Iconographie

Icônes au trait de 2 px, bouts arrondis, couleur du texte voisin. Pas d'emoji. Les deux listes ont les mêmes filtres, par note, avec l'icône à gauche du libellé : étoile, coche et croix. La source d'une recette (lien, livre, flèches croisées) n'est pas un filtre : elle s'affiche sur chaque ligne et sur la fiche. Les pastilles « Tous » et « Toutes » n'en ont pas. Une seule micro-animation d'icône : sur le bouton de scan, un trait `potiron` balaie le code-barres de haut en bas en 1,3 s, en boucle ; il reste immobile si l'utilisateur a demandé de réduire les mouvements.

## Mouvement

Des micro-animations courtes (0,28 à 0,45 s), jamais décoratives : la capsule de la barre d'onglets glisse vers l'onglet choisi, le sélecteur à deux positions (Code-barres / Photo) déplace sa pastille `menthe` sous le libellé choisi, et le contenu qui change apparaît en fondu avec 6 px de montée. Toute sélection (filtre, délai de rappel, note, source) change de fond et de couleur en fondu sur 0,22 s, et l'élément se contracte légèrement sous le doigt. Tout mouvement est coupé si l'utilisateur a demandé de réduire les animations.

## Espacements et rayons

Marge latérale `space-4`, blocs séparés de `space-3`. Champs et boutons en `radius-md`, pastilles en `radius-pill`. Cibles tactiles d'au moins 44 px.
