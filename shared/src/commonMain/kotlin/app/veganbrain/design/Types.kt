package app.veganbrain.design

import kotlin.jvm.JvmInline

/** Couleur sRGB opaque sur 24 bits, 0xRRGGBB (docs/design/tokens.json, color.tokens). */
@JvmInline
value class Couleur(val rgb: Int) {
    init {
        require(rgb in 0..0xFFFFFF) { "Couleur hors de 0x000000..0xFFFFFF." }
    }

    val rouge: Int get() = rgb shr 16 and 0xFF
    val vert: Int get() = rgb shr 8 and 0xFF
    val bleu: Int get() = rgb and 0xFF
}

/** Couleur posée avec une opacité entre 0 et 1 (color.derived). */
data class Teinte(val couleur: Couleur, val alpha: Double)

/** Rayon d'angle : en px des maquettes, ou arrondi complet (moitié de la hauteur, radius-pill). */
sealed interface Rayon {
    data class Px(val px: Int) : Rayon

    data object Complet : Rayon
}

/** Familles de type.families : display (titres) et sans (texte). */
enum class Famille { Display, Sans }

/** Style de texte de type.groups ; tailles et interlignes en px des maquettes. */
data class StyleTexte(val famille: Famille, val taillePx: Int, val interlignePx: Int, val graisse: Int)
