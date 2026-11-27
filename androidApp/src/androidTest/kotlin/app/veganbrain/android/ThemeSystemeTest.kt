package app.veganbrain.android

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.DarkMode
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A11-08 (docs/criteres.md:211) : le thème suit le réglage du système ; en sombre, le fond prend la
 * valeur `sombre` de `fond`.
 *
 * Le réglage passe par DeviceConfigurationOverride.DarkMode, qui fournit une Configuration en mode nuit
 * à LocalConfiguration, que lit isSystemInDarkTheme() : le même canal que le réglage du système.
 * Valeurs attendues recopiées de docs/design/tokens.json:19-20 (et non lues dans Jetons, pour ne pas
 * comparer le thème à lui-même). Le pixel lu est au centre de l'écran, dans la zone de contenu vide ;
 * il changera quand le dégradé fond-haut → fond-bas remplacera ce repli (README, Couleurs).
 */
@RunWith(AndroidJUnit4::class)
class ThemeSystemeTest {

    @get:Rule
    val regle = createComposeRule()

    @Test
    fun systemeEnSombreDonneLeFondSombre() = verifierFond(sombre = true, attendu = 0xFF131F18.toInt())

    @Test
    fun systemeEnClairDonneLeFondClair() = verifierFond(sombre = false, attendu = 0xFFEAF4EA.toInt())

    private fun verifierFond(sombre: Boolean, attendu: Int) {
        regle.setContent {
            DeviceConfigurationOverride(DeviceConfigurationOverride.DarkMode(sombre)) {
                AppVegan()
            }
        }
        val image = regle.onRoot().captureToImage().asAndroidBitmap()
        val pixel = image.getPixel(image.width / 2, image.height / 2)
        assertEquals(
            "pixel central en ${if (sombre) "sombre" else "clair"} : #%08X".format(pixel),
            attendu,
            pixel,
        )
    }
}
