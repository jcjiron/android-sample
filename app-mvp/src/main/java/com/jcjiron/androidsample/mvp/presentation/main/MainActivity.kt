package com.jcjiron.androidsample.mvp.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jcjiron.androidsample.ui.main.MainScaffold
import com.jcjiron.androidsample.ui.theme.AndroidSampleTheme
import com.jcjiron.androidsample.mvp.presentation.characters.CharactersContract
import com.jcjiron.androidsample.mvp.presentation.characters.CharactersScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Hilt inyecta el Presenter en la Activity (en MVVM sería un ViewModel).
    @Inject
    lateinit var presenter: CharactersContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSampleTheme {
                MainScaffold(architecture = "MVP") { snackbarHostState, modifier ->
                    CharactersScreen(presenter, snackbarHostState, modifier)
                }
            }
        }
    }
}
