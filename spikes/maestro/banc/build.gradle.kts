// Même AGP que le build principal (gradle/libs.versions.toml). Java seul, aucune dépendance.
plugins {
    id("com.android.application") version "9.3.1"
}

android {
    namespace = "app.veganbrain.banc"
    compileSdk = 37

    defaultConfig {
        applicationId = "app.veganbrain.banc"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
