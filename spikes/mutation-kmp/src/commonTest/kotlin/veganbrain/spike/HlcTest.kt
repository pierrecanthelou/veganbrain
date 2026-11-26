package veganbrain.spike

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HlcTest {
    private val a = Hlc(1_000, 0, "A")

    @Test
    fun avance_avec_l_horloge() {
        assertEquals(Hlc(2_000, 0, "A"), a.suivante(2_000))
    }

    @Test
    fun ne_recule_pas_si_l_horloge_recule() { // SYN-07
        val suivante = a.suivante(500)
        assertEquals(Hlc(1_000, 1, "A"), suivante)
        assertTrue(suivante > a)
    }

    @Test
    fun meme_milliseconde_incremente_le_compteur() { // ajouté après le premier passage de Pitest
        assertEquals(Hlc(1_000, 1, "A"), a.suivante(1_000))
    }

    @Test
    fun reception_prend_le_plus_grand() {
        assertEquals(Hlc(3_000, 5, "A"), a.recue(Hlc(3_000, 4, "B"), 2_000))
        assertEquals(Hlc(1_000, 8, "A"), Hlc(1_000, 2, "A").recue(Hlc(1_000, 7, "B"), 900))
        assertEquals(Hlc(1_000, 3, "A"), Hlc(1_000, 2, "A").recue(Hlc(900, 7, "B"), 900))
        assertEquals(Hlc(4_000, 0, "A"), a.recue(Hlc(3_000, 4, "B"), 4_000))
    }

    @Test
    fun ordre_total_avec_l_appareil() {
        assertTrue(Hlc(1_000, 0, "B") > Hlc(1_000, 0, "A"))
        assertTrue(Hlc(1_000, 1, "A") > Hlc(1_000, 0, "B"))
    }

    @Test
    fun horloge_systeme_positive() {
        assertTrue(horlogeSystemeMillis() > 0)
    }
}
