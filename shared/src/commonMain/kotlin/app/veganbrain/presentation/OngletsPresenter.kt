package app.veganbrain.presentation

import app.veganbrain.domaine.Onglet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OngletsEtat(val courant: Onglet = Onglet.Produits) {
    fun estCourant(onglet: Onglet): Boolean = onglet == courant
}

sealed interface OngletsIntention {
    data class Choisir(val onglet: Onglet) : OngletsIntention
}

/** Présentateur commun aux deux plateformes : un état observable, des intentions en entrée. */
class OngletsPresenter {
    private val _etat = MutableStateFlow(OngletsEtat())
    val etat: StateFlow<OngletsEtat> = _etat.asStateFlow()

    fun envoyer(intention: OngletsIntention) {
        when (intention) {
            is OngletsIntention.Choisir -> _etat.update { it.copy(courant = intention.onglet) }
        }
    }
}
