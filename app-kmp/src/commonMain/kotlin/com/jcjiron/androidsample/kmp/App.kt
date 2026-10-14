package com.jcjiron.androidsample.kmp

import androidx.compose.runtime.Composable
import com.jcjiron.androidsample.kmp.presentation.characters.CharactersScreen
import com.jcjiron.androidsample.ui.main.MainScaffold
import com.jcjiron.androidsample.ui.theme.AndroidSampleTheme
import org.koin.compose.KoinContext

/** Pantalla raíz. Es la MISMA en Android y en iOS. */
@Composable
fun App() {
    KoinContext {
        AndroidSampleTheme {
            MainScaffold(architecture = "KMP") { snackbarHostState, modifier ->
                CharactersScreen(snackbarHostState, modifier)
            }
        }
    }
}
