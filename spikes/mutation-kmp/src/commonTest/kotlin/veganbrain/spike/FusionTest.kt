package veganbrain.spike

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FusionTest {
    private fun saisie(v: String, mur: Long, app: String = "A") = ValeurChamp(v, Hlc(mur, 0, app), Origine.Saisie)
    private fun off(v: String, mur: Long) = ValeurChamp(v, Hlc(mur, 0, "A"), Origine.OpenFoodFacts)

    @Test
    fun la_plus_recente_gagne_quel_que_soit_l_ordre() { // SYN-05
        val dixHeures = saisie("Correct", 10, "A")
        val onzeHeures = saisie("Bon", 11, "B")
        assertEquals(onzeHeures, Fusion.champ(dixHeures, onzeHeures))
        assertEquals(onzeHeures, Fusion.champ(onzeHeures, dixHeures))
    }

    @Test
    fun off_cede_devant_une_saisie_meme_plus_ancienne() { // SCN-14
        val tape = saisie("Steak au blé", 1)
        val venuDOff = off("Steak de seitan grillé", 99)
        assertEquals(tape, Fusion.champ(venuDOff, tape))
        assertEquals(tape, Fusion.champ(tape, venuDOff))
    }

    @Test
    fun off_remplit_un_champ_vide() {
        val venuDOff = off("Atelier Blé", 5)
        assertEquals(venuDOff, Fusion.champ(null, venuDOff))
        assertEquals(venuDOff, Fusion.champ(venuDOff, null))
        assertNull(Fusion.champ<String>(null, null))
    }

    @Test
    fun deux_valeurs_off_la_plus_recente_gagne() {
        assertEquals(off("B", 2), Fusion.champ(off("A", 1), off("B", 2)))
    }

    @Test
    fun champs_differents_gardes() { // SYN-06
        val locale = mapOf("note" to saisie("Bon", 1))
        val distante = mapOf("lieu" to saisie("Marché du samedi", 2, "B"))
        assertEquals(locale + distante, Fusion.fiche(locale, distante))
    }

    @Test
    fun version_suspendue() = runTest {
        val locale = mapOf("note" to saisie("Bon", 1))
        assertEquals(locale, Fusion.ficheAsync(locale) { null })
        val distante = mapOf("note" to saisie("Correct", 2, "B"))
        assertEquals(distante, Fusion.ficheAsync(locale) { distante })
    }
}
