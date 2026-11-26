# Spike : mutation sur la cible JVM d'un module KMP

Tâche 4 de `docs/suivi.md`. Question : Pitest, puis Arcmutate, se branchent-ils sur la cible `jvm()` d'un module Kotlin Multiplatform dont les tests sont dans `commonTest` ?

**Conclusion (2026-11-26) : Pitest seul est retenu.** Il tourne sur la cible `jvm()` par une tâche Gradle maison. Arcmutate est écarté (décision de l'utilisateur du 2026-11-26 : pas de licence gratuite).

## Contenu

- `Hlc.kt` : horloge logique hybride (SYN-07), avec un `expect fun horlogeSystemeMillis()` et son `actual` JVM.
- `Fusion.kt` : fusion LWW par champ (SYN-05, SYN-06) avec une version simplifiée de l'exception OFF (SCN-14), une `sealed interface`, une `data class` et une fonction `suspend`. Simplification : « champ vide » veut dire clé absente, et deux valeurs OFF se départagent par l'HLC ; SCN-14 dit qu'une valeur OFF ne s'écrit que dans un champ vide.
- Tests `kotlin.test` dans `commonTest`, lancés sur JVM avec JUnit Platform (`kotlin-test-junit5` 2.4.21 tire JUnit Jupiter 5.10.1).
- Le plugin Gradle de Pitest ne prend pas KMP en charge (szpak/gradle-pitest-plugin#234, ouvert) : la tâche `pitest` de `build.gradle.kts` lance la ligne de commande de Pitest (`MutationCoverageReport`).

Versions : Kotlin 2.4.21, Gradle 9.7.0 (maximum « fully supported » par KGP 2.4.21), Pitest 1.30.2, pitest-junit5-plugin 1.2.3, coroutines 1.11.0, JDK 17.

## Lancer

```
./gradlew jvmTest          # tests
./gradlew pitest           # Pitest seul
./gradlew pitest -PeviterAppels=kotlin.jvm.internal.Intrinsics,kotlin.ResultKt   # avec le filtre mesuré plus bas
```

Rapport : `build/reports/pitest/index.html` et `mutations.xml`. La tâche vide le rapport avant chaque passage.

## Résultats du 2026-11-26 (Windows 11, JDK 17.0.18)

**Étape A, Pitest seul : réussie.** 38 mutants, 32 tués (84 %, dont 1 par dépassement de délai), couverture de lignes 37/37.

Premier passage : `--targetClasses veganbrain.spike.*` mutait aussi les classes de test (81 mutants). Corrigé par `--mutableCodePaths` limité aux classes `main`, le moyen documenté (« to avoid mutating test helper classes », https://pitest.org/quickstart/commandline/). Le deuxième passage a fait survivre une vraie lacune, `Hlc.suivante` à la milliseconde égale ; le test `meme_milliseconde_incremente_le_compteur` l'a comblée.

Les 6 mutants non tués du dernier passage :

| Mutant | Statut | Nature |
|---|---|---|
| `Fusion.deux:27`, `>=` devenu `>` | survit | Équivalent si une HLC désigne un seul événement : hypothèse non vérifiée, le départage à égalité est un point ouvert (`docs/criteres.md:266`). |
| `Fusion.fiche:36`, appel `Intrinsics::checkNotNull` retiré | survit | Code généré par `!!`. |
| `Fusion.ficheAsync:39`, appel `ResultKt::throwOnFailure` retiré (2 mutants) | survit, non couvert | Machine à états de la coroutine. |
| `Fusion.ficheAsync:39`, retour remplacé par `null` | non couvert | Machine à états de la coroutine. |
| `ValeurChamp.getValeur:9`, retour `null` | non couvert | Accesseur généré, jamais appelé par les tests. |

À la ligne 43 de `Fusion.ficheAsync`, deux conditions sont inversées. Le test `== COROUTINE_SUSPENDED` de la machine à états (`if_acmpne` dans `javap -c`) finit en dépassement de délai, que Pitest compte comme tué : c'est un sixième mutant de code généré. Le `?:` de la même ligne est tué normalement.

Cinq des six mutants non tués, et le mutant en dépassement de délai, viennent de code généré par le compilateur. D'autres mutants tués portent aussi sur du code généré : accesseurs de `Hlc` (ligne 4) et références de propriété de `compareValuesBy` (`Hlc$compareTo$1` à `$3`, ligne 7). Ce sont les catégories que la documentation du plugin Kotlin d'Arcmutate dit filtrer : « coroutines », « intrinsics », accesseurs générés (https://docs.arcmutate.com/docs/kotlin.html). Non vérifié : Arcmutate n'a pas tourné.

Pitest l'écrit lui-même à la fin : « Project uses kotlin, but the Arcmutate kotlin plugin is not present. »

**Filtre de Pitest seul, à la place d'Arcmutate.** `--avoidCallsTo kotlin.jvm.internal.Intrinsics,kotlin.ResultKt` : 33 mutants au lieu de 38, 31 tués (94 %, dont 1 par dépassement de délai). Les cinq mutants écartés sont ceux des lignes 36 et 39 de `Fusion.kt` : quatre de code généré, et un vrai mutant tué (« replaced return value with Collections.emptyMap for veganbrain/spike/Fusion::fiche », ligne 36), perdu parce que le filtre agit sur la ligne entière. Il laisse le dépassement de délai de la coroutine (ligne 43) et l'accesseur. Le filtre n'est donc pas activé par défaut. Passer une liste remplace aussi celle de Pitest par défaut (« If a list is not explicitly supplied then PIT will default to a list of common logging packages », https://pitest.org/quickstart/commandline/) : sans effet ici, le spike ne journalise rien.

**Étape B, avec Arcmutate : abandonnée.** Sans licence, le plugin `com.arcmutate:pitest-kotlin-plugin:1.5.2` se chargeait, puis Pitest s'arrêtait : `java.lang.IllegalStateException: No licence found. See https://www.arcmutate.com for details.` L'option a été retirée du spike.

## Contrainte pour le vrai module partagé

La mutation passe par une cible `jvm()` : chaque `expect` de `commonMain` devra avoir un `actual` pour la JVM, même si l'application ne livre que sur Android et iOS. Sans `Horloge.jvm.kt`, `./gradlew compileKotlinJvm` échoue : « The 'expect' declaration 'horlogeSystemeMillis' has no 'actual' declaration in module '<commonMain> for JVM'. » Non vérifié : un `actual` placé dans un source set intermédiaire, partagé avec Android, suffirait peut-être.
