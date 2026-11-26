// Versions : Kotlin 2.4.21 et Gradle 9.7.0 (maximum « fully supported »,
// https://kotlinlang.org/docs/gradle-configure-project.html), Pitest 1.30.2 (Maven Central).
plugins {
    kotlin("multiplatform") version "2.4.21"
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}

// Le plugin Gradle de Pitest ne prend pas en charge KMP (szpak/gradle-pitest-plugin#234, ouvert) :
// tâche maison qui lance la ligne de commande (https://pitest.org/quickstart/commandline/).
// L'outil et ses extensions vont sur le classpath de lancement (« placed on the classpath of the pitest
// tool », README de pitest-junit5-plugin) ; le code et ses tests passent par --classPath avec
// --includeLaunchClasspath=false (https://pitest.org/quickstart/commandline/).
val pitestOutil: Configuration = configurations.create("pitestOutil")

dependencies {
    pitestOutil("org.pitest:pitest-command-line:1.30.2")
    pitestOutil("org.pitest:pitest-junit5-plugin:1.2.3")
    // Étape B : ./gradlew pitest -Parcmutate. La licence (arcmutate-licence.txt, à la racine du spike)
    // ne va jamais dans le dépôt (.gitignore) ; https://docs.arcmutate.com/docs/kotlin.html.
    if (providers.gradleProperty("arcmutate").isPresent) {
        pitestOutil("com.arcmutate:pitest-kotlin-plugin:1.5.2")
    }
}

val jvmTest = tasks.named<Test>("jvmTest")

tasks.register<JavaExec>("pitest") {
    group = "verification"
    description = "Mutation (Pitest) sur la cible JVM."
    dependsOn("jvmTestClasses")
    mainClass.set("org.pitest.mutationtest.commandline.MutationCoverageReport")
    classpath = pitestOutil
    val classesProjet = jvmTest.map { it.classpath }
    val rapport = layout.buildDirectory.dir("reports/pitest")
    val sources = files("src/commonMain/kotlin", "src/jvmMain/kotlin")
    val sortiesMain = kotlin.jvm().compilations.getByName("main").output.classesDirs
    doFirst { delete(rapport) } // pas de rapport périmé après un échec
    argumentProviders.add(CommandLineArgumentProvider {
        listOf(
            "--reportDir", rapport.get().asFile.absolutePath,
            "--targetClasses", "veganbrain.spike.*",
            "--targetTests", "veganbrain.spike.*",
            "--mutableCodePaths", sortiesMain.joinToString(",") { it.absolutePath },
            "--sourceDirs", sources.joinToString(",") { it.absolutePath },
            "--classPath", classesProjet.get().files.joinToString(",") { it.absolutePath },
            "--includeLaunchClasspath", "false",
            "--outputFormats", "HTML,XML",
            "--timestampedReports", "false",
        )
    })
}
