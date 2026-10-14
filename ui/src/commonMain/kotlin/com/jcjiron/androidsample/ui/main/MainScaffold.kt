package com.jcjiron.androidsample.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.jcjiron.androidsample.ui.kotlinbasics.KotlinVariablesScreen
import com.jcjiron.androidsample.ui.kotlinbasics.ScopeFunctionsScreen
import com.jcjiron.androidsample.ui.resources.Res
import com.jcjiron.androidsample.ui.resources.architecture_label
import com.jcjiron.androidsample.ui.resources.tab_characters
import com.jcjiron.androidsample.ui.resources.tab_scope_functions
import com.jcjiron.androidsample.ui.resources.tab_variables
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Las secciones de la app, una por pestaña de la barra de abajo. */
enum class MainTab(val title: StringResource, val icon: ImageVector) {
    CHARACTERS(Res.string.tab_characters, Icons.Filled.Person),
    VARIABLES(Res.string.tab_variables, Icons.Filled.Info),
    SCOPE_FUNCTIONS(Res.string.tab_scope_functions, Icons.Filled.Build),
}

/**
 * Esqueleto común de las tres apps: barra superior, pestañas y snackbar.
 *
 * @param architecture nombre del patrón ("MVVM", "MVI", "MVP") que se muestra en la barra.
 * @param charactersTab la pestaña de personajes; cada app la arma con su propio patrón.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    architecture: String,
    charactersTab: @Composable (snackbarHostState: SnackbarHostState, modifier: Modifier) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.CHARACTERS) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(selectedTab.title))
                        Text(
                            text = stringResource(Res.string.architecture_label, architecture),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.title)) },
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        val contentModifier = Modifier.padding(padding)
        when (selectedTab) {
            MainTab.CHARACTERS -> charactersTab(snackbarHostState, contentModifier)
            MainTab.VARIABLES -> KotlinVariablesScreen(contentModifier)
            MainTab.SCOPE_FUNCTIONS -> ScopeFunctionsScreen(contentModifier)
        }
    }
}
