package com.jcjiron.androidsample.components.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * BROADCAST RECEIVER registrado mientras la pantalla está visible.
 * Se registra al entrar y se quita al salir (`onDispose`) para no fugar memoria.
 *
 * `RECEIVER_NOT_EXPORTED`: otras apps no pueden mandarle broadcasts. Los del sistema
 * (como el de modo avión) sí llegan, porque Android siempre puede entregarlos.
 */
@Composable
fun BroadcastReceiverEffect(action: String, onReceive: (Intent) -> Unit) {
    val context = LocalContext.current
    val currentOnReceive by rememberUpdatedState(onReceive)

    DisposableEffect(context, action) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                currentOnReceive(intent)
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(action),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        onDispose { context.unregisterReceiver(receiver) }
    }
}

/** Broadcast DEL SISTEMA: Android avisa cada vez que se prende o apaga el modo avión. */
@Composable
fun rememberAirplaneModeOn(): Boolean {
    val context = LocalContext.current
    var isOn by remember { mutableStateOf(isAirplaneModeOn(context)) }

    BroadcastReceiverEffect(Intent.ACTION_AIRPLANE_MODE_CHANGED) { intent ->
        isOn = intent.getBooleanExtra("state", false)
    }
    return isOn
}

private fun isAirplaneModeOn(context: Context): Boolean =
    Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
