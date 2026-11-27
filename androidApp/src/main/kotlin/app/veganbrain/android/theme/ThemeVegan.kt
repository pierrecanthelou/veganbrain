package app.veganbrain.android.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.veganbrain.design.Couleur
import app.veganbrain.design.Jetons
import app.veganbrain.design.Palette
import app.veganbrain.design.Rayon
import app.veganbrain.design.StyleTexte
import app.veganbrain.design.Teinte

// Thème Compose : traduit les jetons générés depuis docs/design/tokens.json (Jetons, module shared).
// Aucune valeur n'est écrite ici ; 1 px des maquettes = 1 dp, et 1 sp pour le texte.

// Pas de palette par défaut : hors de ThemeVegan, un écran (ou un test) échoue au lieu de passer en clair.
private val LocalPalette = staticCompositionLocalOf<Palette> { error("Theme.palette lu hors de ThemeVegan.") }

/** Accès aux jetons du thème en cours : `Theme.palette.fond.compose`. */
object Theme {
    val palette: Palette
        @Composable @ReadOnlyComposable
        get() = LocalPalette.current
}

/** Suit le réglage du système, clair ou sombre (A11-08, docs/criteres.md:211). */
@Composable
fun ThemeVegan(sombre: Boolean = isSystemInDarkTheme(), contenu: @Composable () -> Unit) {
    val palette = if (sombre) Jetons.sombre else Jetons.clair
    val schema = (if (sombre) darkColorScheme() else lightColorScheme()).avec(palette)
    CompositionLocalProvider(LocalPalette provides palette) {
        MaterialTheme(colorScheme = schema, content = contenu)
    }
}

// Rôles Material lus par la barre d'onglets provisoire de la tâche 8 (NavigationBarTokens de
// material3 1.5.0, lu par javap : libellé et icône actifs en secondary et onSecondaryContainer, indicateur en
// secondaryContainer, inactifs en onSurfaceVariant, barre en surfaceContainer), pour qu'aucune couleur
// Material par défaut ne s'y affiche. Indicateur en `menthe` : choix provisoire, la barre des maquettes
// (capsule potiron-clair ou violette-clair) arrivera avec les écrans. `error` est fixé par avance.
private fun ColorScheme.avec(p: Palette): ColorScheme = copy(
    background = p.fond.compose,
    onBackground = p.encre.compose,
    surface = p.surface.compose,
    onSurface = p.encre.compose,
    onSurfaceVariant = p.encreDouce.compose,
    surfaceContainer = p.surface.compose,
    secondary = p.encre.compose,
    secondaryContainer = p.menthe.compose,
    onSecondaryContainer = p.encreSurCouleur.compose,
    error = p.danger.compose,
)

val Couleur.compose: Color get() = Color(red = rouge, green = vert, blue = bleu)

val Teinte.compose: Color get() = couleur.compose.copy(alpha = alpha.toFloat())

/** Rayon.Complet : 50 % du plus petit côté, soit la moitié de la hauteur d'une capsule (radius-pill). */
val Rayon.forme: Shape
    get() = when (this) {
        is Rayon.Px -> RoundedCornerShape(px.dp)
        Rayon.Complet -> RoundedCornerShape(percent = 50)
    }

/** Polices : tokens.json n'en embarque aucune (type.fonts vide), famille système en attendant. */
val StyleTexte.compose: TextStyle
    get() = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = taillePx.sp,
        lineHeight = interlignePx.sp,
        fontWeight = FontWeight(graisse),
    )
