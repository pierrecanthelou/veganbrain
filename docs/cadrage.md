# Cadrage technique — Phase 1

Date des relevés : 2026-11-22. Toutes les versions, tous les prix et toutes les limites sont ceux affichés ce jour-là dans les sources citées. Le libellé « à sourcer » signale une affirmation de conception que je n'ai pas encore adossée à une documentation officielle. Le libellé « hypothèse » signale une estimation de ma part.

Sources non lues pour ce document : maquettes et design system (chemins non fournis). Aucun point ci-dessous n'en dépend. Les conclusions marquées ⟳ sont à revoir une fois les maquettes lues.

Aucun obstacle sérieux aux deux décisions prises (Kotlin Multiplatform, Cloudflare) n'a été identifié. Deux contraintes en découlent, chiffrées plus bas :
- il n'existe pas de synchronisation clé en main compatible avec D1 (§3) ;
- Cloudflare n'envoie pas de notifications push (§5).

**Statut : validé le 2026-11-22.** Décisions prises à la validation :
- Workers Paid, déjà souscrit (§2) ;
- dépôt public : CI GitHub Actions gratuite (§6) ; mutation avec Pitest seul, Arcmutate écarté le 2026-11-26 ;
- ~~un avis par personne~~ remplacé le 2026-11-22 : **un seul avis commun par carnet, une note commune**. Un conflit sur ce texte long reste possible (LWW, §3) et c'est assumé ;
- multi-utilisateur, « porte ouverte » (2026-11-22) : la V1 reste privée pour deux personnes, mais les données sont rangées par carnet avec ses membres, et l'authentification passe par une interface remplaçable. Les comptes publics et les obligations des stores viendront avec une éventuelle publication ;
- distribution par TestFlight et test interne Google Play : l'exigence d'API cible Play s'applique.

---

## 1. Interface : natif par plateforme ou Compose Multiplatform

### Options

- **A. Natif** (choix par défaut) : Jetpack Compose sur Android, SwiftUI sur iOS. Le module partagé expose l'état (StateFlow) et les intentions.
- **B. Compose Multiplatform (CMP) intégral** : une seule UI en Kotlin, rendue par Compose sur iOS.
- **C. Hybride** : une coque SwiftUI native (onglets, navigation), avec des écrans en CMP à l'intérieur. C'est l'architecture que JetBrains décrit pour adopter Liquid Glass (« you need a native SwiftUI shell »), à côté d'une voie UIKit pilotée depuis Kotlin.

### Faits établis

