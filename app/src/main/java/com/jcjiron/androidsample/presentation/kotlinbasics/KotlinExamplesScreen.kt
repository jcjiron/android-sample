package com.jcjiron.androidsample.presentation.kotlinbasics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jcjiron.androidsample.R
import com.jcjiron.androidsample.presentation.theme.AndroidSampleTheme
import com.jcjiron.androidsample.presentation.theme.AppTheme

@Composable
fun KotlinVariablesScreen(modifier: Modifier = Modifier) {
    KotlinExamplesList(
        intro = stringResource(R.string.variables_intro),
        examples = VariableExamples.all,
        modifier = modifier,
    )
}

@Composable
fun ScopeFunctionsScreen(modifier: Modifier = Modifier) {
    KotlinExamplesList(
        intro = stringResource(R.string.scope_functions_intro),
        examples = ScopeFunctionExamples.all,
        modifier = modifier,
    )
}

@Composable
private fun KotlinExamplesList(
    intro: String,
    examples: List<KotlinExample>,
    modifier: Modifier = Modifier,
) {
    val dimens = AppTheme.dimens
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(dimens.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(dimens.spaceMedium),
    ) {
        item {
            Text(
                text = intro,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(examples, key = { it.title }) { example ->
            KotlinExampleCard(example)
        }
    }
}

@Composable
private fun KotlinExampleCard(example: KotlinExample) {
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
        Column(
            modifier = Modifier.padding(dimens.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceSmall),
        ) {
            Text(text = example.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = example.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            CodeBlock(
                text = example.code,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.example_result),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            CodeBlock(
                text = example.result,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun CodeBlock(
    text: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = text,
            style = AppTheme.codeTextStyle,
            softWrap = false,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(AppTheme.dimens.spaceSmall),
        )
    }
}

@Preview(name = "Variables", showBackground = true)
@Composable
private fun KotlinVariablesPreview() {
    AndroidSampleTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            KotlinVariablesScreen()
        }
    }
}

@Preview(name = "Scope functions (dark)", showBackground = true)
@Composable
private fun ScopeFunctionsPreview() {
    AndroidSampleTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ScopeFunctionsScreen()
        }
    }
}
