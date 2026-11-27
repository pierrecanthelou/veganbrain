// Déclarer KGP 2.4.21 ici fixe aussi la version qu'utilise le Kotlin intégré d'AGP 9
// (« Runtime dependency on Kotlin Gradle plugin », notes d'AGP 9.0).
plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kmp.library) apply false
}
