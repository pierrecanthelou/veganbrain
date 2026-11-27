package app.veganbrain.design

import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * A11-01 (docs/criteres.md:204) : chaque paire exigée de docs/design/contrastes.md est recalculée
 * depuis tokens.json (via Jetons, généré par la tâche genererJetons) et atteint son seuil.
 *
 * Méthode de contrastes.md:7-17 : formule WCAG 2.2, seuils 4,5:1 (texte) et 3:1 (focus) sans arrondi,
 * mélanges alpha arrondis à 8 bits par canal, fond réellement rendu (halos à leur point le plus intense).
 *
 * Paires retenues : les 30 lignes « Texte (1.4.3) » (contrastes.md:25-56, sauf 44-45) et les 14 lignes
 * « Retenu (focus) » (contrastes.md:65-78). Écartées, comme le dit A11-01 : les lignes « ancien »
 * (57-59), les repères visuels non exigés (63-64), et les paires en attente des points 13 à 15
 * d'À trancher (docs/suivi.md) : bordure (60-61), pastille choisie (62), texte au cœur des halos (44-45).
 */
class ContrastesTest {

    private enum class Critere(val seuil: Double) { Texte(4.5), Focus(3.0) }

    private class Paire(
        val ligne: Int,
        val critere: Critere,
        val premierPlanClair: Palette.() -> Couleur,
        val premierPlanSombre: Palette.() -> Couleur,
        val fond: Palette.() -> Couleur,
        /** Valeurs de contrastes.md, à deux décimales. */
        val clair: String,
        val sombre: String,
    )

    private fun texte(ligne: Int, pp: Palette.() -> Couleur, fond: Palette.() -> Couleur, clair: String, sombre: String) =
        Paire(ligne, Critere.Texte, pp, pp, fond, clair, sombre)

    private fun focus(ligne: Int, fond: Palette.() -> Couleur, clair: String, sombre: String) =
        Paire(ligne, Critere.Focus, { encreDouce }, { encreDouce }, fond, clair, sombre)

    private val paires = listOf(
        texte(25, { encre }, { fond }, "10,97", "14,73"),
        texte(26, { encre }, { fondHaut }, "11,40", "13,96"),
        texte(27, { encre }, { fondBas }, "10,58", "15,44"),
        texte(28, { encre }, { surface }, "12,09", "12,81"),
        texte(29, { encre }, { vignette }, "9,92", "10,06"),
        texte(30, { encre }, { potironClair }, "8,79", "10,50"),
        texte(31, { encre }, { violetteClair }, "9,21", "12,23"),
        texte(32, { encre }, { dangerClair }, "9,60", "11,81"),
        texte(33, { encreSurCouleur }, { menthe }, "7,54", "8,51"),
        texte(34, { encreSurCouleur }, { potiron }, "5,13", "6,44"),
        texte(35, { encreSurCouleur }, { violette }, "4,97", "6,05"),
        texte(36, { encreSurCouleur }, { noteSuper }, "6,07", "7,64"),
        texte(37, { encreSurCouleur }, { noteBon }, "10,06", "11,81"),
        texte(38, { encreSurCouleur }, { notePasBon }, "7,84", "9,04"),
        texte(39, { encreDouce }, { fond }, "5,68", "8,99"),
        texte(40, { encreDouce }, { fondHaut }, "5,91", "8,52"),
        texte(41, { encreDouce }, { fondBas }, "5,48", "9,42"),
        texte(42, { encreDouce }, { surface }, "6,26", "7,82"),
        texte(43, { encreDouce }, { vignette }, "5,14", "6,14"),
        // 44-45 : en attente (point 15).
        texte(46, { encreDouce }, { fondHaut + haloMenthe }, "4,74", "5,26"),
        texte(47, { encreDouce }, { fondHaut + verre + repos }, "5,40", "6,69"),
        texte(48, { encreDouce }, { fondHaut + haloMenthe + verre + repos }, "4,97", "5,49"),
        texte(49, { encreDouce }, { fondHaut + repos }, "5,24", "7,09"),
        texte(50, { encreDouce }, { fondBas + repos }, "4,85", "7,94"),
        texte(51, { encreDouce }, { fondBas + pisteProduits }, "4,84", "7,07"),
        texte(52, { encreDouce }, { fondBas + pisteRecettes }, "4,87", "7,33"),
        texte(53, { danger }, { surface }, "6,06", "6,69"),
        texte(54, { danger }, { fondHaut }, "5,72", "7,29"),
        texte(55, { danger }, { dangerClair }, "4,81", "6,17"),
        // Encart du scan : encre-sur-couleur en clair, encre en sombre (README, Fiches).
        Paire(56, Critere.Texte, { encreSurCouleur }, { encre }, { fondHaut + encartScan }, "9,15", "8,62"),
        focus(65, { fondHaut }, "5,91", "8,52"),
        focus(66, { fondBas }, "5,48", "9,42"),
        focus(67, { surface }, "6,26", "7,82"),
        focus(68, { fondHaut + verre }, "6,12", "8,11"),
        focus(69, { fondHaut + haloMenthe + verre }, "5,61", "6,70"),
        focus(70, { fondHaut + haloMenthe }, "4,74", "5,26"),
        // Halos de section : dans l'angle bas, donc sur fond-bas (README, Couleurs).
        focus(71, { fondBas + haloPotiron }, "4,35", "7,08"),
        focus(72, { fondBas + haloViolet }, "3,63", "7,15"),
        focus(73, { fondBas + pisteProduits }, "4,84", "7,07"),
        focus(74, { fondBas + pisteRecettes }, "4,87", "7,33"),
        focus(75, { potironClair }, "4,56", "6,41"),
        focus(76, { violetteClair }, "4,77", "7,46"),
        focus(77, { vignette }, "5,14", "6,14"),
        focus(78, { fondHaut + encartScan }, "4,74", "5,26"),
    )

