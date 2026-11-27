package app.veganbrain.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import app.veganbrain.android.theme.Theme
import app.veganbrain.android.theme.ThemeVegan
import app.veganbrain.android.theme.compose
import app.veganbrain.domaine.Onglet
import app.veganbrain.presentation.OngletsIntention
import app.veganbrain.presentation.OngletsPresenter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppVegan()
        }
    }
}

/** Racine de l'app, thème compris : c'est elle que vérifie le test A11-08. */
@Composable
fun AppVegan() {
    ThemeVegan {
        EcranPrincipal()
    }
}

@Composable
private fun EcranPrincipal() {
    // Squelette : l'état n'est pas encore conservé à la rotation ni à la mort du processus.
    val presenter = remember { OngletsPresenter() }
    val etat by presenter.etat.collectAsState()

    Scaffold(
        // `fond` est le repli du dégradé fond-haut → fond-bas (README, Couleurs), qui arrivera avec les écrans.
        containerColor = Theme.palette.fond.compose,
        bottomBar = {
            NavigationBar {
                Onglet.entries.forEach { onglet ->
                    val libelle = libelle(onglet)
                    NavigationBarItem(
                        selected = etat.estCourant(onglet),
                        onClick = { presenter.envoyer(OngletsIntention.Choisir(onglet)) },
                        icon = {},
                        label = { Text(libelle) },
                        modifier = Modifier.testTag(identifiant(onglet)),
                    )
                }
            }
        },
    ) { marges ->
        // Écrans vides : leur contenu arrive avec les tranches de phase 3.
        Box(Modifier.fillMaxSize().padding(marges))
    }
}

@Composable
private fun libelle(onglet: Onglet): String = when (onglet) {
    Onglet.Produits -> stringResource(R.string.onglet_produits)
    Onglet.Recettes -> stringResource(R.string.onglet_recettes)
}

/** Identifiants communs Android et iOS : docs/criteres.md §13. */
private fun identifiant(onglet: Onglet): String = when (onglet) {
    Onglet.Produits -> "onglets.produits"
    Onglet.Recettes -> "onglets.recettes"
}
