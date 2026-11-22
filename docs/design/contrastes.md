# Contrastes des thèmes clair et sombre

Calculés le 2026-11-22 à partir de `tokens.json` (version 2). Ils sont à refaire si une valeur de couleur ou une opacité dérivée change.

## Méthode

- **Formule WCAG 2.2.**
  - Linéarisation sRGB avec un seuil de 0,04045 ; L = 0,2126 R + 0,7152 G + 0,0722 B ; ratio = (L1 + 0,05) / (L2 + 0,05).
  - Sources : https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html et https://www.w3.org/TR/WCAG22/#dfn-contrast-ratio
- **Seuils niveau AA.**
  - 4,5:1 pour le texte (critère 1.4.3).
  - 3:1 pour les contours, les états et les indicateurs de focus (critère 1.4.11, https://www.w3.org/TR/WCAG22/#non-text-contrast).
  - Pas d'arrondi avant la comparaison.
- **Fond réel.**
  - Chaque paire est mesurée sur le fond réellement rendu.
  - Les mélanges alpha sont arrondis à 8 bits par canal, comme le fait un navigateur.
  - Les halos sont pris à leur point le plus intense.
- **Focus.** Le contour est détaché de l'élément par 2 px : il ne touche que le fond qui entoure l'élément. Il est donc mesuré contre chaque fond où un élément interactif peut se trouver, pas contre l'élément lui-même. Source : https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html, section « Relationship with Focus Visible » : un indicateur extérieur « needs to contrast with the background that the component is on ».
- **Opacités dérivées.** Voir `color.derived` dans `tokens.json`.

## Tableau

| Critère | Premier plan | Fond | Seuil | Clair | Sombre |
|---|---|---|---|---|---|
| Texte (1.4.3) | encre | fond | 4,5:1 | 10,97 | 14,73 |
| Texte (1.4.3) | encre | fond-haut | 4,5:1 | 11,40 | 13,96 |
| Texte (1.4.3) | encre | fond-bas | 4,5:1 | 10,58 | 15,44 |
| Texte (1.4.3) | encre | surface | 4,5:1 | 12,09 | 12,81 |
| Texte (1.4.3) | encre | vignette | 4,5:1 | 9,92 | 10,06 |
| Texte (1.4.3) | encre | potiron-clair | 4,5:1 | 8,79 | 10,50 |
| Texte (1.4.3) | encre | violette-clair | 4,5:1 | 9,21 | 12,23 |
| Texte (1.4.3) | encre | danger-clair | 4,5:1 | 9,60 | 11,81 |
| Texte (1.4.3) | encre-sur-couleur | menthe | 4,5:1 | 7,54 | 8,51 |
| Texte (1.4.3) | encre-sur-couleur | potiron | 4,5:1 | 5,13 | 6,44 |
| Texte (1.4.3) | encre-sur-couleur | violette | 4,5:1 | 4,97 | 6,05 |
| Texte (1.4.3) | encre-sur-couleur | note-super | 4,5:1 | 6,07 | 7,64 |
| Texte (1.4.3) | encre-sur-couleur | note-bon | 4,5:1 | 10,06 | 11,81 |
| Texte (1.4.3) | encre-sur-couleur | note-pas-bon | 4,5:1 | 7,84 | 9,04 |
| Texte (1.4.3) | encre-douce | fond | 4,5:1 | 5,68 | 8,99 |
| Texte (1.4.3) | encre-douce | fond-haut | 4,5:1 | 5,91 | 8,52 |
| Texte (1.4.3) | encre-douce | fond-bas | 4,5:1 | 5,48 | 9,42 |
| Texte (1.4.3) | encre-douce | surface | 4,5:1 | 6,26 | 7,82 |
| Texte (1.4.3) | encre-douce | vignette | 4,5:1 | 5,14 | 6,14 |
| Texte (1.4.3) | encre-douce | cœur du halo potiron (Produits) | 4,5:1 | **4,35 ✗** | 7,08 |
| Texte (1.4.3) | encre-douce | cœur du halo violet (Recettes) | 4,5:1 | **3,63 ✗** | 7,15 |
| Texte (1.4.3) | encre-douce | cœur du halo menthe | 4,5:1 | 4,74 | 5,26 |
| Texte (1.4.3) | encre-douce | pastille au repos dans l'en-tête (repos sur verre) | 4,5:1 | 5,40 | 6,69 |
| Texte (1.4.3) | encre-douce | pastille au repos, sous le cœur du halo menthe | 4,5:1 | 4,97 | 5,49 |
| Texte (1.4.3) | encre-douce | bouton de note non choisi, sur fond-haut | 4,5:1 | 5,24 | 7,09 |
| Texte (1.4.3) | encre-douce | bouton de note non choisi, sur fond-bas | 4,5:1 | 4,85 | 7,94 |
| Texte (1.4.3) | encre-douce | onglet sur la piste violette (Produits) | 4,5:1 | 4,84 | 7,07 |
| Texte (1.4.3) | encre-douce | onglet sur la piste potiron (Recettes) | 4,5:1 | 4,87 | 7,33 |
| Texte (1.4.3) | danger | surface (et surface sur danger) | 4,5:1 | 6,06 | 6,69 |
| Texte (1.4.3) | danger | fond-haut | 4,5:1 | 5,72 | 7,29 |
| Texte (1.4.3) | danger | danger-clair | 4,5:1 | 4,81 | 6,17 |
| Texte (1.4.3) | encre-sur-couleur en clair, encre en sombre | encart du scan (`encart-scan` : menthe à 55 % en clair, 22 % en sombre), sur fond-haut | 4,5:1 | 9,15 | 8,62 |
| Contour ou état (1.4.11) | note-super (ancien contour du champ actif) | surface | 3:1 | **1,99 ✗** | 6,83 |
| Contour ou état (1.4.11) | menthe (ancien halo du champ actif) | fond-haut | 3:1 | **1,51 ✗** | 8,29 |
| Contour ou état (1.4.11) | menthe (ancien halo du champ actif) | fond-bas | 3:1 | **1,40 ✗** | 9,17 |
| Contour ou état (1.4.11) | bordure (contour des champs) | surface | 3:1 | **1,63 ✗** | 3,10 |
| Contour ou état (1.4.11) | bordure (contour des champs) | fond-haut | 3:1 | **1,54 ✗** | 3,38 |
| Contour ou état (1.4.11) | menthe (pastille choisie) | pastille au repos dans l'en-tête | 3:1 | **1,38 ✗** | 6,51 |
| Repère visuel (non exigé) | surface (boîte de confirmation) | voile sur fond | 3:1 | **2,69 ✗** | **1,33 ✗** |
| Repère visuel (non exigé) | bordure (contour de la boîte, sombre) | voile sur fond | 3:1 | — | 4,13 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | fond-haut | 3:1 | 5,91 | 8,52 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | fond-bas | 3:1 | 5,48 | 9,42 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | surface | 3:1 | 6,26 | 7,82 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | verre de l'en-tête | 3:1 | 6,12 | 8,11 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | verre sous le cœur du halo menthe | 3:1 | 5,61 | 6,70 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | cœur du halo menthe | 3:1 | 4,74 | 5,26 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | cœur du halo potiron | 3:1 | 4,35 | 7,08 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | cœur du halo violet | 3:1 | 3,63 | 7,15 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | piste Produits | 3:1 | 4,84 | 7,07 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | piste Recettes | 3:1 | 4,87 | 7,33 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | capsule potiron-clair | 3:1 | 4,56 | 6,41 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | capsule violette-clair | 3:1 | 4,77 | 7,46 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | vignette | 3:1 | 5,14 | 6,14 |
| Retenu (focus) | encre-douce (contour de focus détaché, 2 px) | encart du scan (`encart-scan` : menthe à 55 % en clair, 22 % en sombre), sur fond-haut | 3:1 | 4,74 | 5,26 |

## Décisions du 2026-11-22

- **Contour de focus.**
  - Contour de 2 px en `encre-douce`, détaché de l'élément par 2 px, sans halo, dans les deux thèmes, pour le focus comme pour le champ en saisie.
  - Minimum mesuré : 3,6287:1, soit au moins 3,6:1. Voir les lignes « Retenu (focus) ».
  - Il remplace deux versions écartées. D'abord le contour fin `note-super` avec halo `menthe` : 1,99:1 sur `surface` en clair, halo contre fond 1,51:1. Ensuite le contour collé à l'élément avec halo `menthe` à l'extérieur, validé un temps : le halo touchait le fond à 1,51:1 en clair, et le contour se confondait en sombre avec les éléments vifs (1,03:1 sur `menthe`, 1,17:1 sur `danger`).
- **Encart du scan.**
  - En sombre, `menthe` à 22 % avec un texte `encre`, au lieu de 55 % avec `encre-sur-couleur` (3,61:1 pour le texte, 2,43:1 pour le focus du lien ODbL). Texte 8,62:1, focus 5,26:1.
  - Mesuré sur `fond-haut` nu ; dans la maquette, l'encart est aussi sous le halo `menthe` de la feuille (ScanInconnu.dc.html:36). À remesurer dans l'application.
- **Boîte de confirmation.**
  - En sombre, contour `bordure` de 1 px, parce que l'ombre ne se voit plus sur le voile.
  - Voir les lignes « Repère visuel ».

## Échecs connus, non traités à ce jour

Thème clair :

- `bordure` sur `surface` : 1,63:1.
- Pastille choisie contre pastille au repos : 1,38:1. Le libellé change de couleur mais pas de graisse ; je ne sais pas encore si cela suffit au critère 1.4.11.
- Texte secondaire au cœur des halos : potiron 4,35:1, violet 3,63:1.
- Boîte contre voile : 2,69:1, avec une ombre. Ce point n'est pas exigé par WCAG.

Thème sombre : aucun.
