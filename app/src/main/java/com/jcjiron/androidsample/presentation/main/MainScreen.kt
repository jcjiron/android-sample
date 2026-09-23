package com.jcjiron.androidsample.presentation.main

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import com.jcjiron.androidsample.R
import com.jcjiron.androidsample.presentation.characters.CharactersScreen
import com.jcjiron.androidsample.presentation.kotlinbasics.KotlinVariablesScreen
import com.jcjiron.androidsample.presentation.kotlinbasics.ScopeFunctionsScreen

/** Las secciones de la app, una por pestaña de la barra de abajo. */
enum class MainTab(@StringRes val title: Int, val icon: ImageVector) {
    CHARACTERS(R.string.tab_characters, Icons.Filled.Person),
    VARIABLES(R.string.tab_variables, Icons.Filled.Info),
    SCOPE_FUNCTIONS(R.string.tab_scope_functions, Icons.Filled.Build),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.CHARACTERS) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(selectedTab.title)) },
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
            MainTab.CHARACTERS -> CharactersScreen(snackbarHostState, contentModifier)
            MainTab.VARIABLES -> KotlinVariablesScreen(contentModifier)
            MainTab.SCOPE_FUNCTIONS -> ScopeFunctionsScreen(contentModifier)
        }
    }
}
