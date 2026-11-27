# Spike Maestro, volet Android (tâche 10)

But : vérifier, sur ce poste Windows et un émulateur Android, ce que Maestro sait piloter parmi les points à risque des lignes E de `docs/criteres.md` (`docs/cadrage.md:396`). Le volet iOS est à la tâche 14 ; ce qui en est dit ici vient de la documentation, sans essai.

Essais du 2026-11-27. Les chemins personnels sont retirés des sorties collées.

## Environnement

| Élément | Valeur |
|---|---|
| Poste | Windows 11, sans WSL |
| Maestro CLI | 2.12.1, zip de https://github.com/mobile-dev-inc/maestro/releases/tag/cli-2.12.1, extrait dans `%LOCALAPPDATA%\maestro` |
| Empreinte du zip | sha256 `54dbb0716425626604973cbb374c585ca30272375d9ce15177a1152765ddd9d7`, égale à celle de `checksums_sha256.txt` de la même release |
| Java | OpenJDK 17.0.18 (Maestro demande Java 17 ou plus, https://docs.maestro.dev/maestro-cli/how-to-install-maestro-cli) |
| Émulateur | 36.4.9.0, AVD « Medium Phone », image `android-36.1` `google_apis_playstore` x86_64, Android 16, langue anglaise |
| Caméras de l'AVD | `hw.camera.back=virtualscene`, `hw.camera.front=emulated` |

Pourquoi 2.12.1 : c'est la dernière version publiée avant le jour de l'essai. La 2.13.0 est sortie le 2026-11-27 à 09:23 UTC, le matin même ; elle n'a pas été essayée. La CI (tâche 11) épingle la 2.12.1 avec l'empreinte ci-dessus.

### Installation sans WSL

La documentation donne une installation Windows native : télécharger `maestro.zip` depuis les releases GitHub, l'extraire, ajouter `bin` au `PATH` (https://docs.maestro.dev/maestro-cli/how-to-install-maestro-cli, section « Windows »). WSL n'est qu'une autre option. L'installation native marche ici ; le `PATH` n'a pas été modifié, `maestro.bat` est appelé par son chemin.

Variables posées à chaque lancement (https://docs.maestro.dev/maestro-cli/environment-variables) :

```sh
MAESTRO_CLI_NO_ANALYTICS=true                  # pas de statistiques d'usage
MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED=true
MAESTRO_DISABLE_UPDATE_CHECK=true
MAESTRO_OPTS="-Dstdout.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8"
```

Sous la console Windows, les accents de la sortie s'affichent en « � » tant que la page de code n'est pas UTF-8. Lancer `chcp 65001` avant `maestro.bat`, avec `MAESTRO_OPTS` ci-dessus, les rétablit (`maestro.bat` passe `MAESTRO_OPTS` à la JVM). Les sélecteurs accentués marchent dans les deux cas : seul l'affichage est en cause.

## Ce qui a été construit

- `androidApp/.../MainActivity.kt` : `Modifier.semantics { testTagsAsResourceId = true }` sur la racine (point 1).
- `flows/onglets.yaml` : flux technique du squelette (LIS-08), celui que la CI reprendra.
- `flows/mode-avion-on.yaml`, `flows/mode-avion-off.yaml`, `flows/paysage.yaml`.
- `banc/` : app Android jetable (`app.veganbrain.banc`, Java, sans dépendance), qui demande l'autorisation de notifier, poste une notification qui rouvre l'app, demande la caméra et affiche son aperçu. Chaque résultat s'affiche en texte, pour que Maestro le lise. Installation : `./gradlew -p spikes/maestro/banc installDebug`.
- `flows/banc/*.yaml` : un flux par point, sur le banc.

Lancement d'un flux : `maestro test spikes/maestro/flows/onglets.yaml`.

## Résultats

| # | Point | Résultat |
|---|---|---|
| 1 | Lecture du `testTag` | **Oui**, par `id:`, à condition de poser `testTagsAsResourceId = true` |
| 2 | Mode avion | **Oui** sur Android (`setAirplaneMode`), coupure réelle vérifiée. Pas d'équivalent sur simulateur iOS (doc) |
| 3 | Boîtes d'autorisation | **Oui**, boîtes visibles et pilotables par identifiant. **Mais** la clé `all` et le défaut de `launchApp` sont sans effet : chaque autorisation doit être nommée |
| 4 | Caméra accordée d'avance | **Oui** : `launchApp` avec `permissions: { camera: allow }` (ou `adb shell pm grant`) |
| 5 | Caméra en émulateur | **Oui** pour l'écran caméra : aperçu de la scène virtuelle, images reçues. Prise de photo et lecture d'un code-barres non essayées |
| 6 | Notification touchée | **Oui** : volet ouvert par glissement, notification touchée, app rouverte avec son extra |
| 7 | Paysage | **Oui** : `setOrientation`, app pilotable en paysage (téléphone ; pas d'AVD tablette) |

### 1. Lecture du `testTag`

La doc des sélecteurs dit, pour Compose : « Use `Modifier.semantics { testTagsAsResourceId = true }` to ensure your test tags are discoverable as IDs » (https://docs.maestro.dev/reference/selectors/core-selectors). Le sélecteur `id:` lit alors le `testTag`.

Sans la propriété, `flows/onglets.yaml` échoue, et la hiérarchie capturée par `maestro hierarchy` ne contient aucun `onglets.produits` (0 occurrence) :

```
> Flow onglets
Launch app "app.veganbrain" with clear state... COMPLETED
Assert that id: onglets.produits, selected is visible... FAILED
Assertion is false: id: onglets.produits, selected is visible
```

Avec la propriété posée dans `MainActivity.kt` :

```
> Flow onglets
Launch app "app.veganbrain" with clear state... COMPLETED
Assert that id: onglets.produits, selected is visible... COMPLETED
Tap on id: onglets.recettes... COMPLETED
Assert that id: onglets.recettes, selected is visible... COMPLETED
Assert that id: onglets.produits, not selected is visible... COMPLETED
```

Le sélecteur d'état `selected` lit l'onglet courant. Sur iOS, `id` vise l'`accessibilityIdentifier` (même page de doc) : à vérifier à la tâche 14.

### 2. Mode avion

`setAirplaneMode: enabled|disabled` (https://docs.maestro.dev/reference/commands-available/setairplanemode). La coupure se vérifie par adb, hors Maestro :

```sh
maestro test spikes/maestro/flows/mode-avion-on.yaml
adb shell settings get global airplane_mode_on
adb shell dumpsys connectivity | grep -m1 "Active default network"
adb shell "nc -z -w 5 world.openfoodfacts.org 443 && echo joignable || echo injoignable"
```

```
Launch app "app.veganbrain"... COMPLETED
Enable airplane mode... COMPLETED
Assert that id: onglets.produits is visible... COMPLETED
-- sonde
airplane_mode_on=1
Active default network: none
nc: world.openfoodfacts.org:443: No address associated with hostname
injoignable
```

Puis `flows/mode-avion-off.yaml` :

```
Disable airplane mode... COMPLETED
-- sonde
airplane_mode_on=0
Active default network: 107
joignable
```

Le `ping` ne sert pas de sonde : l'ICMP échoue sur l'émulateur même avec le réseau.

iOS : « This command is supported on Android only. iOS simulators do not have an airplane mode. On web or iOS, the command will pass but has no effect. » (même page). Un flux iOS hors réseau ne peut donc pas s'appuyer sur cette commande ; il passera la commande sans erreur et sans couper le réseau.

### 3. Boîtes d'autorisation

#### La clé `all` et le défaut ne font rien

La doc dit : « By default, Maestro grants all permissions » (https://docs.maestro.dev/maestro-flows/flow-control-and-logic/permissions) et, pour `launchApp`, « By default, all permissions are allowed » (https://docs.maestro.dev/reference/commands-available/launchapp). Elle ajoute pour Android : « the permission is granted silently without a prompt ».

Observé ici, autre chose. L'état réel est lu après chaque flux par :

```sh
adb shell dumpsys package app.veganbrain.banc | grep -E "(POST_NOTIFICATIONS|CAMERA): granted"
```

| Flux (après `pm clear`) | POST_NOTIFICATIONS | CAMERA |
|---|---|---|
| `launchApp` avec `clearState: true`, sans `permissions` | false | false |
| `launchApp` sans `clearState` ni `permissions` | false | false |
| `launchApp` avec `clearState: true` et `permissions: { all: allow }` | false | false |
| `clearState` puis `launchApp` | false | false |
| `launchApp`, puis `setPermissions: { permissions: { notifications: allow, camera: allow } }` | **true** | **true** |
| `launchApp` avec `clearState: true` et `permissions: { camera: allow, notifications: allow }` | **true** | **true** |

Et dans l'autre sens, en partant des deux autorisations accordées :

| Flux | POST_NOTIFICATIONS | CAMERA |
|---|---|---|
| `launchApp` avec `permissions: { all: deny }` | true | true |
| `setPermissions` avec `all: deny` | true | true |
| `setPermissions` avec `all: unset` | true | true |
| `setPermissions` avec `camera: deny`, `notifications: deny` | **false** | **false** |

Sortie type, ici le dernier cas :

```
Launch app "app.veganbrain.banc"... COMPLETED
Set permissions... COMPLETED
Assert that "(?i)demander notifications" is visible... COMPLETED
        android.permission.POST_NOTIFICATIONS: granted=false, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]
        android.permission.CAMERA: granted=false, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]
```

Le journal `maestro.log` (option `--debug-output`) ne trace pas l'accord des autorisations, qu'elles soient nommées ou non : il note seulement le paramètre lu, ici `permissions={all=allow}` dans la `LaunchAppCommand`. Il ne dit donc pas où `all` se perd. Aucune issue trouvée sur ce point : recherche `gh search issues --repo mobile-dev-inc/maestro` avec « permissions all », « android permissions not granted launchApp », « android 16 permissions », « setPermissions all android », et une recherche web ; la seule issue proche (#3417) concerne un iPhone réel.

**Règle pour les flux de l'app** : nommer chaque autorisation dans `launchApp` (`notifications`, `camera`, …), avec `allow`, `deny` ou `unset`, et ne jamais compter sur le défaut ni sur `all`.

#### Boîte de notification (RAP-04, RAP-12)

`flows/banc/notif-demande.yaml` : autorisation `unset` au lancement, première demande → boîte système, « Allow » → accordée ; seconde demande → pas de boîte.

```
Launch app "app.veganbrain.banc" with clear state... COMPLETED
Tap on "Demander notifications"... COMPLETED
Assert that id: com.android.permissioncontroller:id/permission_allow_button is visible... COMPLETED
Take screenshot boite-notifications... COMPLETED
Tap on id: com.android.permissioncontroller:id/permission_allow_button... COMPLETED
Assert that "Notifications accordées" is visible... COMPLETED
Tap on "Demander notifications"... COMPLETED
Assert that id: com.android.permissioncontroller:id/permission_allow_button is not visible... COMPLETED
Assert that "Notifications accordées" is visible... COMPLETED
```

`flows/banc/notif-refus.yaml` : « Don't allow » dans la boîte, puis tentative de notification.

```
Launch app "app.veganbrain.banc" with clear state... COMPLETED
Tap on "Demander notifications"... COMPLETED
Tap on id: com.android.permissioncontroller:id/permission_deny_button... COMPLETED
Assert that "Notifications refusées" is visible... COMPLETED
Tap on "Poster notification"... COMPLETED
Assert that "Notifications non accordées" is visible... COMPLETED
```

#### Boîte caméra (SCN-12)

Boutons relevés par `adb shell uiautomator dump` pendant la boîte :

```
text="Allow Banc Maestro to take pictures and record video?" resource-id="com.android.permissioncontroller:id/permission_message"
text="While using the app" resource-id="com.android.permissioncontroller:id/permission_allow_foreground_only_button"
text="Only this time" resource-id="com.android.permissioncontroller:id/permission_allow_one_time_button"
text="Don’t allow" resource-id="com.android.permissioncontroller:id/permission_deny_button"
```

`flows/banc/camera-refus.yaml` :

```
Launch app "app.veganbrain.banc" with clear state... COMPLETED
Tap on "Ouvrir caméra"... COMPLETED
Tap on id: com.android.permissioncontroller:id/permission_deny_button... COMPLETED
Assert that "Caméra refusée" is visible... COMPLETED
```

Les flux visent les boutons par identifiant, pas par texte : le texte dépend de la langue de l'appareil. Ces identifiants viennent du `permissioncontroller` de cette image système (`google_apis_playstore`, Android 16) ; l'image de la CI doit être du même type, sinon les flux sont à revérifier.

iOS, d'après la doc et sans essai : la boîte de notification n'est pas accordée en silence ; Maestro touche « Allow » de lui-même (page permissions, « Push notifications on iOS »). RAP-04 (« la boîte s'ouvre ») ne s'y observerait donc pas tel quel, et RAP-12 passerait par `notifications: deny` au lancement. À vérifier à la tâche 14.

### 4. Caméra accordée d'avance

Deux moyens vérifiés : `launchApp` avec `permissions: { camera: allow }` (tableau du point 3), et `adb shell pm grant app.veganbrain.banc android.permission.CAMERA` (`Success`, puis `CAMERA: granted=true`). Le premier reste dans le flux, sans appel adb : c'est lui que reprennent les flux qui ouvrent le scan. La doc le donne aussi pour iOS ; non essayé (tâche 14).

### 5. Caméra en émulateur

`flows/banc/camera-accordee.yaml` : caméra accordée d'avance, pas de boîte, aperçu Camera2 de la caméra arrière.

```
Launch app "app.veganbrain.banc" with clear state... COMPLETED
Tap on "Ouvrir caméra"... COMPLETED
Assert that id: com.android.permissioncontroller:id/permission_deny_button is not visible... COMPLETED
Assert that "Aperçu caméra actif" is visible... COMPLETED
Assert that "Images reçues : [0-9]+" is visible... COMPLETED
Take screenshot camera-virtualscene... COMPLETED
```

La capture montre la scène virtuelle de l'émulateur (une pièce, un écran en damier) et « Images reçues : 60 ».

Non essayé :
- la **prise** d'une photo (FPR-09 : « Changer la photo » puis photo prise) : seul l'aperçu l'a été ;
- la lecture d'un code-barres par la caméra : SCN-02 reste manuel (`docs/criteres.md:32`), les flux de scan passent par la saisie (SCN-16) ;
- la voie « Choisir » une photo : la doc a une commande `addMedia` et une recette « choose images from the gallery », non lues.

### 6. Notification touchée

`flows/banc/notif-touchee.yaml` : la notification est postée par le banc (canal à importance haute, `PendingIntent` qui rouvre l'activité avec un extra), l'app passe en arrière-plan, le volet est ouvert par un glissement depuis le haut.

```
Launch app "app.veganbrain.banc" with clear state... COMPLETED
Tap on "Poster notification"... COMPLETED
Assert that "Notification postée" is visible... COMPLETED
Press Home key... COMPLETED
Swipe from (50%, 1%) to (50%, 70%) in 400 ms... COMPLETED
Tap on "Rappel du banc"... COMPLETED
Assert that "Ouvert depuis la notification" is visible... COMPLETED
```

La notification de RAP-07 sera programmée (rappel) ; le spike la poste tout de suite. Le déclenchement à l'heure se teste ailleurs (U sur la planification).

### 7. Paysage

`flows/paysage.yaml` sur l'app :

```
Launch app "app.veganbrain" with clear state... COMPLETED
Set orientation LANDSCAPE_LEFT... COMPLETED
Tap on id: onglets.recettes... COMPLETED
Assert that id: onglets.recettes, selected is visible... COMPLETED
Take screenshot paysage... COMPLETED
Set orientation PORTRAIT... COMPLETED
```

A11-09 parle de tablette ; l'essai est sur un téléphone, faute d'AVD tablette. La commande est la même.

## Stabilité

`onglets` et les cinq flux du banc ont été rejoués trois fois de suite, code de sortie de `maestro test` noté à chaque passage. Les flux du mode avion et du paysage n'ont tourné qu'une à deux fois, le mode avion avec la sonde adb à chaque fois.

```
onglets essai 1 : code 0
onglets essai 2 : code 0
onglets essai 3 : code 0
banc/notif-demande essai 1 : code 0
banc/notif-demande essai 2 : code 0
banc/notif-demande essai 3 : code 0
banc/notif-refus essai 1 : code 0
banc/notif-refus essai 2 : code 0
banc/notif-refus essai 3 : code 0
banc/notif-touchee essai 1 : code 0
banc/notif-touchee essai 2 : code 0
banc/notif-touchee essai 3 : code 0
banc/camera-accordee essai 1 : code 0
banc/camera-accordee essai 2 : code 0
banc/camera-accordee essai 3 : code 0
banc/camera-refus essai 1 : code 0
banc/camera-refus essai 2 : code 0
banc/camera-refus essai 3 : code 0
```

Avant ces répétitions, un échec isolé a été vu une fois, au premier essai d'un flux du banc avec `clearState: true` : le logcat montre « Destroy timeout of remove-task » puis « Killing …:app.veganbrain.banc … remove task » 12 ms plus tard, juste après le relancement. Android a tué le processus neuf en retirant l'ancienne tâche, et l'app précédente est revenue au premier plan. Le flux a échoué au premier `tapOn`. C'est une course entre la remise à zéro et le relancement, côté Android.

## Ce qui reste pour la tâche 14 (iOS)

- `id:` sur `accessibilityIdentifier` ;
- boîtes d'autorisation sur simulateur, et la boîte de notification acceptée d'office ;
- écran caméra en simulateur, le risque nommé en `docs/cadrage.md:428` (« Maestro ne peut pas piloter l'écran caméra en simulateur (non vérifié) ») : levé ici pour l'émulateur Android seulement ;
- pas de mode avion en simulateur (doc ci-dessus).
