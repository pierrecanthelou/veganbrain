package veganbrain.spike

/** Provenance d'une valeur de champ : saisie par l'un de nous, ou préremplie par Open Food Facts. */
sealed interface Origine {
    data object Saisie : Origine
    data object OpenFoodFacts : Origine
}

data class ValeurChamp<T>(val valeur: T?, val hlc: Hlc, val origine: Origine)

/**
 * Fusion LWW par champ (SYN-05, SYN-06) avec l'exception OFF (SCN-14) :
 * une valeur OFF ne s'écrit que dans un champ vide et cède devant toute saisie, quelle que soit l'HLC.
 * Choix du spike, plus simple que SCN-14 : « champ vide » = clé absente ; deux valeurs OFF se départagent par l'HLC.
 */
object Fusion {

    fun <T> champ(locale: ValeurChamp<T>?, distante: ValeurChamp<T>?): ValeurChamp<T>? = when {
        locale == null -> distante
        distante == null -> locale
        else -> deux(locale, distante)
    }

    private fun <T> deux(a: ValeurChamp<T>, b: ValeurChamp<T>): ValeurChamp<T> = when {
        a.origine is Origine.OpenFoodFacts && b.origine is Origine.Saisie -> b
        a.origine is Origine.Saisie && b.origine is Origine.OpenFoodFacts -> a
        a.hlc >= b.hlc -> a
        else -> b
    }

    /** Fusionne deux fiches champ par champ ; un champ absent d'un côté est gardé de l'autre (SYN-06). */
    fun fiche(
        locale: Map<String, ValeurChamp<String>>,
        distante: Map<String, ValeurChamp<String>>,
    ): Map<String, ValeurChamp<String>> =
        (locale.keys + distante.keys).associateWith { cle -> champ(locale[cle], distante[cle])!! }

    /** Version suspendue, pour produire le code de coroutine que le plugin Kotlin d'Arcmutate filtre. */
    suspend fun ficheAsync(
        locale: Map<String, ValeurChamp<String>>,
        source: suspend () -> Map<String, ValeurChamp<String>>?,
    ): Map<String, ValeurChamp<String>> {
        val distante = source() ?: return locale
        return fiche(locale, distante)
    }
}
