package com.jcjiron.androidsample.presentation.characters

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.jcjiron.androidsample.R
import com.jcjiron.androidsample.components.provider.CharactersContract
import com.jcjiron.androidsample.components.receiver.BroadcastReceiverEffect
import com.jcjiron.androidsample.components.receiver.DownloadEvents
import com.jcjiron.androidsample.components.receiver.rememberAirplaneModeOn
import com.jcjiron.androidsample.core.presentation.characters.CharactersContent
import com.jcjiron.androidsample.core.presentation.theme.AppTheme
import com.jcjiron.androidsample.presentation.detail.CharacterDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** MVVM: la pantalla observa los LiveData del ViewModel y le avisa de los eventos. */
@Composable
fun CharactersScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: CharactersViewModel = hiltViewModel(),
) {
    // observeAsState convierte cada LiveData en estado de Compose.
    val characters by viewModel.characters.observeAsState(emptyList())
    val isRefreshing by viewModel.isRefreshing.observeAsState(false)
    val isLoadingMore by viewModel.isLoadingMore.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isAirplaneModeOn = rememberAirplaneModeOn()

    // Broadcast propio: el DownloadAllService avisa que terminó. El total se lee a través del
    // ContentProvider, igual que lo haría otra app de la empresa firmada con la misma llave.
    BroadcastReceiverEffect(DownloadEvents.ACTION_FINISHED) { intent ->
        scope.launch {
            val message = if (intent.getBooleanExtra(DownloadEvents.EXTRA_SUCCESS, false)) {
                val total = withContext(Dispatchers.IO) { countCachedCharacters(context) }
                context.getString(R.string.download_finished, total)
            } else {
                context.getString(R.string.download_failed)
            }
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
        }
    }

    Column(modifier = modifier) {
        if (isAirplaneModeOn) AirplaneModeBanner()
        CharactersContent(
            characters = characters,
            isRefreshing = isRefreshing,
            isLoadingMore = isLoadingMore,
            errorMessage = errorMessage,
            snackbarHostState = snackbarHostState,
            onRefresh = viewModel::refresh,
            onLoadMore = viewModel::loadMore,
            onErrorShown = viewModel::onErrorShown,
            // Activity: Intent explícito hacia la pantalla de detalle.
            onCharacterClick = { character ->
                context.startActivity(CharacterDetailActivity.intent(context, character.id))
            },
        )
    }
}

@Composable
private fun AirplaneModeBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        Text(
            text = stringResource(R.string.airplane_mode_banner),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(AppTheme.dimens.spaceMedium),
        )
    }
}

private fun countCachedCharacters(context: Context): Int =
    context.contentResolver
        .query(CharactersContract.contentUri(context), null, null, null, null)
        ?.use { it.count }
        ?: 0
