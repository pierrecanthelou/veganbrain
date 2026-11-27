// Banc jetable du spike Maestro (tâche 10, docs/suivi.md) : autorisations, notification, caméra.
// Projet autonome, hors du build principal. Lancer depuis la racine : ./gradlew -p spikes/maestro/banc installDebug
rootProject.name = "banc-maestro"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
