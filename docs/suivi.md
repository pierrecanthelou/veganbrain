# Suivi du travail

Dernière mise à jour : 2026-11-22

## En cours

- Rien. Prochaine tâche : 3.

## À faire

3. docs/criteres.md : table dorée (identifiant, règle, exemple entrée → sortie, écran, niveau de test), à partir des maquettes lues, relue deux fois : par le sous-agent habituel et par un sous-agent Fable.
4. Licence Arcmutate open source demandée et essai de branchement Pitest + Arcmutate sur la cible JVM du module partagé (spike).
5. Tarifs des comptes Apple Developer et Google Play Console sourcés (doc officielle).
6. Suite de la phase 2 (tests unitaires, mutation, E2E Maestro, CI, accessibilité) : découpée en tâches une fois la table dorée écrite.

## Fait

- 2026-11-22 : docs/suivi.md créé (preuve : ce fichier).
- 2026-11-22 : sources officielles rassemblées pour les six points, cinq recherches en parallèle. Spot-checks refaits moi-même : tarifs D1/Workers/R2 (docs Cloudflare), versions Kotlin/CMP/SQLDelight/Ktor/Kotest (API GitHub), OFF (31 984 produits label vegan en France, limites 15/10 req/min, licences ODbL/CC BY-SA), Arcmutate (pas de licence personnelle gratuite). Preuve : URL citées dans docs/cadrage.md.
- 2026-11-22 : docs/cadrage.md rédigé, sections 1 à 6 (options, tableau, recommandation, « ce qui me ferait changer d'avis », sources). Preuve : docs/cadrage.md.
- 2026-11-22 : docs/cadrage.md relu par un sous-agent généraliste (environ 40 affirmations revérifiées). Corrections sourcées intégrées après relecture de leur preuve : D1 `batch()`, dérogation d'orientation Android 16, Adaptive alpha dans CMP 1.13, citations Access et R2, attribution SKIE, résumé ODbL, Maestro. Ajouts de conception, marqués hypothèse ou non vérifiés : états d'erreur, distribution, dérive d'horloge HLC, rappel déjà noté par l'autre, tableau E2E, lecture des photos, coût de l'auth. Non intégrées faute de preuve relue : plafond de dépense Workers, plantage sans NSCameraUsageDescription, formule Managed OAuth « No user identity », définition ODbL de « Publicly ». Preuve : docs/cadrage.md.
- 2026-11-22 : plan Cloudflare tranché, Workers Paid déjà souscrit. cadrage.md §2 mis à jour (D1 1 000 requêtes par invocation, CPU jusqu'à 5 min, Time Travel 30 jours). Preuve : ta réponse, page Workers limits revérifiée.
- 2026-11-22 : **phase 1 validée** (preuve : ta réponse « Je valide tout »). Décisions prises :
  - dépôt **public** (CI GitHub Actions et Arcmutate gratuits en open source) ;
  - **un avis par personne**, **une note commune** ;
  - distribution par **TestFlight et test interne Google Play** ;
  - relecture par un **sous-agent généraliste** avec consigne ;
  - maquettes dans `docs/maquettes/`, design system dans `docs/design/`.
- 2026-11-22 : maquettes et design system récupérés depuis les artifacts claude.ai (Design JwYfF1ykTi6mgnT9gJRC3g ; design system PYW9YG1w1FCwhYomsFtSXW, version 1795333659-1878). Preuve : 12 `.dc.html` et `canvas.json` dans `docs/maquettes/`, `tokens.json` (sha256 6281bf84…) et `README.md` (sha256 fb0b5016…) dans `docs/design/`, identiques aux sources.
- 2026-11-22 : écart 1 tranché (preuve : ta réponse « Code-barres seul »). Le mode Photo est retiré en V1. Un code-barres inconnu du carnet ouvre « produit à compléter », prérempli par Open Food Facts ; l'encart dit « Trouvé dans Open Food Facts, vérifiez » au lieu de parler de reconnaissance.
- 2026-11-22 : écart 2 tranché (preuve : ta réponse « Erreur = code inconnu »).
  - Hors réseau : le scan est gardé en « À noter » avec son code-barres et un rappel, puis prérempli au retour du réseau.
  - Code inconnu d'Open Food Facts : l'écran Erreur s'affiche, sans la phrase sur la connexion. « Saisir le produit à la main » y garde le code-barres.
- 2026-11-22 : écart 3 tranché et décision de phase 1 révisée (preuve : ta question sur les stores, puis ta réponse « Porte ouverte »). Un seul avis commun par carnet, comme la maquette ; « Nos ajustements » reste commun. Multi-utilisateur en « porte ouverte » : la V1 reste à deux, mais les données sont rangées par carnet avec ses membres et l'authentification passe par une interface remplaçable. Mise à jour faite dans docs/cadrage.md (décisions de validation).
- 2026-11-22 : écart 4 tranché (preuve : ta réponse « Produits seulement »). Filtre « À noter » (icône horloge) ajouté aux produits seulement. Une recette s'enregistre toujours avec une note, sans note présélectionnée. Exception à reporter dans le design system (README:58).
- 2026-11-22 : écart 5 tranché (preuve : ta réponse « Seulement sans note »). Le bloc de rappel s'affiche tant qu'aucune note n'est choisie, au scan comme à la création à la main. Aucun délai n'est présélectionné. L'autorisation est demandée au premier choix. Le rappel est annulé dès que le produit est noté.
- 2026-11-22 : écart 6 tranché (preuve : ta réponse « Je la propose »). Je propose une palette sombre dérivée des tokens, avec les contrastes calculés et WCAG AA pour cible. Tu la valides sur un aperçu ; ensuite je l'ajoute au design system.
- 2026-11-22 : écart 7 tranché (preuve : ta réponse « Pas en V1 »). Sur tablette, la V1 affiche l'interface du téléphone, étirée ; la disposition liste/détail passe en V2 (Parking). Contrainte qui reste : sur Android, avec la cible API 36, les restrictions d'orientation sont ignorées sur les grands écrans (docs/cadrage.md:45). L'interface étirée doit donc fonctionner aussi en paysage.
- 2026-11-22 : écart 8 tranché (preuve : ta réponse « Dans l'encart du scan »). Le lien « Données Open Food Facts, ODbL » va dans l'encart menthe de « produit à compléter ».
- 2026-11-22 : écart 9 tranché (preuve : ta réponse « Feuille de choix »). « Lier… » ouvre une feuille avec une recherche et une liste aux mêmes lignes que les écrans de liste ; on peut lier plusieurs éléments. Un lien se retire par une croix sur sa ligne, sans confirmation. La feuille sera décrite dans la table dorée.
- 2026-11-22 : écart 10 tranché (preuve : ta réponse « Fiche vide »). Le « + » des recettes ouvre la feuille vide « Nouvelle recette » : sans « Supprimer », aucune source ni note présélectionnée, bouton « Ajouter au carnet ». La note étant obligatoire pour une recette (écart 4), la table dorée devra dire comment l'enregistrement sans note est empêché.
- 2026-11-22 : écart 11 tranché (preuve : ta réponse « Garder, recherche locale »). La recherche reste dans les deux listes. Elle tourne sur le téléphone, dans les données synchronisées, donc aussi hors réseau. Elle porte sur le nom, la marque et la catégorie pour les produits, sur le nom pour les recettes, et se combine avec le filtre de note.
- 2026-11-22 : écart 12 tranché (preuve : ta réponse « Les quatre »).
  - « Annuler » ramène d'où l'on vient : à la liste après un glissement, à la fiche sinon.
  - La boîte de confirmation nomme l'élément réellement visé.
  - Le glissement pour supprimer marche aussi sur les recettes (docs/design/README.md:50).
  - Chaque élément actif a un focus visible, avec le style des champs actifs (docs/design/README.md:16). À ajouter au design system.
- 2026-11-22 : **tâche 1 close** : maquettes et design system lus en entier, 12 écrans inventoriés, 12 écarts tranchés avec toi un par un. Preuve : docs/inventaire-ecrans.md (relu par le sous-agent) et les entrées « écart 1 » à « écart 12 » ci-dessus.
- 2026-11-22 : contre-revue par un autre modèle décidée (preuve : ta réponse « cadrage.md + table dorée »). Les deux relectures déjà faites tournaient sur le même modèle que moi (Opus 5.5, `model: None` dans le journal de session). Fable relit docs/cadrage.md maintenant, puis la table dorée ; l'inventaire n'est pas relu une deuxième fois.
- 2026-11-22 : **contre-revue Fable de docs/cadrage.md faite**. Pas d'erreur grave ni d'obstacle aux trois contraintes. 24 corrections appliquées, chacune après relecture de sa preuve dans la page téléchargée ou par une commande (`curl` sur l'API GitHub, npm, Couchbase) :
  - décisions déjà prises mais pas encore reportées : dépôt public, avis commun, distribution, plan Paid ;
  - nouvelle section « Secrets et dépôt public » ;
  - plafond de dépense Workers sourcé : il ne couvre pas R2, et l'erreur 1103 le signale ;
  - grille Workers Logs Paid et grille du 1ᵉʳ décembre ;
  - CPU 30 s par défaut ;
  - longueur connue exigée par R2 `put()` ;
  - KMP-NativeCoroutines n'est pas un vrai repli de SKIE ; Swift export est une sortie à terme ;
  - réserves Android (économie de batterie, redémarrage) ;
  - Maestro, Arcmutate incrémental, facturation macOS ;
  - sources OFF Kotlin, Couchbase Lite et code scanner ;
  - états d'erreur réalignés sur l'écart 2.
  
  Rejeté, preuve contraire à l'appui : la citation kaputt figure bien dans le README (ligne 233, « Verified on macOS against jvm, `iosSimulatorArm64` »). Non confirmés : Arcmutate à 13 $, Zero Trust gratuit jusqu'à 50 utilisateurs, limite iOS de 64 notifications. Les manques de synchro relevés par raisonnement sont ajoutés comme points ouverts, pas comme des faits. Preuve : docs/cadrage.md, sauvegarde de la version précédente dans le scratchpad.
- 2026-11-22 : **tâche 2b close** : thème sombre validé (preuve : ta réponse « Je valide », sur l'aperçu https://claude.ai/artifact/St8i9jqHVS9FDQ8e15CZo2).
  - docs/design/tokens.json passe en version 2 (sha256 625f7832…) : thème `sombre` sur les 20 couleurs, deux tokens nouveaux `encre-sur-couleur` et `ombre` (valeur `encre` en clair, donc thème clair inchangé), usages des fonds vifs mis à jour, opacités dans `color.derived`.
  - docs/design/contrastes.md (sha256 8277619c…) : 41 paires mesurées sur le fond réellement rendu, dans les deux thèmes. Le sombre passe tous les seuils AA (avant la mesure de l'encart du scan, voir 2c).
  - Focus : contour 2 px `encre-douce` + halo `menthe` dans les deux thèmes (preuve : ta réponse « Contour encre-douce 2 px »). Il remplace le style de l'écart 12, qui ne faisait que 1,99:1 en clair.
  - Relecture : la première version de l'aperçu a été relue par le sous-agent et ses remarques ont été reprises. La version publiée n'a pas eu de seconde relecture.
- 2026-11-22 : **tâche 2 close** : dépôt public https://github.com/pierrecanthelou/veganbrain, branche `main`, commit initial 116b417 (preuve : ta réponse « Oui, public veganbrain », `gh repo view` → PUBLIC main, `git status` propre).
  - `.gitignore` : `.dev.vars*`, `.env*`, `.wrangler/` repris du gabarit Workers officiel (cloudflare/workers-sdk, create-cloudflare) ; Gradle, Kotlin, Android et Swift repris de github/gitignore (commit eeb775f7cbd2). Ajouts de ma part, sans source : `*.p8`, `*.p12`, `*.mobileprovision`, `GoogleService-Info.plist`, `.DS_Store`, `Thumbs.db`.
  - Exclusions testées avec `git check-ignore` ; recherche d'adresses e-mail, de jetons et de chemins personnels dans docs/ : rien trouvé.
- 2026-11-22 : **tâche 2c close** : décisions des écarts et thème sombre reportés dans docs/design/README.md et docs/design/tokens.json (preuve : diff du commit « Tâche 2c : décisions des écarts et thème sombre dans le design system »).
  - Relecture par le sous-agent : 9 corrections (C1 à C9) et 6 points à discuter (D1 à D6). J'ai relu chaque preuve citée dans les maquettes avant de corriger. Les points antérieurs à 2c (D5) passent en « À trancher ».
  - Seconde relecture : 9 corrections et 8 points à discuter, preuves relues dans les maquettes et recalculées. Tout est appliqué, sauf la mise à jour des renvois de ligne dans l'historique de ce fichier, qui désignent la version du commit 116b417.
  - Focus : la géométrie validée plus tôt (contour collé + halo `menthe`) ne tenait pas. Le halo touchait le fond à 1,51:1 en clair, et le contour se confondait en sombre avec les éléments vifs (1,03:1 sur `menthe`). Elle est remplacée par un contour de 2 px `encre-douce`, détaché de l'élément par 2 px, sans halo (preuve : ta réponse « Contour détaché, sans halo »). Minimum 3,6:1 sur 13 fonds, dans les deux thèmes ; seul l'encart du scan, en sombre à 55 %, descend à 2,4:1 (point 17).
  - docs/design/contrastes.md régénéré : 55 paires, avec les mesures du nouveau focus et de l'encart du scan. Règle relue dans WCAG, Understanding 1.4.11, section « Relationship with Focus Visible ».

## À trancher

Écarts relevés dans docs/inventaire-ecrans.md (section « Écarts »), à trancher un par un avant la table dorée :
1. ~~mode Photo en V1~~ : tranché, voir Fait ;
2. ~~hors réseau et scan sans résultat~~ : tranché, voir Fait ;
3. ~~un seul « Notre avis »~~ : tranché, voir Fait ;
4. ~~filtre « À noter »~~ : tranché, voir Fait ;
5. ~~rappel~~ : tranché, voir Fait ;
6. ~~mode sombre~~ : tranché, voir Fait ;
7. ~~tablette~~ : tranché, voir Fait ;
8. ~~mention ODbL~~ : tranché, voir Fait ;
9. ~~sélecteur de lien~~ : tranché, voir Fait ;
10. ~~nouvelle recette~~ : tranché, voir Fait ;
11. ~~recherche dans les listes~~ : tranché, voir Fait ;
12. ~~points mineurs~~ : tranché, voir Fait.

Relevé à la tâche 2b, à trancher avant les tests d'accessibilité (docs/design/contrastes.md, thème clair) :
13. contour des champs `bordure` sur `surface` : 1,63:1 pour 3:1 exigé ;
14. pastille choisie contre pastille au repos : 1,38:1. Le libellé change de couleur, pas de graisse (Main.dc.html:27) : cela suffit-il ?
15. texte secondaire au cœur des halos : potiron 4,35:1, violet 3,63:1 pour 4,5:1 exigé.

Relevé à la tâche 2c :
16. valeur en sombre des opacités qui n'en ont qu'en clair :
    - voile des fiches, `ombre` à 30 % (Produit.dc.html:35) ;
    - ombre de la feuille, 22 % (Produit.dc.html:36) ;
    - poignée, `encre` à 22 % (Produit.dc.html:38) ;
    - ombre de « Changer la photo », 16 % (Produit.dc.html:49) ;
    - ombre de la boîte de confirmation, 28 % (ConfirmProduit.dc.html:23).
17. encart `menthe` du scan en sombre (ScanInconnu.dc.html:45).
    - À 55 %, le texte n'atteint que 3,61:1 en `encre-sur-couleur` et 3,97:1 en `encre`, et le focus du lien ODbL 2,43:1.
    - À 22 %, comme le halo, `encre` donne 8,62:1 et le focus 5,26:1 ; mais le texte doit alors passer en `encre` (`encre-sur-couleur` : 1,66:1), exception au README.
    - Ces mesures prennent `fond-haut` nu ; dans la maquette, l'encart est aussi sous le halo `menthe` de la feuille (ScanInconnu.dc.html:36). À remesurer une fois la valeur choisie.
18. rayon des pastilles : `radius-pill` vaut 24 px, mais les maquettes dessinent 22 px (Produit.dc.html:103, Creation.dc.html:49). Le token est aussi donné pour le bouton Ajouter, qui est un cercle de rayon 30 px (Main.dc.html:98).
19. points antérieurs relevés à la relecture 2c :
    - `note-super` est employé hors des notes : contour de 2 px (Scan.dc.html:35, ScanConnu.dc.html:24), pointillés de la zone photo ;
    - `menthe` est dit marquer les « actions secondaires » ;
    - `encre-douce` est employé au-delà de `fond`, `surface` et `vignette` (pistes, halos, capsules ; contrastes.md).
20. focus, deux cas d'implémentation : la recherche n'a pas de contour propre (`outline: 0`, Main.dc.html:42), le focus doit être porté par le `label` qui l'englobe (Main.dc.html:39) ; l'action « Supprimer » révélée au glissement touche le bord de l'écran (ListeGlisser.dc.html:61), son contour détaché serait rogné.

## Parking

- Tablette en liste/détail : V2. Maquettes à faire à ce moment-là.
- Publication publique sur les stores (multi-foyers) : vrais comptes, isolation des carnets, politique de confidentialité, suppression du compte dans l'app, règles de connexion des stores. À sourcer dans la doc officielle Apple et Google le moment venu ; rien n'est vérifié à ce jour.
- Contribution en retour vers Open Food Facts (produits créés à la main) : hors V1.
- Cache local de produits OFF pour consulter hors réseau : hors V1.
- OCR sur l'appareil pour préremplir un produit sans code-barres : V2, selon le taux de scans inconnus mesuré.
- Durée de rétention des pierres tombales avant purge (N jours) : à fixer en phase 2.
- Points ouverts de synchro relevés en contre-revue (docs/cadrage.md §3), à fixer en phase 2 : appareil qui ne revient jamais ou qui revient après la purge ; effacement de l'objet R2 d'une fiche supprimée ; sauvegarde des photos R2.
- Dépôt public : vérifier avant chaque commit qu'aucune donnée personnelle (photos, avis réels, identifiants d'appareil) n'entre dans le dépôt, y compris dans les jeux de test.
