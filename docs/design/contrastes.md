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
- **Focus.** L'indicateur (contour et halo) est mesuré contre ce qui l'entoure : `surface` à l'intérieur, le fond à l'extérieur.
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
| Texte (1.4.3) | encre-douce | pastille au repos dans la barre (repos sur verre) | 4,5:1 | 5,40 | 6,69 |
| Texte (1.4.3) | encre-douce | pastille au repos, sous le cœur du halo menthe | 4,5:1 | 4,97 | 5,49 |
| Texte (1.4.3) | encre-douce | bouton de note non choisi, sur fond-haut | 4,5:1 | 5,24 | 7,09 |
| Texte (1.4.3) | encre-douce | bouton de note non choisi, sur fond-bas | 4,5:1 | 4,85 | 7,94 |
| Texte (1.4.3) | encre-douce | onglet sur la piste violette (Produits) | 4,5:1 | 4,84 | 7,07 |
| Texte (1.4.3) | encre-douce | onglet sur la piste potiron (Recettes) | 4,5:1 | 4,87 | 7,33 |
| Texte (1.4.3) | danger | surface (et surface sur danger) | 4,5:1 | 6,06 | 6,69 |
| Texte (1.4.3) | danger | fond-haut | 4,5:1 | 5,72 | 7,29 |
| Texte (1.4.3) | danger | danger-clair | 4,5:1 | 4,81 | 6,17 |
| Contour ou état (1.4.11) | note-super (contour du champ actif) | surface | 3:1 | **1,99 ✗** | 6,83 |
| Contour ou état (1.4.11) | menthe (halo du champ actif) | fond-haut | 3:1 | **1,51 ✗** | 8,29 |
| Contour ou état (1.4.11) | menthe (halo du champ actif) | fond-bas | 3:1 | **1,40 ✗** | 9,17 |
| Contour ou état (1.4.11) | bordure (contour des champs) | surface | 3:1 | **1,63 ✗** | 3,10 |
| Contour ou état (1.4.11) | bordure (contour des champs) | fond-haut | 3:1 | **1,54 ✗** | 3,38 |
| Contour ou état (1.4.11) | menthe (pastille choisie) | pastille au repos dans la barre | 3:1 | **1,38 ✗** | 6,51 |
| Repère visuel (non exigé) | surface (boîte de confirmation) | voile sur fond | 3:1 | **2,69 ✗** | **1,33 ✗** |
| Repère visuel (non exigé) | bordure (contour de la boîte, sombre) | voile sur fond | 3:1 | — | 4,13 |
| Retenu (focus) | encre-douce (contour de focus, 2 px) | surface | 3:1 | 6,26 | 7,82 |
| Retenu (focus) | encre-douce (contour de focus, 2 px) | fond-haut | 3:1 | 5,91 | 8,52 |

## Décisions du 2026-11-22

- **Contour de focus.**
  - Contour de 2 px en `encre-douce`, halo `menthe` conservé, dans les deux thèmes.
  - Il remplace le contour fin `note-super`, qui ne fait que 1,99:1 en clair.
  - Voir les lignes « Retenu (focus) ».
- **Boîte de confirmation.**
  - En sombre, contour `bordure` de 1 px, parce que l'ombre ne se voit plus sur le voile.
  - Voir les lignes « Repère visuel ».

## Échecs connus du thème clair, non traités à ce jour

- `bordure` sur `surface` : 1,63:1.
- Pastille choisie contre pastille au repos : 1,38:1. Le libellé en gras aide ; je ne sais pas encore s'il suffit au critère 1.4.11.
- Texte secondaire au cœur des halos : potiron 4,35:1, violet 3,63:1.
- Boîte contre voile : 2,69:1, avec une ombre. Ce point n'est pas exigé par WCAG.
