package com.jcjiron.androidsample.components.receiver

import android.content.Context
import android.content.Intent

/**
 * Broadcast PROPIO de la app: el Service avisa que terminó la descarga.
 * `setPackage` hace que solo lo reciba esta app (nadie más lo puede escuchar).
 */
object DownloadEvents {
    const val ACTION_FINISHED = "com.jcjiron.androidsample.action.DOWNLOAD_FINISHED"
    const val EXTRA_SUCCESS = "success"

    fun finishedIntent(context: Context, success: Boolean): Intent =
        Intent(ACTION_FINISHED)
            .setPackage(context.packageName)
            .putExtra(EXTRA_SUCCESS, success)
}
