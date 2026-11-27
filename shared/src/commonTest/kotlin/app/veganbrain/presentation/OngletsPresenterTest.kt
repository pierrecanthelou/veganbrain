package app.veganbrain.presentation

import app.cash.turbine.test
import app.veganbrain.domaine.Onglet
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** LIS-08 (docs/criteres.md:47) : l'onglet de l'écran courant est marqué comme courant. */
class OngletsPresenterTest {

    @Test
    fun produitsEstCourantAuLancement() {
        val presenter = OngletsPresenter()
        assertEquals(Onglet.Produits, presenter.etat.value.courant)
    }

    @Test
    fun surRecettesLongletRecettesEstCourant() = runTest {
        val presenter = OngletsPresenter()
        presenter.etat.test {
            assertEquals(Onglet.Produits, awaitItem().courant)
            presenter.envoyer(OngletsIntention.Choisir(Onglet.Recettes))
            val etat = awaitItem()
            assertEquals(Onglet.Recettes, etat.courant)
            assertEquals(false, etat.estCourant(Onglet.Produits))
            assertEquals(true, etat.estCourant(Onglet.Recettes))
        }
    }

    @Test
    fun retourSurProduitsRendProduitsCourant() = runTest {
        val presenter = OngletsPresenter()
        presenter.envoyer(OngletsIntention.Choisir(Onglet.Recettes))
        assertEquals(Onglet.Recettes, presenter.etat.value.courant)
        presenter.envoyer(OngletsIntention.Choisir(Onglet.Produits))
        assertEquals(Onglet.Produits, presenter.etat.value.courant)
    }
}
