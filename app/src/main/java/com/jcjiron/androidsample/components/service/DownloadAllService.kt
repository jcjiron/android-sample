package com.jcjiron.androidsample.components.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.jcjiron.androidsample.R
import com.jcjiron.androidsample.components.receiver.DownloadEvents
import com.jcjiron.androidsample.core.domain.repository.CharacterRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SERVICE: trabajo que sigue corriendo aunque el usuario salga de la pantalla.
 *
 * Es un *foreground service* de tipo `dataSync`: descarga todas las páginas de personajes a Room
 * para usar la app sin internet, y muestra el avance en una notificación (Android obliga a que
 * un foreground service tenga notificación visible). Al terminar avisa con un broadcast propio.
 *
 * Play Store: el tipo `dataSync` se declara en el manifiesto y en Play Console
 * (App content → Foreground service permissions) con una explicación y un video corto.
 */
@AndroidEntryPoint
class DownloadAllService : Service() {

    @Inject
    lateinit var repository: CharacterRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (job?.isActive == true) return START_NOT_STICKY

        createChannel()
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            progressNotification(page = 0, totalPages = 0),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )

        job = scope.launch {
            val result = repository.downloadAll { page, totalPages ->
                showNotification(progressNotification(page, totalPages))
            }
            result.exceptionOrNull()?.let { Log.w(TAG, "La descarga falló", it) }
            sendBroadcast(DownloadEvents.finishedIntent(this@DownloadAllService, result.isSuccess))
            ServiceCompat.stopForeground(this@DownloadAllService, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.download_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun progressNotification(page: Int, totalPages: Int): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(getString(R.string.download_notification_title))
            .setContentText(
                if (totalPages == 0) {
                    getString(R.string.download_notification_starting)
                } else {
                    getString(R.string.download_notification_progress, page, totalPages)
                },
            )
            .setProgress(totalPages, page, totalPages == 0)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .build()

    private fun showNotification(notification: Notification) {
        // Desde Android 13 mostrar notificaciones requiere permiso del usuario.
        val canNotify = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (canNotify) {
            getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        private const val TAG = "DownloadAllService"
        private const val CHANNEL_ID = "download_all"
        private const val NOTIFICATION_ID = 1

        fun start(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, DownloadAllService::class.java))
        }
    }
}
