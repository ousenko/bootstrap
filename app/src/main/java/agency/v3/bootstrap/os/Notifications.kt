package agency.v3.bootstrap.os

import agency.v3.bootstrap.BootstrapApp
import agency.v3.bootstrap.R
import agency.v3.bootstrap.RootActivity
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.StringRes
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

/**
 * Enumeration of application's notification channels
 * */
@TargetApi(26)
enum class AppNotificationChannel(val channelId: String,
                                  @StringRes val channelNameResId: Int,
                                  @StringRes val descriptionResId: Int,
                                  val importance: Int) {

    SOME_CHANNEL(
            channelId = "IMPORTANT CHANNEL",
            channelNameResId = R.string.notificaiton_channel_name,
            descriptionResId = R.string.notificaiton_channel_description,
            importance = NotificationManager.IMPORTANCE_HIGH
            );
}


/**
 * An applications's facade for issuing notifications
 * */
class NotificationsService(private val context: Context,
                           private val notificationManager: NotificationManagerCompat
) {

    companion object {
        private var id = 0
    }



    @Synchronized fun issueNotification(title: String?, text: String?, data: Map<String, String>) {
        if (BootstrapApp.inForeground) return

        val builder = NotificationCompat.Builder(context, AppNotificationChannel.SOME_CHANNEL.channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
//                .setSmallIcon(R.drawable.ic_notification)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.priority = NotificationManager.IMPORTANCE_HIGH
        } else {
            @Suppress("DEPRECATION")
            builder.priority = Notification.PRIORITY_HIGH
        }
        val intent = Intent(context, RootActivity::class.java)
        for (key in data.keys) {
            intent.putExtra(key, data[key])
        }
        id++
        val contentIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(contentIntent)
        notificationManager.notify(id, builder.build())
    }
}