package com.jcjiron.androidsample.presentation.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.jcjiron.androidsample.R
import com.jcjiron.androidsample.core.domain.model.Character
import com.jcjiron.androidsample.core.presentation.theme.AndroidSampleTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * ACTIVITY: una pantalla independiente con su propio ciclo de vida.
 *
 * - Se abre con un Intent EXPLÍCITO (sabemos exactamente qué clase abrir): ver [intent].
 * - Comparte con un Intent IMPLÍCITO (`ACTION_SEND`): Android pregunta qué app lo resuelve.
 */
@AndroidEntryPoint
class CharacterDetailActivity : ComponentActivity() {

    private val viewModel: CharacterDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSampleTheme {
                val character by viewModel.character.observeAsState()
                CharacterDetailScreen(
                    character = character,
                    onBack = ::finish,
                    onShare = ::share,
                )
            }
        }
    }

    private fun share(character: Character) {
        val text = getString(
            R.string.share_text,
            character.name,
            character.species,
            character.location,
            "https://rickandmortyapi.com/api/character/${character.id}",
        )
        val send = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(send, getString(R.string.share_chooser_title)))
    }

    companion object {
        const val EXTRA_CHARACTER_ID = "character_id"

        /** Intent explícito: "abre CharacterDetailActivity con este id". */
        fun intent(context: Context, characterId: Int): Intent =
            Intent(context, CharacterDetailActivity::class.java)
                .putExtra(EXTRA_CHARACTER_ID, characterId)
    }
}