| Sujet | Constat | Source |
|---|---|---|
| Versions stables | Kotlin 2.4.21 (8 oct. 2026), CMP 1.12.2 (15 oct. 2026). Vérifié via l'API GitHub `releases/latest`. | https://kotlinlang.org/docs/releases.html, https://github.com/JetBrains/compose-multiplatform/releases |
| Statut CMP iOS | « The Android, iOS, and desktop targets of Compose Multiplatform are Stable. » | https://kotlinlang.org/docs/multiplatform/faq.html |
| Maturité des briques CMP | La CMP stable 1.12.2 embarque Material3 `1.12.0-alpha03` et Navigation / Navigation3 en `rc01`. Côté Jetpack, Material3 1.5.0, Navigation 2.10.2 et Navigation3 1.2.2 sont stables. La CMP 1.13.0 (Material3 rc, navigation stable) n'est encore qu'une pré-release (18 nov.), et Material3 Adaptive y repasse en `1.4.0-alpha01` (notes de v1.13.0-rc01, vérifié via l'API GitHub). | https://github.com/JetBrains/compose-multiplatform/releases/tag/v1.12.2, https://developer.android.com/jetpack/androidx/releases/compose-material3, …/navigation3 |
| Position de JetBrains | « Your most frequently used screens … are often better implemented with fully native UIs… For lower-impact areas, Compose Multiplatform is a strong fit. » | https://kotlinlang.org/docs/multiplatform/kmp-for-ios.html |
| Liquid Glass (iOS 26) | « you need a native SwiftUI shell » pour l'adopter dans CMP. | https://kotlinlang.org/docs/multiplatform/ios-liquid-glass.html |
| Saisie de texte iOS en CMP | La saisie native (UITextInput) est opt-in et expérimentale. Son passage par défaut figure à la feuille de route, sans engagement de date. | https://kotlinlang.org/docs/multiplatform/whats-new-compose-111.html, https://kotlinlang.org/docs/roadmap.html |
| Accessibilité CMP iOS | VoiceOver fonctionne sur les widgets Material. « ColorScheme … currently lacks out-of-the-box support for high-contrast colours. » | https://kotlinlang.org/docs/multiplatform/compose-ios-accessibility.html |
| Caméra en CMP | Aucune bibliothèque officielle. JetBrains documente une vue caméra iOS via `UIKitView` + `AVCaptureSession`. | https://kotlinlang.org/docs/multiplatform/compose-uikit-integration.html |
| Scan Android | ML Kit Barcode (EAN-8/EAN-13). Modèle embarqué +2,4 Mo, modèle non embarqué +200 Ko téléchargé par Play Services. CameraX `camera-mlkit-vision` 1.7.0 stable. | https://developers.google.com/ml-kit/vision/barcode-scanning/android, https://developer.android.com/jetpack/androidx/releases/camera |
| Scan iOS | `DataScannerViewController` (VisionKit, iOS 16+, puce A12 ou plus récente, vérifier `isSupported`). Repli possible sur `AVCaptureMetadataOutput` avec `.ean13`/`.ean8`. | https://developer.apple.com/documentation/visionkit/datascannerviewcontroller |
| Tablette Android | `NavigableListDetailPaneScaffold` : « In large windows, the list and detail panes appear side by side ». | https://developer.android.com/develop/ui/compose/layouts/adaptive/list-detail |
| Tablette iOS | `NavigationSplitView` (iOS 16+) se replie en pile sur iPhone. | https://developer.apple.com/documentation/swiftui/navigationsplitview |
| Tablette CMP | `material3.adaptive:adaptive:1.3.0` en commonMain. `ListDetailPaneScaffold` sur iOS via CMP non confirmé explicitement. | https://kotlinlang.org/docs/multiplatform/compose-adaptive-layouts.html |
| Android 16 grands écrans | Pour une cible API 36, les restrictions d'orientation sont ignorées si la plus petite largeur est ≥ 600 dp. Une dérogation existe (`PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY`), mais « The opt-out is temporary and won't apply when targeting API level 37 ». L'app doit donc être pensée pour la rotation sur tablette. | https://developer.android.com/about/versions/16/behavior-changes-16 |
| Swift ↔ Kotlin | Swift export : « currently in Alpha » (page native-swift-export). La page multiplatform-upgrade-app renvoie à SKIE ou KMP-NativeCoroutines pour exposer Flow et suspend avec annulation. SKIE 0.10.16 (10 nov. 2026) supporte Kotlin 2.4.21, sorti le 8 oct. : un mois de décalage. | https://kotlinlang.org/docs/native-swift-export.html, https://kotlinlang.org/docs/multiplatform/multiplatform-upgrade-app.html, https://github.com/touchlab/SKIE/releases |

### Tableau comparatif

| Critère | A. Natif | B. CMP intégral | C. Hybride |
|---|---|---|---|
| Scan caméra | Code plateforme direct (CameraX + ML Kit ; DataScannerViewController en SwiftUI) | Code plateforme quand même, plus une couche d'interop UIKitView | Comme A pour l'écran de scan |
| Tablette liste/détail | Composants stables et documentés des deux côtés | Briques adaptives multiplateformes, support iOS non confirmé | Natif pour la structure |
| Stabilité des dépendances UI | Toutes stables | Material3 alpha et navigation en RC dans la stable actuelle | Mixte |
| Accessibilité et conventions iOS | Natives | Bonnes sur Material, pas de contraste élevé, saisie de texte encore en transition | Natives pour la structure |
| Effort d'un développeur seul | Deux UI à écrire (environ ×2 sur la couche UI), plus l'interop SKIE | Une seule UI | Le plus complexe : deux technologies sur iOS |
| Dépendance tierce | SKIE (0.x, un tiers) : chaque montée de Kotlin attend sa compatibilité (environ un mois pour 2.4.21) | Aucune pour l'interop Swift | SKIE aussi |
| Tests E2E | Un jeu Maestro, mais avec des branches par plateforme pour les sélecteurs | Sélecteurs partagés : flux plus simples | Comme A |

### Recommandation

**Je confirme l'option A, l'UI native.** Pour un développeur seul, le coût est réel : deux couches UI. Il est compensé sur les trois critères demandés :
- **Scan** : le code reste spécifique à chaque plateforme, avec ou sans CMP.
- **Tablette** : les composants liste/détail sont stables et documentés des deux côtés.
- **Stabilité** : aucune dépendance UI en alpha ou en RC.

Deux inconvénients de A restent visibles dans le tableau :
- **SKIE**, outil tiers en 0.x, retarde chaque montée de Kotlin. KMP-NativeCoroutines, l'autre outil cité par JetBrains, ne réduit pas ce délai : sa v1.0.7 (« Updated Kotlin to 2.4.21 ») date du 15 nov. 2026, cinq jours après SKIE 0.10.16 (API GitHub `releases`, vérifié le 2026-11-22). Repli réel : rester une version de Kotlin en arrière. À terme, Swift export exporte `suspend fun` en `async` et les flows en `AsyncSequence`, mais il reste en alpha (https://kotlinlang.org/docs/native-swift-export.html).
- **Maestro** demande des branches par plateforme. Atténuation : mêmes identifiants d'accessibilité (`testTag` / `accessibilityIdentifier`) des deux côtés, fixés dans la table dorée.

Ces deux coûts sont plus faibles que l'alpha de Material3 et d'Adaptive dans CMP, qui touche directement la tablette.

Pour limiter le doublon :
- tout l'état et toute la logique d'écran vivent dans le module partagé (présentateurs ou ViewModel exposant un état immuable et recevant des intentions) ;
- les UI ne sont que des fonctions de cet état ;
- SKIE expose les Flow à Swift.

### Ce qui me ferait changer d'avis

- Les maquettes ⟳ montrent un design très custom, identique sur les deux plateformes, sans conventions iOS. L'avantage d'une seule UI l'emporterait alors (option B).
- CMP sort en stable avec Material3, la navigation **et Material3 Adaptive** stables (ce n'est pas le cas de 1.13.0-rc01, où Adaptive est en alpha), et `ListDetailPaneScaffold` est confirmé sur iOS.
- SKIE prend du retard sur plusieurs versions de Kotlin, ou son support s'arrête.
- Le temps disponible devient la contrainte principale, avant la qualité perçue sur iOS.

---

## 2. Architecture Cloudflare

### Services retenus

| Service | À quoi il sert | Coût | Limites qui comptent ici | À opérer soi-même |
|---|---|---|---|---|
| **Workers** | Héberger l'API (synchronisation, photos, authentification). | Free : 100 000 requêtes/jour, 10 ms CPU par invocation. Paid : 5 $/mois minimum, dont 10 M requêtes/mois et 30 M ms CPU. Pas de frais de sortie. | Free : 50 sous-requêtes par requête. Corps de requête : 100 Mo (dépend du plan du domaine). | Le code et le déploiement (`wrangler deploy`). Rien côté serveur. Plafond de dépense : il couvre « requests and CPU time across all Workers in your account », pas R2 ni les autres produits facturés à part (« A Workers spending limit does not apply to those charges » ; D1 n'est pas nommé dans cette liste). « A spending limit is not an exact maximum bill. » Une fois atteint, les requêtes reçoivent l'erreur `1103` (https://developers.cloudflare.com/workers/platform/spend-management/). |
| **D1** | Base SQLite serveur (produits, recettes, liens, journal de synchro). | Free : 5 M lignes lues/jour, 100 000 écrites/jour, 5 Go. Paid : 25 Md lues et 50 M écrites inclus par mois. | Base de 500 Mo (Free) ou 10 Go (Paid). **50 requêtes par invocation de Worker en Free** (1 000 en Paid) ; « a `db.batch()` call counts as a single query against it, not one query per statement ». Au-delà du quota du jour en Free, les requêtes échouent jusqu'à minuit UTC (appliqué depuis le 1ᵉʳ sept. 2026). | Les migrations (`wrangler d1 migrations create/apply`). Sauvegardes : Time Travel toujours actif, 7 jours (Free) ou 30 jours (Paid). Une rétention plus longue demande un export vers R2. Les photos (R2) ne sont pas couvertes : leur sauvegarde est un point ouvert pour la phase 2. |
| **R2** | Stocker les photos. | Gratuit jusqu'à 10 Go-mois, 1 M opérations de classe A et 10 M de classe B par mois. Ensuite 0,015 $/Go-mois. Sortie gratuite. | Une souscription R2 est à activer : « You need a Cloudflare account with an R2 subscription » (https://developers.cloudflare.com/r2/get-started/). Les URL présignées ne fonctionnent pas sur un domaine personnalisé. | Le bucket et son binding. Des clés API seulement si l'on choisit les URL présignées. |
| Durable Objects | Coordination temps réel entre clients (WebSocket). | Disponibles en Free (backend SQLite). | — | **Non retenu en V1** : sans temps réel requis, la synchro HTTP suffit (§3). |
| KV | Clé-valeur cohérent à terme. | Free : 1 000 écritures/jour. | « changes may take up to 60 seconds or more to be visible » | **Écarté** comme source de vérité. |

Sources :
- https://developers.cloudflare.com/workers/platform/pricing/ et https://developers.cloudflare.com/workers/platform/limits/
- https://developers.cloudflare.com/d1/platform/pricing/ (mise à jour 30 oct. 2026), https://developers.cloudflare.com/d1/platform/limits/, https://developers.cloudflare.com/d1/reference/time-travel/, https://developers.cloudflare.com/changelog/post/2026-09-01-d1-free-tier-limit-enforcement/
- https://developers.cloudflare.com/r2/pricing/ (mise à jour 30 oct. 2026), https://developers.cloudflare.com/r2/api/s3/presigned-urls/
- https://developers.cloudflare.com/durable-objects/platform/pricing/
- https://developers.cloudflare.com/kv/concepts/how-kv-works/

Ordre de grandeur (hypothèse) : quelques milliers de photos compressées à environ 300 Ko font environ 1 Go, sous les 10 Go gratuits de R2. Deux personnes restent loin des quotas quotidiens de D1 et de Workers.

### Photos : passer par le Worker ou par URL présignée

| | Via le Worker (binding R2) | URL présignée (PUT direct vers R2) |
|---|---|---|
| Secrets à gérer | Aucun : le binding suffit | Clés API R2 en secret Worker, plus la signature SigV4 (aws4fetch) |
| Limite de taille | Corps de requête limité selon le plan du domaine (100 Mo, voir Workers), largement suffisant | Non indiquée sur la page des URL présignées (à sourcer) |
| Domaine | Celui du Worker | Domaine S3 de R2 obligatoire |

Lecture des photos : `GET` via le Worker avec le même jeton (§ authentification), bucket non public. Le client garde la photo en cache local.

**Recommandation : passer par le Worker.** Les photos sont petites, il n'y a aucun secret de plus à gérer et l'authentification est la même que pour l'API. Le coût CPU d'un envoi en flux est « à sourcer ». Contrainte R2 : un flux passé à `put()` doit avoir une longueur connue (« its length must be known: for example, a request body with a `Content-Length` header »), sinon `put()` lève une `TypeError` (https://developers.cloudflare.com/r2/api/workers/workers-api-reference/). L'envoi depuis un fichier (§3) devrait fournir cet en-tête (hypothèse, à vérifier sur les deux plateformes).

**Ce qui me ferait changer d'avis** : des photos lourdes (vidéo, haute définition non compressée), ou un coût CPU d'envoi qui pèse sur la facture Paid. On passerait alors aux URL présignées.

### Authentification de deux utilisateurs

| Option | Principe | Identité par personne | Coût | À opérer | Source |
|---|---|---|---|---|---|
| a. Jeton de service Access | En-têtes `CF-Access-Client-Id/Secret` | Non : la page les destine aux systèmes automatisés (« You can provide automated systems with service tokens »). Un seul secret, intégré à l'app. | Zero Trust : gratuit jusqu'à 50 utilisateurs selon une architecture de référence seulement (incertain) | Une organisation Zero Trust | https://developers.cloudflare.com/cloudflare-one/access-controls/service-credentials/service-tokens/ |
| b. Access Managed OAuth | « The end user authenticates with their identity provider, and the client receives an OAuth access token » | Oui | Idem a | Organisation Zero Trust. Non documenté pour une app mobile : les clients cités sont « CLIs, AI agents, SDKs, and scripts ». | https://developers.cloudflare.com/changelog/post/2026-03-20-managed-oauth/ |
| c. Sign in with Apple / Google | Le Worker vérifie l'ID token (bibliothèque `jose`, JWKS) | Oui | Aucun coût propre : les comptes Apple Developer et Play Console sont déjà nécessaires à la distribution (tarifs : Exigences transverses, ligne Distribution) | Configuration chez Apple et Google. Cloudflare ne documente le motif `jose` + JWKS que pour Access, pas pour Apple ni Google. | https://developers.cloudflare.com/cloudflare-one/access-controls/applications/http-apps/authorization-cookie/validating-json/ |
| **d. Jeton par appareil** | Un jeton aléatoire par téléphone, créé une fois par script. D1 stocke seulement son empreinte. L'app l'envoie en `Authorization: Bearer`. | Oui (table `device → user`) | Rien de plus que Workers et D1 | Un script d'enrôlement et une table. Révoquer un appareil = supprimer une ligne. | Mécanismes : secrets Workers https://developers.cloudflare.com/workers/configuration/secrets/, binding de limitation de débit https://developers.cloudflare.com/workers/runtime-apis/bindings/rate-limit/. Empreinte via Web Crypto et stockage Keychain / Keystore : à sourcer. |

**Recommandation : option d.** C'est le moins à opérer pour deux personnes et quatre appareils au plus. Il n'y a ni fournisseur d'identité ni organisation Zero Trust, et on sait qui a modifié quoi, ce qui sert à la synchro. Le binding de limitation de débit freine la force brute, mais « Rate limits are approximate and apply separately in each Cloudflare location ». Le compte est en Workers Paid, donc la question de sa disponibilité en Free ne se pose plus. Un jeton aléatoire de 256 bits rend de toute façon la force brute irréaliste (hypothèse).

**Ce qui me ferait changer d'avis** :
- plus de deux utilisateurs ;
- un besoin de récupération de compte en libre-service ;
- un besoin de connexion sur un nouvel appareil sans passer par moi.

Dans ces cas, je passerais à l'option c.

### Plan Free ou Paid

**Décision (2026-11-22) : Workers Paid, déjà souscrit.** Ce que cela change pour ce projet :
- D1 : 1 000 requêtes par invocation au lieu de 50, pas de blocage quotidien. Les quotas sont mensuels, et un dépassement est facturé au lieu de bloquer (25 Md de lignes lues et 50 M de lignes écrites inclus).
- Workers : CPU par requête de 30 s par défaut au lieu de 10 ms, relevable jusqu'à 5 min dans la configuration (« 5 min (default: 30 seconds) », page limits), 10 000 sous-requêtes.
- Time Travel D1 sur 30 jours au lieu de 7.
- Le regroupement des écritures en `db.batch()` reste utile pour l'atomicité, plus pour tenir une limite.

Le coût réel dépend de ce qui est déjà consommé sur le compte : l'usage de deux personnes reste un ordre de grandeur sous les quotas inclus (hypothèse).

Observabilité : Workers Logs en Paid, « 20 million included per month +$0.60 per additional million », rétention 7 jours (https://developers.cloudflare.com/workers/observability/logs/workers-logs/). À partir du 1ᵉʳ décembre 2026 (« This pricing takes effect on December 1, 2026 »), en Paid : 50 Go d'ingestion et 10 Go-mois de stockage inclus par cycle, puis 0,25 $/Go et 0,10 $/Go-mois (https://developers.cloudflare.com/observability/pricing/).

### Secrets et dépôt public

Le dépôt est public (décisions en tête). Règles :
- `.dev.vars*` et `.env*` dans `.gitignore` ; les secrets du Worker passent par les secrets Wrangler, jamais par `vars` (« Do not use `vars` to store sensitive information in your Worker's Wrangler configuration file. Use secrets instead. », https://developers.cloudflare.com/workers/configuration/secrets/) ;
- les jetons d'appareil ne vont ni dans le dépôt ni dans les journaux de CI : le script d'enrôlement les affiche une seule fois ;
- les clés de signature Android et iOS et les secrets de CI vont dans les secrets GitHub. Le mécanisme et son comportement sur les PR venant de forks sont à sourcer en phase 2 ;
- un scan de secrets tourne en CI (outil à choisir et à sourcer en phase 2).

---

## 3. Synchronisation et hors-ligne

### Constat

Aucune solution clé en main ne fonctionne avec D1 :

| Solution | Pourquoi elle ne convient pas | Source |
|---|---|---|
| PowerSync (SDK KMP v1.16.0) | Base source « Postgres, MongoDB, MySQL, SQL Server or Convex ». D1 absent. | https://docs.powersync.com/installation/database-setup |
| ElectricSQL | « read-path sync engine for Postgres », pas de SDK Kotlin documenté | https://electric.ax/docs/intro |
| Couchbase Lite / Kotbase | Demande Sync Gateway et Couchbase Server. Kotbase est communautaire. | https://docs.couchbase.com/sync-gateway/current/introduction.html |
| Realm Device Sync | « reached end-of-life and were removed on September 30, 2025 » | https://www.mongodb.com/docs/device-sdks-deprecation/ |
| partysync (Cloudflare) | « Experimental », en TypeScript, sans client Kotlin | https://github.com/cloudflare/partykit/tree/main/packages/partysync |

Ce n'est pas un obstacle sérieux à Cloudflare : le modèle ci-dessous est de taille modeste, mais il est **à écrire nous-mêmes**.

### Base locale

| | SQLDelight 2.4.1 | Room 3.0.3 (KMP) |
|---|---|---|
| Statut | Stable (6 oct. 2026). Drivers Android et Native. | Stable (9 sept. 2026). iOS supporté. KSP obligatoire. Le guide KMP de Google utilise une version alpha (3.1.0-alpha04) dans son exemple. |
| Schéma | SQL écrit à la main, d'où le code généré | Annotations Kotlin |
| Sources | https://github.com/sqldelight/sqldelight/releases, https://sqldelight.github.io/sqldelight/latest/multiplatform_sqlite/ | https://developer.android.com/jetpack/androidx/releases/room3, https://developer.android.com/kotlin/multiplatform/room |

**Recommandation : SQLDelight.** Le schéma local et le schéma D1 sont tous deux en SQLite, écrits en SQL. On garde un seul langage de migration des deux côtés, et le SQL reste relisable. Je passerais à Room si tu préfères un schéma décrit par annotations.

### Modèle proposé

1. **Identifiants générés sur le téléphone** (UUID), pour créer hors-ligne sans aller-retour serveur.
2. **Écriture locale d'abord** : chaque modification est appliquée à la base locale, et une ligne est ajoutée dans une table `outbox` dans la même transaction. Cette ligne contient l'entité, l'id, les champs modifiés, un horodatage logique et l'appareil.
3. **Envoi** (`POST /sync/push`) : lots bornés, écrits en un `db.batch()` (§2), idempotents grâce à un identifiant de mutation. La ligne d'outbox n'est supprimée qu'après l'accusé de réception.
4. **Réception** (`GET /sync/pull?since=<curseur>`) : le serveur numérote chaque changement accepté par une séquence croissante. Le client garde le dernier curseur reçu.
5. **Déclencheurs** :
   - retour au premier plan ;
   - retour du réseau (Android `registerDefaultNetworkCallback` avec `NET_CAPABILITY_VALIDATED` ; iOS `NWPathMonitor`) ;
   - tâche périodique (Android WorkManager, avec contrainte réseau et 15 min minimum ; iOS `BGAppRefreshTask`, que le système lance quand il le décide, avec environ 30 s d'exécution et sans garantie).
   - Sources : https://developer.android.com/develop/connectivity/network-ops/reading-network-state, https://developer.apple.com/documentation/network/nwpathmonitor, https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work, https://developer.apple.com/documentation/backgroundtasks/choosing-background-strategies-for-your-app
6. **Pas de temps réel en V1** : on tire les changements au premier plan. Les Durable Objects ne seraient utiles que pour un canal temps réel.

### Conflits : nous deux sur la même fiche

| Stratégie | Comportement | Coût d'écriture | Défaut |
|---|---|---|---|
| LWW par champ, ordre d'arrivée au serveur | Le dernier champ reçu par le serveur gagne (le défaut documenté par PowerSync) | Minimal | Une modification faite hors-ligne à 10 h et envoyée à 12 h écrase une modification faite en ligne à 11 h |
| **LWW par champ, horloge logique hybride (HLC)** | Le champ modifié le plus tard *en temps réel de saisie* gagne. Les autres champs de la fiche fusionnent. | Faible : une horloge et une comparaison par champ | HLC non documentée dans les sources officielles consultées (choix de conception). Repose sur des horloges d'appareils à peu près justes : une horloge très décalée ferait gagner ses modifications (hypothèse, non vérifiée). |
| CRDT (Automerge…) | Fusion automatique, y compris du texte | Élevé : aucun binding Kotlin listé sur la page citée | Disproportionné pour deux personnes |

Source LWW : https://docs.powersync.com/usage/lifecycle-maintenance/handling-update-conflicts. Automerge : https://automerge.org/docs/hello/.

**Recommandation : LWW par champ avec HLC.** Deux modifications de champs différents (l'un la note, l'autre le lieu d'achat) fusionnent sans perte. Un vrai conflit sur le même champ est rare à deux, et sa règle reste simple à expliquer.

**Ce qui me ferait changer d'avis** : vous écrivez souvent en même temps dans le même texte long (avis, ajustements). Dans ce cas, il faudrait un CRDT sur ces seuls champs. L'avis par personne a été écarté le 2026-11-22 (décisions en tête).

### Suppressions hors-ligne

- **Pierres tombales** : une suppression pose `deleted_at` au lieu d'effacer la ligne. Elle se propage par la même synchro.
- **La suppression gagne** sur une modification concurrente. C'est la règle documentée par PowerSync (« Deletes always win ») et par Couchbase Lite (« A deleted document (that is, a tombstone) always wins over a document update », https://docs.couchbase.com/couchbase-lite/current/android/conflict.html).
- **Cascade produit → recettes** : supprimer un produit pose aussi une pierre tombale sur ses liens `recette_produit`. La cascade est exécutée côté client et rejouée côté serveur. Si l'autre personne lie ce produit à une recette pendant ce temps, le lien arrive avec un produit supprimé : le serveur l'ignore et le marque supprimé. Aucune source officielle ne traite ce cas : c'est une décision de conception.
- **Suppression d'une recette** : pose aussi une pierre tombale sur ses liens `recette_produit` ; les produits restent (décision de conception).
- **Confirmation** : la demande de confirmation est une affaire d'interface (glissement ou bouton) ; elle sera décrite avec les maquettes ⟳.
- Purge des pierres tombales : après N jours, quand tous les appareils connus ont synchronisé au-delà (N à trancher plus tard). Point ouvert, relevé en contre-revue : un appareil enrôlé qui ne revient jamais bloquerait la purge, et un appareil qui revient après la purge avec une file ancienne doit être forcé à une resynchronisation complète. Règle à fixer en phase 2.

**Ce qui me ferait changer d'avis** : un besoin d'annuler une suppression, ou de « résurrection » si l'autre personne modifie le produit. On passerait alors à une corbeille de 30 jours, où une modification concurrente restaure le produit.

### Photos prises hors-ligne

- La photo est enregistrée comme fichier dans le stockage de l'app. La fiche référence ce fichier local.
- Une ligne `photo_upload` en file indique le fichier et la fiche.
- **Envoi** via le Worker (§2) quand le réseau revient :
  - Android : WorkManager, contrainte réseau ;
  - iOS : `URLSession` d'arrière-plan, qui n'accepte que les envois depuis un fichier (« Only upload tasks from a file are supported »).
  - Source iOS : https://developer.apple.com/documentation/foundation/downloading-files-in-the-background
- Une fois l'envoi accusé, la fiche reçoit la clé R2. Le fichier local reste en cache.
- Point ouvert, relevé en contre-revue : l'objet R2 d'une fiche supprimée, et une photo arrivée après la suppression de sa fiche, doivent être effacés, par exemple à la purge de la pierre tombale. À fixer en phase 2.

### Ce que nous écrivons nous-mêmes

- l'outbox ;
- les points d'API push et pull ;
- l'HLC et la fusion par champ ;
- les pierres tombales et la cascade ;
- la file de photos ;
- les migrations miroir local/D1 ;
- les tests de convergence (deux clients simulés), qui iront dans la table dorée.

Client HTTP : Ktor 3.6.1 (moteurs Darwin et OkHttp, https://ktor.io/docs/client-engines.html). Sérialisation : kotlinx.serialization 1.13.0.

---

## 4. Identification des produits

### Base par code-barres : Open Food Facts (OFF)

| Point | Constat | Source |
|---|---|---|
| API | v3 recommandée (« v3 (latest: v3.6) ✅ Current »). v2 dépréciée mais seule à offrir la recherche structurée. Lecture : `GET /api/v3/product/{ean}.json?fields=…`, testée le 2026-11-22. | https://openfoodfacts.github.io/openfoodfacts-server/api/ |
| Champs utiles | `product_name`, `brands`, `categories`, `image_front_url`, `labels_tags`, `ingredients_analysis_tags` (valeurs : vegan, non-vegan, maybe-vegan, vegan-status-unknown) | Idem, et https://openfoodfacts.github.io/documentation/docs/Product-Opener/api/tutorials/get-ingredient-related-analysis/ |
| Débit | « 15 req/min/IP address for all read product queries » ; recherche 10 req/min. « If your requests come from your users directly (ex: mobile app), the rate limits apply per user. » Vérifié moi-même. | Page API |
| User-Agent | Obligatoire, « AppName/Version (ContactEmail) » | Page API |
| Lecture | Sans authentification. L'écriture demande un compte. | Page API |
| Licences | Base sous ODbL, contenus sous DbCL, images sous CC BY-SA. « Derivative works must be shared under the same conditions. » Vérifié moi-même. | https://world.openfoodfacts.org/terms-of-use |
| SDK Kotlin | « early development phase », « OpenFoodFacts v0 (Legacy) API », « not currently published in any of the popular Maven repositories ». **Inutilisable** : un petit client Ktor maison suffit. | README du sous-dossier `openfoodfacts-kotlin/` de https://github.com/openfoodfacts/openfoodfacts-kotlin |

### Couverture chiffrée (API v2 search, 2026-11-22)

| Requête | Nombre |
|---|---|
| Produits vendus en France (`countries_tags_en=france`) | 1 298 999 |
| …avec le **label** vegan (`labels_tags=en:vegan`), vérifié moi-même | **31 984** |
| …avec l'**analyse d'ingrédients** vegan | 82 156 (inclut des produits vegan « par défaut », par exemple de l'eau de source) |

Limite du chiffre : OFF ne dit pas combien de produits vegan sont réellement vendus en France. Je ne peux donc pas donner un taux de couverture des produits que vous achetez.

**Proposition** : mesurer en V1 le taux « scan trouvé / scan inconnu », journalisé localement, et décider sur cette mesure.

### Conditions d'usage

OFF demande de mentionner la licence et d'attribuer la paternité à Open Food Facts (terms-of-use). L'ODbL vise « public use of the database » et impose le partage à l'identique si l'on « publicly use any adapted version of this database » (https://opendatacommons.org/licenses/odbl/summary/). Savoir si une app privée à deux personnes relève d'un usage public est une question juridique que je ne tranche pas.

**Recommandation (peu coûteuse dans tous les cas)** :
- mention « Données Open Food Facts, ODbL », avec lien, sur la fiche préremplie ;
- pas de redistribution de leurs données ;
- contribution en retour vers OFF hors V1 (Parking).

### Hors réseau en magasin

Un produit inconnu de nous ne peut pas être prérempli sans réseau. Le scan enregistre alors :
- le code-barres ;
- la photo ;
- un rappel (§5).

Le préremplissage se fait au retour du réseau.

### Autres bases

| Base | Conditions | Source |
|---|---|---|
| Open Products Facts | Hors alimentaire | https://world.openproductsfacts.org/ |
| Verified by GS1 | 30 requêtes/jour en manuel, API réservée aux membres | https://support.gs1.org/support/solutions/articles/43000734077 |
| UPCitemdb | Gratuit 100 requêtes/jour, puis 99 $/mois | https://devs.upcitemdb.com/ |

Aucune n'est utile en V1.

### Reconnaissance par photo : coût réel

| Option | Coût | Ce qu'elle fait vraiment | Source |
|---|---|---|---|
| OCR sur l'appareil (ML Kit Text Recognition v2, Apple Vision) | Gratuit, hors-ligne | Lit le texte de l'emballage. Il faut ensuite une recherche OFF par texte (10 req/min, pas de recherche à la frappe). | https://developers.google.com/ml-kit/guides, https://developer.apple.com/documentation/vision/vnrecognizetextrequest |
| Google Vision Product Search | 1 000 prédictions gratuites, puis 4,50 $ / 1 000 | Demande **un index d'images de référence que l'on constitue soi-même**. Inadapté. | https://cloud.google.com/vision/product-search/docs, https://cloud.google.com/vision/product-search/pricing |
| Workers AI (modèle vision) | 10 000 neurons/jour gratuits, puis 0,011 $ / 1 000 neurons | Coût par image non publié (nombre de jetons par image inconnu). Résultat non déterministe. | https://developers.cloudflare.com/workers-ai/platform/pricing/ |
| Robotoff (OFF) | Gratuit | Exige un code-barres : n'identifie pas un produit depuis une photo brute | https://openfoodfacts.github.io/robotoff/references/api/ |

**Recommandation : V1 sans reconnaissance par photo.**
- Le scan se fait par code-barres.
- Si le code est inconnu, ou sans code-barres : création à la main avec photo.
- En V2, si le taux de scans inconnus mesuré est élevé : OCR sur l'appareil pour préremplir nom et marque, puis recherche OFF.

**Choix du scanner** :
- **Android** : CameraX + ML Kit avec le modèle *embarqué* (+2,4 Mo). C'est préféré au Google code scanner et au modèle non embarqué, téléchargés par les services Google Play. Ce téléchargement peut être demandé dès l'installation (`com.google.mlkit.vision.DEPENDENCIES` = `barcode_ui`) ou via `ModuleInstallClient` (https://developers.google.com/ml-kit/vision/barcode-scanning/code-scanner). Le modèle embarqué supprime quand même tout risque en magasin sans réseau.
- **iOS** : `DataScannerViewController`, avec repli `AVCaptureMetadataOutput` si `isSupported` est faux.

### Ce qui me ferait changer d'avis

- Un taux de scans inconnus élevé dès les premières semaines : on avancerait l'OCR.
- Un besoin de consulter OFF hors réseau : on ajouterait un cache local de produits OFF (Parking).

---

## 5. Rappels

### Besoin

« Noter plus tard » est un rappel **local** : programmé sur le téléphone qui a scanné, au moment du scan. Il n'a pas besoin du serveur.

L'envoi distant ne servirait qu'à prévenir l'autre personne, ce qui n'est pas demandé en V1. L'absence de service push chez Cloudflare ne bloque donc pas la V1.

### Options

| | Notifications locales | Push distant (FCM) |
|---|---|---|
| iOS | `UNTimeIntervalNotificationTrigger` / `UNCalendarNotificationTrigger`. Autorisation par `requestAuthorization` : le premier appel affiche la demande, les suivants non. Variante *provisional* : livrée en silence dans le centre de notifications, sans demande. Limite : 64 notifications en attente par app (confirmé par un ingénieur Apple sur le forum, absent de la doc de référence). | FCM transmet vers APNs (clé .p8 téléversée dans Firebase) |
| Android | `POST_NOTIFICATIONS` à demander à l'exécution (Android 13+), canal obligatoire (8.0+). Programmation par alarme **inexacte** `setAndAllowWhileIdle()` : déclenchée « within one hour » sur Android 12+. **Aucune permission d'alarme exacte** : `SCHEDULE_EXACT_ALARM` est refusée par défaut pour les cibles 13+. Les alarmes disparaissent à l'extinction : reprogrammer au redémarrage (`RECEIVE_BOOT_COMPLETED`). | FCM HTTP v1 |
| Côté Cloudflare | Rien | Aucun service push. Un Worker peut appeler FCM HTTP v1 (jeton OAuth 2.0 d'un compte de service). APNs exige HTTP/2, et l'HTTP/2 sortant de `fetch()` n'est pas documenté : FCM serait la voie pour iOS aussi. |

Sources :
- https://developer.apple.com/documentation/usernotifications/scheduling-a-notification-locally-from-your-app
- https://developer.apple.com/documentation/usernotifications/asking-permission-to-use-notifications
- https://developer.apple.com/forums/thread/811171
- https://developer.android.com/develop/ui/views/notifications/notification-permission
- https://developer.android.com/develop/ui/views/notifications/channels
- https://developer.android.com/about/versions/14/changes/schedule-exact-alarms
- https://developer.android.com/develop/background-work/services/alarms/schedule
- https://firebase.google.com/docs/cloud-messaging/send/v1-api
- https://developer.apple.com/documentation/usernotifications/sending-notification-requests-to-apns

### Recommandation

Notifications locales uniquement en V1 :
- **Module partagé** : la règle « quand rappeler » et « annuler le rappel ». Le rappel est annulé dès que le produit est noté, sur ce téléphone ou via la synchro venant de l'autre.
- **Planificateur** : une interface implémentée par plateforme, en deux petites implémentations maison.
- **KMPNotifier** : écarté. Un seul mainteneur, et il ajoute FCM, qu'on n'utilise pas (https://github.com/mirzemehdi/KMPNotifier).
- **Autorisation** : demandée au moment où l'on choisit « me rappeler », pas au lancement de l'app.
- **Limite assumée** : sans push, l'annulation venant de l'autre personne n'arrive qu'après une synchro (premier plan, ou tâche de fond sans garantie). Le rappel peut donc sonner alors que le produit a déjà été noté par l'autre. Atténuation : à l'ouverture depuis le rappel, synchro puis message « déjà noté » si c'est le cas.
- **Autres limites Android** : l'alarme inexacte sonne dans l'heure « unless any battery-saving restrictions are in effect such as battery saver or Doze », et le redémarrage n'est reçu que si « the app has already been launched by the user at least once » (https://developer.android.com/develop/background-work/services/alarms/schedule).
- Côté Android, WorkManager (déjà utilisé pour la synchro) pourrait remplacer AlarmManager + `RECEIVE_BOOT_COMPLETED` : comparaison à faire en phase 2 (non vérifiée).

### Ce qui me ferait changer d'avis

Vouloir que le rappel arrive aussi chez l'autre personne. Il faudrait alors FCM sur les deux plateformes, appelé depuis un Worker, avec un projet Firebase et un compte de service à opérer.

---

## 6. Outillage de test

| Niveau | Outil | Version / statut | Source |
|---|---|---|---|
| Unitaire, module partagé | `kotlin.test` dans commonTest, exécuté sur JVM et sur simulateur iOS (`allTests`) | Kotlin 2.4.21 | https://kotlinlang.org/docs/multiplatform/multiplatform-run-tests.html |
| Coroutines / Flow | `kotlinx-coroutines-test` (`runTest`) + Turbine | 1.11.0 ; Turbine 1.2.1 | https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/, https://github.com/cashapp/turbine |
| Assertions (option) | Kotest assertions : « supported on all targets » | 6.2.7 (11 nov. 2026) | https://kotest.io/docs/quickstart |
| Mutation | Pitest seul sur la cible **JVM** du module partagé, par une tâche Gradle maison (Arcmutate écarté le 2026-11-26, voir plus bas) | Pitest 1.30.2 | https://pitest.org/quickstart/commandline/, `spikes/mutation-kmp/README.md` |
| End to end, deux plateformes | **Maestro** (comparaison ci-dessous) | CLI 2.12.1, gratuit. iOS : « Full support for simulators ». | https://docs.maestro.dev/get-started/supported-platform |
| Accessibilité Android | Compose : `enableAccessibilityChecks()` (ATF, Compose 1.8+). Taille de texte : `DeviceConfigurationOverride.FontScale`. | — | https://developer.android.com/develop/ui/compose/accessibility/testing, https://developer.android.com/develop/ui/compose/testing/common-patterns |
| Accessibilité iOS | XCUITest `performAccessibilityAudit` (iOS 17+), catégories `.dynamicType` et `.contrast` | Version Xcode minimale non établie | https://developer.apple.com/documentation/xcuiautomation/xcuiapplication/performaccessibilityaudit(for:_:) |

### End to end : options

| Option | Deux plateformes, un seul script | Coût | Avec deux UI natives | Source |
|---|---|---|---|---|
| **Maestro** | Oui : flux YAML, branches par plateforme (`runFlow` avec `when: platform: Android`, https://docs.maestro.dev/maestro-flows/flow-control-and-logic/conditions) | CLI gratuite ; Maestro Cloud payant (« Cloud $250 /device/mo », https://maestro.dev/pricing) | Sélecteurs par identifiants d'accessibilité communs | https://docs.maestro.dev/get-started/supported-platform |
| Appium | Oui : « Cross-platform test automation for native, hybrid, mobile web and desktop apps » ; pilotes iOS et Android | Gratuit (open source) | Idem, mais des tests écrits en code et un serveur Appium à faire tourner | https://github.com/appium/appium |
| Natif par plateforme | Non : Compose UI Test (`createComposeRule`) sur Android, XCUITest sur iOS | Gratuit | Deux jeux de tests à écrire et à maintenir | https://developer.android.com/develop/ui/compose/testing, https://developer.apple.com/documentation/xctest |

**Recommandation : Maestro** pour les parcours critiques (scan, création, suppression avec confirmation, hors-ligne), car un seul script couvre les deux plateformes, sans serveur à opérer. Les tests natifs restent pour l'accessibilité (ligne ci-dessus) et les cas d'écran isolés.

**Ce qui me ferait changer d'avis** : Maestro ne sait pas piloter un parcours critique (caméra, autorisations système) sur l'une des plateformes. On passerait à Appium pour ce parcours, ou au test natif.

### Mutation : le point délicat

| Option | Pour | Contre |
|---|---|---|
| **Pitest + Arcmutate, cible JVM** | Seul chemin documenté pour Kotlin (« via the Arcmutate kotlin plugin ») | Payant : la page d'accueil, relue le 2026-11-26, affiche Base à 8 $ et Pro à 12 $/utilisateur/mois ; les 13 $/mois de la page d'abonnement n'ont pas été relus (page rendue en JavaScript). Gratuit en open source (« We provide free licenses for open-source projects. Contact us to learn more. », https://www.arcmutate.com/, relu le 2026-11-26 ; contact support@arcmutate.com, https://docs.arcmutate.com/licence.html) ou en évaluation (https://subscribe.arcmutate.com/trial, durée non vérifiée). Le plugin Gradle pitest ne supporte pas KMP (ticket #234 ouvert depuis 2020) : il faudra une tâche Gradle maison. |
| Pitest seul, cible JVM | Gratuit | Pitest mute le bytecode JVM. Mesuré par le spike du 2026-11-26 : sur 38 mutants, au moins 12 portent sur du code généré par Kotlin (accesseurs, références de propriété, `!!`, coroutine), dont 6 non tués ou en dépassement de délai. |
| kaputt (mutants en IR Kotlin) | « Verified … against jvm, iosSimulatorArm64 » | Projet d'août 2026, une étoile, un seul auteur |

**Recommandation** :
- Tranché le 2026-11-26 : **Pitest seul** sur la cible JVM du module partagé, Arcmutate écarté (décision de l'utilisateur du 2026-11-26 : pas de licence gratuite ; la page citée plus haut annonce pourtant des licences open source gratuites, conditions non relues). Le spike du 2026-11-26 montre que Pitest tourne sur la cible `jvm()` d'un module KMP par une tâche Gradle maison, sans extraire de module JVM (`spikes/mutation-kmp/README.md`). Conséquence : chaque `expect` de `commonMain` a un `actual` pour la JVM. Les mutants de code généré par Kotlin restent dans le score. Le filtre `--avoidCallsTo` écarte 4 de ces 6 mutants de code généré non tués ou en dépassement de délai, mais ligne entière, au prix d'un vrai mutant tué : il n'est pas activé.
- **Seuil** proposé, sans recommandation officielle trouvée : 70 % au départ sur le domaine (règles de la table dorée, fusion de synchro), relevé à 80 % une fois stable.
- **Fréquence** : nuit et avant chaque version, pas sur chaque PR. Pitest a sa propre analyse incrémentale, marquée expérimentale (« PIT contains an experimental feature [...] incremental analysis », https://pitest.org/quickstart/incremental_analysis/ ; options `--historyInputLocation` et `--historyOutputLocation`). Non essayée par le spike.

### CI

| Option | Coût | Rôle |
|---|---|---|
| GitHub Actions Linux | 0,006 $/min. Gratuit en dépôt public. 12 $ inclus avec GitHub Free. | Tests partagés JVM, Android, émulateur avec KVM, Maestro Android |
| GitHub Actions macOS | 0,062 $/min, environ 10 fois Linux. Le montant inclus est en dollars, « not a fixed number of minutes », et le macOS y puise à son tarif. Sans effet en dépôt public : « The use of standard GitHub-hosted runners is free: In public repositories ». | Tests iOS, Maestro iOS |
| Xcode Cloud | 25 h/mois incluses avec l'Apple Developer Program (« Membership includes 25 compute hours/month », relu le 2026-11-26) | Alternative pour iOS |

Sources : https://docs.github.com/en/billing/concepts/product-billing/github-actions, https://docs.github.com/en/actions/reference/runners/github-hosted-runners, https://developer.apple.com/xcode-cloud/, https://developer.apple.com/programs/whats-included/

**Recommandation** : GitHub Actions avec Linux sur chaque PR, et macOS limité à la branche principale et à la nuit. Je passerais à Xcode Cloud si la facture macOS dépasse quelques dollars par mois.

Tranché le 2026-11-22 : dépôt public. La CI sur runners standard est gratuite, macOS compris (page de facturation ci-dessus), et la mutation se fait avec Pitest seul, gratuit (Arcmutate écarté le 2026-11-26). Limiter macOS à la branche principale et à la nuit devient un choix de rapidité, plus de coût.

### Ce qui me ferait changer d'avis

- ~~Le spike de mutation échoue des deux façons : on ferait de la mutation sur le seul module domaine JVM.~~ Tombé le 2026-11-26 : Pitest seul tourne sur la cible `jvm()` (`spikes/mutation-kmp/README.md`).
- Maestro ne peut pas piloter l'écran caméra en simulateur (non vérifié) : il faudrait des E2E de scan avec un scanner simulé injecté.

---

## Exigences transverses relevées

| Exigence | Constat |
|---|---|
| Zones réservées de l'écran | L'affichage bord à bord s'applique par défaut pour une cible Android 15, et Android 16 n'offre plus d'opt-out. Google Play exige une cible Android 16 (API 36) depuis le 31 août 2026 (https://developer.android.com/google/play/requirements/target-sdk). Le bord à bord est donc obligatoire dès la V1. Côté iOS, safe areas SwiftUI. |
| Mode sombre | Android : `isSystemInDarkTheme()`. iOS : couleurs sémantiques (Apple : « Choose semantic colors instead of fixed color values »). À porter par les tokens du design system ⟳. |
| Navigation typée | Android : Navigation3 1.2.2 stable. iOS : `NavigationStack` avec un chemin typé (à sourcer en phase 2). |
| Distribution | Non traitée par le cahier des charges : Play (test interne) et TestFlight, ou installation directe. La page Play exclut de l'exigence d'API cible les « Permanently private apps that are restricted to users in a specific organization and intended for internal distribution only » ; l'applicabilité à un couple n'est pas établie. Tranché le 2026-11-22 : TestFlight et test interne Google Play (décisions en tête), donc l'exigence d'API cible Play s'applique. Coûts des comptes développeur, relevés le 2026-11-26 : voir « Comptes développeur » ci-dessous. |
| Modèle des recettes | Champ « source » (lien Instagram, livre, adaptation) et ajustements : modèle à fixer en phase 2 avec les maquettes ⟳. |

### Comptes développeur (relevés le 2026-11-26)

| | Apple Developer Program | Google Play Console |
|---|---|---|
| Tarif | **99 USD par année d'adhésion**, récurrent : « The Apple Developer Program is 99 USD per membership year. Prices may vary by region and are listed in local currency during the enrollment process. » Aucun montant en euros publié : la page française dit « 99 USD […] prix à convertir dans la devise locale, le cas échéant » et demande « une carte de crédit acceptant les facturations en USD ». | **25 USD, une seule fois** : « There is a US$25 one-time registration fee. » |
| Nécessaire pour notre distribution | Oui : dans le tableau des adhésions, TestFlight n'est coché que pour « Apple Developer Program member », ni pour le compte gratuit ni pour l'Enterprise Program. | Oui : le test interne passe par la Play Console. |
| Test interne | Jusqu'à 100 testeurs internes, qui doivent être membres de l'équipe : « up to 100 members of your development team who hold the Account Holder, Admin, App Manager, Developer, or Marketing role ». L'autre personne doit donc être ajoutée à l'équipe App Store Connect avec l'un de ces rôles, ou passer par les testeurs externes (jusqu'à 10 000), pour lesquels « beta app description and beta app review information are required ». | Jusqu'à 100 testeurs : « An internal test can have up to 100 testers per app. » Aucune condition d'accès à la piste interne (« None. »). Le test fermé de 12 testeurs pendant 14 jours ne vise que l'accès à la production des comptes personnels créés après le 13 novembre 2023 : il ne s'applique pas à notre distribution. « Internal tests might not be subject to standard Play policy or security reviews. » |
| Compte individuel | Apple Account avec double authentification ; « Your name will be displayed as the seller name of your apps on the App Store. » | Pièce d'identité et carte possibles (« Google may require a valid government ID and a credit card, both issued under your legal name ») ; un appareil Android est exigé : « personal account owners must verify access to an Android device using the Play Console mobile app before publishing apps. » |
| Exonération | Réservée aux associations, établissements d'enseignement accrédités et entités publiques ; exige de « Not be an individual, sole proprietor, or single-person business ». Ne nous concerne pas. | Aucune trouvée. |

**Total** : 124 USD la première année (99 + 25), puis 99 USD par an. Hors taxes et taux de change, qui ne sont pas publiés.

Hors périmètre : la vérification des développeurs Android pour la distribution hors Play (Android Developer Console, frais annoncés de 25 USD pour le compte « Full Distribution ») ne nous concerne pas, puisque la distribution passe par Play. Information relevée par le sous-agent, non relue par moi.

Sources :
- https://developer.apple.com/programs/enroll/, https://developer.apple.com/fr/help/account/membership/program-enrollment/, https://developer.apple.com/programs/whats-included/
- https://developer.apple.com/support/compare-memberships/ (redirige vers https://developer.apple.com/help/account/basics/about-your-developer-account), https://developer.apple.com/testflight/, https://developer.apple.com/help/account/membership/fee-waivers/
- https://support.google.com/googleplay/android-developer/answer/6112435, https://support.google.com/googleplay/android-developer/answer/14151465, https://support.google.com/googleplay/android-developer/answer/9845334
- https://developer.android.com/developer-verification

## États d'erreur à prévoir

| Situation | Comportement proposé |
|---|---|
| Pas de réseau en magasin | Scan gardé en « À noter » avec son code-barres et un rappel, prérempli au retour du réseau (écart 2, docs/suivi.md) |
| Scan sans résultat OFF | Écran Erreur, sans la phrase sur la connexion ; « Saisir le produit à la main » garde le code-barres (écart 2, docs/suivi.md) |
| OFF indisponible ou limite atteinte (15 req/min) | Traité comme « pas de réseau » pour ce produit ; nouvel essai plus tard. Les sous-agents ont reçu plusieurs réponses « Page temporarily unavailable » d'OFF le 2026-11-22. |
| Caméra refusée | Écran d'explication et lien vers les réglages ; saisie manuelle du code-barres |
| Échec de synchro ou d'envoi de photo | Données gardées en file locale, nouvel essai automatique ; indicateur discret « non synchronisé » |
| Erreur serveur (Worker ou D1) | Traitée comme un échec de synchro ; nouvel essai avec délai croissant. Plus de blocage quotidien de D1 : le compte est en Workers Paid. L'erreur `1103` du plafond de dépense (§2) reçoit le même traitement. |

## Incertitudes restantes

- Gratuité de Zero Trust jusqu'à 50 utilisateurs : seule une architecture de référence le dit (sans effet sur la recommandation).
- Plafond de dépense Workers : D1 n'est pas nommé parmi les produits exclus ; à confirmer.
- Taille ajoutée au binaire iOS par CMP : non trouvée (sans effet, CMP non retenu).
- Forcer Dynamic Type dans un test XCUITest : pas de méthode officielle trouvée.
- Maestro et caméra en simulateur : vérifié sur l'émulateur Android pour l'aperçu (spikes/maestro/README.md, 2026-11-27) ; prise de photo et iOS non vérifiés.
- Qualification « usage public » de l'ODbL pour une app à deux personnes : question juridique.
