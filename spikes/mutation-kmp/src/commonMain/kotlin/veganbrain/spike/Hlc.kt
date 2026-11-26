package veganbrain.spike

/** Horloge logique hybride (docs/cadrage.md:205-206). Choix de conception : HLC non documentée par une source officielle. */
data class Hlc(val murMillis: Long, val compteur: Int, val appareil: String) : Comparable<Hlc> {

    override fun compareTo(other: Hlc): Int =
        compareValuesBy(this, other, Hlc::murMillis, Hlc::compteur, Hlc::appareil)

    /** Événement local : ne recule jamais, même si l'horloge système recule (SYN-07). */
    fun suivante(maintenant: Long): Hlc =
        if (maintenant > murMillis) copy(murMillis = maintenant, compteur = 0)
        else copy(compteur = compteur + 1)

    /** Réception d'un horodatage distant. */
    fun recue(distante: Hlc, maintenant: Long): Hlc {
        val mur = maxOf(murMillis, distante.murMillis, maintenant)
        val compteurSuivant = when {
            mur == murMillis && mur == distante.murMillis -> maxOf(compteur, distante.compteur) + 1
            mur == murMillis -> compteur + 1
            mur == distante.murMillis -> distante.compteur + 1
            else -> 0
        }
        return Hlc(mur, compteurSuivant, appareil)
    }
}

/** Point d'expect/actual : le vrai module devra fournir un actual JVM pour que la mutation tourne. */
expect fun horlogeSystemeMillis(): Long
