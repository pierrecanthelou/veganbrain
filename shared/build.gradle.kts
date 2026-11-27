plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
}

// Adresse de contact du User-Agent OFF (OFF-02, docs/criteres.md:195).
// La vraie valeur vit hors du dépôt, dans ~/.gradle/gradle.properties, rangée par
// scripts/installer-secrets.sh. Sans elle (CI, autre poste), l'adresse fictive ci-dessous.
// Le plugin Android KMP n'a pas de BuildConfig : on génère un fichier Kotlin commun.
val contactOff = providers.gradleProperty("veganbrain.offContact").orElse("contact@exemple.invalid")
val dossierGenere = layout.buildDirectory.dir("generated/veganbrain/commonMain/kotlin")

val genererContactOff = tasks.register("genererContactOff") {
    val adresse = contactOff
    val sortie = dossierGenere
    inputs.property("adresse", adresse)
    outputs.dir(sortie)
    doLast {
        val valeur = adresse.get()
        // Forme simple et sans guillemet ni « $ » : la valeur est recopiée dans du code Kotlin.
        // Le message ne cite pas la valeur, pour qu'elle n'apparaisse dans aucun journal.
        require(valeur.matches(Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]+"""))) {
            "veganbrain.offContact n'a pas la forme d'une adresse e-mail."
        }
        val fichier = sortie.get().file("app/veganbrain/donnees/ContactOff.kt").asFile
        fichier.parentFile.mkdirs()
        fichier.writeText(
            """
            |// Fichier généré par la tâche genererContactOff (shared/build.gradle.kts). Ne pas modifier.
            |package app.veganbrain.donnees
            |
            |internal object ContactOff {
            |    const val ADRESSE: String = "$valeur"
            |}
            |""".trimMargin()
        )
    }
}

kotlin {
    jvmToolchain(17)

    jvm()

    android {
        namespace = "app.veganbrain.shared"
        compileSdk = 37
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            kotlin.srcDir(genererContactOff)
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}
