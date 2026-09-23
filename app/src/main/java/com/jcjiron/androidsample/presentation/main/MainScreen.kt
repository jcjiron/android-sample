package com.jcjiron.androidsample.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jcjiron.androidsample.R
import com.jcjiron.androidsample.domain.model.Character
import com.jcjiron.androidsample.domain.model.CharacterStatus
import com.jcjiron.androidsample.presentation.theme.AndroidSampleTheme
import com.jcjiron.androidsample.presentation.theme.AppTheme

private const val LOAD_MORE_THRESHOLD = 4

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MainContent(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onErrorShown = viewModel::onErrorShown,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    uiState: MainUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onErrorShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorText = uiState.errorMessage?.let { stringResource(it) }
    val retryLabel = stringResource(R.string.retry)

    LaunchedEffect(errorText) {
        if (errorText != null) {
            val result = snackbarHostState.showSnackbar(
                message = errorText,
                actionLabel = retryLabel,
                duration = SnackbarDuration.Short,
            )
            onErrorShown()
            if (result == SnackbarResult.ActionPerformed) onRefresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.main_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (uiState.characters.isEmpty()) {
                EmptyState(showMessage = !uiState.isRefreshing)
            } else {
                CharacterList(
                    characters = uiState.characters,
                    isLoadingMore = uiState.isLoadingMore,
                    onLoadMore = onLoadMore,
                )
            }
        }
    }
}

@Composable
private fun CharacterList(
    characters: List<Character>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            layoutInfo.totalItemsCount > 0 &&
                lastVisible >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    val dimens = AppTheme.dimens
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(dimens.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(dimens.spaceMedium),
    ) {
        items(characters, key = { it.id }) { character ->
            CharacterCard(character)
        }
        if (isLoadingMore) {
            item(key = "loading") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimens.spaceMedium),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun CharacterCard(character: Character) {
    val dimens = AppTheme.dimens
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimens.cardElevation),
    ) {
        Row(
            modifier = Modifier.padding(dimens.spaceMedium),
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(dimens.avatarSize)
                    .clip(MaterialTheme.shapes.medium),
            )
            Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXSmall)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                StatusBadge(character.status)
                Text(
                    text = stringResource(
                        R.string.character_species_gender,
                        character.species,
                        character.gender,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.character_location, character.location),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: CharacterStatus) {
    val colors = MaterialTheme.colorScheme
    val (container, content, label) = when (status) {
        CharacterStatus.ALIVE -> Triple(colors.primaryContainer, colors.onPrimaryContainer, R.string.status_alive)
        CharacterStatus.DEAD -> Triple(colors.errorContainer, colors.onErrorContainer, R.string.status_dead)
        CharacterStatus.UNKNOWN -> Triple(colors.surfaceVariant, colors.onSurfaceVariant, R.string.status_unknown)
    }
    Surface(
        color = container,
        contentColor = content,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = AppTheme.dimens.spaceSmall,
                vertical = AppTheme.dimens.spaceXSmall,
            ),
        )
    }
}

@Composable
private fun EmptyState(showMessage: Boolean) {
    // LazyColumn para que el pull-to-refresh funcione aunque no haya datos.
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillParentMaxSize()
                    .padding(AppTheme.dimens.spaceLarge),
                contentAlignment = Alignment.Center,
            ) {
                if (showMessage) {
                    Text(
                        text = stringResource(R.string.empty_state),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private val previewCharacters = listOf(
    Character(1, "Rick Sanchez", CharacterStatus.ALIVE, "Human", "Male", "Earth (C-137)", "Citadel of Ricks", ""),
    Character(2, "Morty Smith", CharacterStatus.ALIVE, "Human", "Male", "unknown", "Citadel of Ricks", ""),
    Character(8, "Adjudicator Rick", CharacterStatus.DEAD, "Human", "Male", "unknown", "Citadel of Ricks", ""),
    Character(6, "Abadango Cluster Princess", CharacterStatus.UNKNOWN, "Alien", "Female", "Abadango", "Abadango", ""),
)

@Preview(name = "Light", showBackground = true)
@Composable
private fun MainContentLightPreview() {
    AndroidSampleTheme(darkTheme = false) {
        MainContent(MainUiState(characters = previewCharacters), {}, {}, {})
    }
}

@Preview(name = "Dark", showBackground = true)
@Composable
private fun MainContentDarkPreview() {
    AndroidSampleTheme(darkTheme = true) {
        MainContent(MainUiState(characters = previewCharacters), {}, {}, {})
    }
}
