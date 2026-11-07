package com.jcjiron.androidsample.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.jcjiron.androidsample.R
import com.jcjiron.androidsample.components.service.DownloadAllService
import com.jcjiron.androidsample.core.presentation.main.MainScaffold
import com.jcjiron.androidsample.core.presentation.theme.AndroidSampleTheme
import com.jcjiron.androidsample.presentation.characters.CharactersScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSampleTheme {
                MainScaffold(
                    architecture = "MVVM",
                    actions = { DownloadAllButton() },
                ) { snackbarHostState, modifier ->
                    CharactersScreen(snackbarHostState, modifier)
                }
            }
        }
    }
}

/**
 * Arranca el DownloadAllService. En Android 13+ primero pide permiso de notificaciones;
 * la descarga corre aunque el usuario diga que no (solo no se ve el progreso).
 */
@Composable
private fun DownloadAllButton() {
    val context = LocalContext.current
    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { DownloadAllService.start(context) }

    TextButton(
        onClick = {
            val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            if (needsPermission) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                DownloadAllService.start(context)
            }
        },
    ) {
        Text(stringResource(R.string.download_all_action))
    }
}