    private class Mesure(val paire: Paire, val theme: String, val ratio: Double, val attendu: String)

    private fun mesures(): List<Mesure> = paires.flatMap { p ->
        listOf(
            Mesure(p, "clair", contraste(p.premierPlanClair(Jetons.clair), p.fond(Jetons.clair)), p.clair),
            Mesure(p, "sombre", contraste(p.premierPlanSombre(Jetons.sombre), p.fond(Jetons.sombre)), p.sombre),
        )
    }

    @Test
    fun lesPairesExigeesSontToutesLa() {
        assertEquals(30, paires.count { it.critere == Critere.Texte })
        assertEquals(14, paires.count { it.critere == Critere.Focus })
        assertEquals(paires.size, paires.map { it.ligne }.toSet().size, "ligne de contrastes.md en double")
    }

    @Test
    fun chaquePaireExigeeAtteintSonSeuil() {
        val echecs = mesures().filter { it.ratio < it.paire.critere.seuil }
            .map { "contrastes.md:${it.paire.ligne} (${it.theme}) : ${it.ratio} < ${it.paire.critere.seuil}" }
        assertTrue(echecs.isEmpty(), echecs.joinToString("\n"))
    }

    /** Si tokens.json change, contrastes.md est à refaire (contrastes.md:3) : ce test le signale. */
    @Test
    fun chaqueRatioRetrouveLaValeurDeContrastesMd() {
        val ecarts = mesures().filter { (it.ratio * 100).roundToLong() != centiemes(it.attendu) }
            .map { "contrastes.md:${it.paire.ligne} (${it.theme}) : calculé ${it.ratio}, documenté ${it.attendu}" }
        assertTrue(ecarts.isEmpty(), ecarts.joinToString("\n"))
    }

    @Test
    fun laFormuleRetrouveLesBornesDeWcag() {
        assertEquals(21.0, contraste(Couleur(0x000000), Couleur(0xFFFFFF)), 1e-9)
        assertEquals(1.0, contraste(Couleur(0x1F3A2B), Couleur(0x1F3A2B)), 1e-9)
    }

    @Test
    fun leMelangeArrondiA8Bits() {
        // 0,5 × 255 + 0,5 × 0 = 127,5 → 128 ; 0,07 × 31 + 0,93 × 240 = 225,37 → 225.
        assertEquals(Couleur(0x808080), Couleur(0x000000) + Teinte(Couleur(0xFFFFFF), 0.5))
        assertEquals(225, (Couleur(0xF0F0F0) + Teinte(Couleur(0x1F1F1F), 0.07)).rouge)
    }

    private fun centiemes(valeur: String): Long = valeur.replace(",", "").toLong()
}

/** Teinte posée sur une couleur opaque, arrondie à 8 bits par canal (contrastes.md:16). */
private operator fun Couleur.plus(dessus: Teinte): Couleur {
    fun canal(haut: Int, bas: Int) = (dessus.alpha * haut + (1 - dessus.alpha) * bas).roundToInt()
    val c = dessus.couleur
    return Couleur((canal(c.rouge, rouge) shl 16) or (canal(c.vert, vert) shl 8) or canal(c.bleu, bleu))
}

/** Luminance relative WCAG 2.2, seuil de linéarisation 0,04045 (contrastes.md:7-9). */
private fun Couleur.luminance(): Double {
    fun lineaire(c: Int): Double {
        val v = c / 255.0
        return if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }
    return 0.2126 * lineaire(rouge) + 0.7152 * lineaire(vert) + 0.0722 * lineaire(bleu)
}

private fun contraste(a: Couleur, b: Couleur): Double {
    val (clair, fonce) = listOf(a.luminance(), b.luminance()).sortedDescending()
    return (clair + 0.05) / (fonce + 0.05)
}
