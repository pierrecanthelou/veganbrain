import java.math.BigDecimal

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

// Jetons du design system (tâche 9) : docs/design/tokens.json → Jetons.kt, constantes neutres
// (sans Compose) en commonMain, lues par le thème Compose d'androidApp et par le test A11-01.
// Le fichier généré ne contient que des valeurs du json : l'UI n'écrit aucune valeur en dur.
val fichierJetons = rootProject.layout.projectDirectory.file("docs/design/tokens.json")

val genererJetons = tasks.register("genererJetons") {
    val entree = fichierJetons
    val sortie = layout.buildDirectory.dir("generated/veganbrain/jetons/kotlin")
    inputs.file(entree)
    outputs.dir(sortie)
    doLast {
        @Suppress("UNCHECKED_CAST")
        val json = groovy.json.JsonSlurper().parse(entree.asFile, "UTF-8") as Map<String, Any?>
        val fichier = sortie.get().file("app/veganbrain/design/Jetons.kt").asFile
        fichier.parentFile.mkdirs()
        fichier.writeText(GenerateurJetons(json).kotlin())
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
            kotlin.srcDir(genererJetons)
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

/**
 * Traduit docs/design/tokens.json en Kotlin (Jetons.kt). Toute forme inattendue arrête le build,
 * avec le nom du jeton en cause : on ne devine aucune valeur.
 * Unités : 1 px des maquettes = 1 dp (1 sp pour le texte) ; la conversion se fait côté Compose.
 */
class GenerateurJetons(private val json: Map<String, Any?>) {
    private val themes = listOf("clair", "sombre")
    private val formeNom = Regex("[a-z][a-z0-9]*(-[a-z0-9]+)*")
    private val formeHex = Regex("#[0-9A-Fa-f]{6}")
    private val formePx = Regex("([0-9]+)px")

    @Suppress("UNCHECKED_CAST")
    private fun Any?.objet(ou: String): Map<String, Any?> =
        this as? Map<String, Any?> ?: error("tokens.json : objet attendu pour $ou.")

    private fun Any?.liste(ou: String): List<Map<String, Any?>> =
        (this as? List<*> ?: error("tokens.json : liste attendue pour $ou.")).map { it.objet(ou) }

    private fun Map<String, Any?>.texte(cle: String, ou: String): String =
        this[cle] as? String ?: error("tokens.json : texte attendu pour $ou.$cle.")

    // Mots réservés de Kotlin (https://kotlinlang.org/docs/keyword-reference.html, « Hard keywords »).
    private val reserves = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in", "interface",
        "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try", "typealias",
        "typeof", "val", "var", "when", "while",
    )
    private val declares = mutableMapOf<String, MutableSet<String>>()

    private fun nom(brut: String, ou: String): String {
        require(brut.matches(formeNom)) { "tokens.json : nom de jeton inattendu « $brut » ($ou)." }
        require(brut !in reserves) { "tokens.json : « $brut » est un mot réservé de Kotlin ($ou)." }
        return brut.split('-')
            .mapIndexed { i, p -> if (i == 0) p else p.replaceFirstChar(Char::uppercase) }
            .joinToString("")
    }

    /** Nom d'une déclaration : deux jetons d'un même groupe ne peuvent pas donner le même nom Kotlin. */
    private fun declaration(brut: String, ou: String, groupe: String): String {
        val n = nom(brut, ou)
        require(declares.getOrPut(groupe) { mutableSetOf() }.add(n)) {
            "tokens.json : « $brut » donne le nom Kotlin « $n », déjà pris dans $groupe."
        }
        return n
    }

    private fun hex(brut: String?, ou: String): String {
        require(brut != null && brut.matches(formeHex)) { "tokens.json : couleur #RRGGBB attendue pour $ou." }
        return "Couleur(0x" + brut.substring(1).uppercase() + ")"
    }

    private fun px(brut: String, ou: String): Int =
        formePx.matchEntire(brut)?.groupValues?.get(1)?.toInt()
            ?: error("tokens.json : valeur en px attendue pour $ou.")

    /** Graisse de 1 à 1000, plage que Compose (FontWeight) accepte : sinon l'échec viendrait à l'exécution. */
    private fun graisse(brut: Any?, style: String): Int =
        entier(brut, "$style.fontWeight").also {
            require(it in 1..1000) { "tokens.json : graisse hors de 1 à 1000 pour $style.fontWeight." }
        }

    private fun entier(brut: Any?, ou: String): Int =
        (brut as? Number)?.takeIf { it.toDouble() == it.toInt().toDouble() }?.toInt()
            ?: error("tokens.json : entier attendu pour $ou.")

    private fun alpha(brut: Any?, ou: String): String {
        val valeur = (brut as? Number)?.let { BigDecimal(it.toString()) }
            ?: error("tokens.json : opacité attendue pour $ou.")
        require(valeur >= BigDecimal.ZERO && valeur <= BigDecimal.ONE) {
            "tokens.json : opacité hors de [0, 1] pour $ou."
        }
        val texte = valeur.toPlainString()
        return if ('.' in texte) texte else "$texte.0"
    }

    private fun doc(usage: Any?, retrait: String): String {
        val texte = (usage as? String)?.trim().orEmpty()
        // Les commentaires Kotlin s'imbriquent : « /* » ouvrirait un commentaire, « */ » fermerait le KDoc.
        require("*/" !in texte && "/*" !in texte) { "tokens.json : « */ » et « /* » interdits dans un usage." }
        return if (texte.isEmpty()) "" else "$retrait/** $texte */\n"
    }

    fun kotlin(): String {
        val version = entier(json["version"], "version")
        val couleurs = json["color"].objet("color")
        val ids = couleurs["themes"].liste("color.themes").map { it.texte("id", "color.themes") }
        require(ids == themes) { "tokens.json : thèmes attendus $themes, trouvés $ids." }

        val bases = couleurs["tokens"].liste("color.tokens")
        val derives = couleurs["derived"].liste("color.derived")
        val nomsBases = bases.map { it.texte("name", "color.tokens") }
        val nomsDerives = derives.map { it.texte("name", "color.derived") }
        val tous = nomsBases + nomsDerives
        require(tous.size == tous.toSet().size) { "tokens.json : nom de couleur en double." }

        fun valeurBase(nomBrut: String, theme: String): String {
            val jeton = bases.first { it["name"] == nomBrut }
            return hex(jeton["value"].objet("$nomBrut.value")[theme] as? String, "$nomBrut.$theme")
        }

        val sb = StringBuilder()
        sb.append("// Fichier généré par la tâche genererJetons (shared/build.gradle.kts)\n")
        sb.append("// depuis docs/design/tokens.json, version $version. Ne pas modifier.\n")
        sb.append("package app.veganbrain.design\n\n")

        sb.append("/** Couleurs d'un thème : color.tokens (opaques) puis color.derived (avec opacité). */\n")
        sb.append("class Palette(\n")
        bases.forEach { sb.append(doc(it["usage"], "    ")).append("    val ${declaration(it.texte("name", "color.tokens"), "color.tokens", "Palette")}: Couleur,\n") }
        derives.forEach { sb.append(doc(it["usage"], "    ")).append("    val ${declaration(it.texte("name", "color.derived"), "color.derived", "Palette")}: Teinte,\n") }
        sb.append(")\n\n")

        sb.append("object Jetons {\n")
        sb.append("    const val VERSION: Int = $version\n")
        for (theme in themes) {
            sb.append("\n    val $theme: Palette = Palette(\n")
            nomsBases.forEach { n -> sb.append("        ${nom(n, "color.tokens")} = ${valeurBase(n, theme)},\n") }
            derives.forEach {
                val n = it.texte("name", "color.derived")
                val source = it.texte("color", "color.derived.$n")
                val couleur = if (source.startsWith("#")) {
                    hex(source, "$n.color")
                } else {
                    require(source in nomsBases) { "tokens.json : $n renvoie à une couleur inconnue." }
                    valeurBase(source, theme)
                }
                val a = alpha(it["alpha"].objet("color.derived.$n.alpha")[theme], "$n.alpha.$theme")
                sb.append("        ${nom(n, "color.derived")} = Teinte($couleur, $a),\n")
            }
            sb.append("    )\n")
        }

        sb.append("\n    /** spacing.tokens, en px des maquettes. */\n    object Espacements {\n")
        json["spacing"].objet("spacing")["tokens"].liste("spacing.tokens").forEach {
            val n = it.texte("name", "spacing.tokens")
            sb.append(doc(it["usage"], "        "))
                .append("        const val ${declaration(n, "spacing.tokens", "Espacements")}: Int = ${px(it.texte("value", n), n)}\n")
        }
        sb.append("    }\n")

        sb.append("\n    /** radius.tokens, en px des maquettes ; « full » devient Rayon.Complet. */\n    object Rayons {\n")
        json["radius"].objet("radius")["tokens"].liste("radius.tokens").forEach {
            val n = it.texte("name", "radius.tokens")
            val v = it.texte("value", n)
            val rayon = if (v == "full") "Rayon.Complet" else "Rayon.Px(${px(v, n)})"
            sb.append(doc(it["usage"], "        ")).append("        val ${declaration(n, "radius.tokens", "Rayons")}: Rayon = $rayon\n")
        }
        sb.append("    }\n")

        val typo = json["type"].objet("type")
        val familles = typo["families"].objet("type.families").keys
        require(familles == setOf("display", "sans")) { "tokens.json : familles attendues display et sans." }
        sb.append("\n    /** type.groups, tailles et interlignes en px des maquettes. */\n    object Textes {\n")
        typo["groups"].liste("type.groups").forEach { groupe ->
            val famille = groupe.texte("family", "type.groups")
            require(famille in familles) { "tokens.json : famille inconnue « $famille »." }
            groupe["styles"].liste("type.groups.styles").forEach {
                val n = it.texte("name", "type.styles")
                sb.append("        val ${declaration(n, "type.styles", "Textes")}: StyleTexte = StyleTexte(")
                    .append("Famille.${famille.replaceFirstChar(Char::uppercase)}, ")
                    .append("taillePx = ${px(it.texte("fontSize", n), "$n.fontSize")}, ")
                    .append("interlignePx = ${px(it.texte("lineHeight", n), "$n.lineHeight")}, ")
                    .append("graisse = ${graisse(it["fontWeight"], n)})\n")
            }
        }
        sb.append("    }\n}\n")
        return sb.toString()
    }
}
