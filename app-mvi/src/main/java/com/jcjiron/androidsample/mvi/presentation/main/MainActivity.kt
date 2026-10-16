package com.jcjiron.androidsample.mvi.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jcjiron.androidsample.core.presentation.main.MainScaffold
import com.jcjiron.androidsample.core.presentation.theme.AndroidSampleTheme
import com.jcjiron.androidsample.mvi.presentation.characters.CharactersScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSampleTheme {
                MainScaffold(architecture = "MVI") { snackbarHostState, modifier ->
                    CharactersScreen(snackbarHostState, modifier)
                }
            }
        }
    }
}
