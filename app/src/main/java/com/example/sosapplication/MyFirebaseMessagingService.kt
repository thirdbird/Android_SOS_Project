package com.example.sosapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage
import com.cometchat.pro.pushnotifications.core.PNExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.karn.notify.Notify
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var msgService: FirebaseInstanceId
    private val database = FirebaseFirestore.getInstance()
    private lateinit var contactsUID: MutableList<String>
    private lateinit var auth: FirebaseAuth
    val TAG = "FCM Service"

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
        var userId = auth.currentUser!!.uid
        database.collection("users").document(userId).update("token",  FirebaseInstanceId.getInstance().getToken())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From" + remoteMessage!!.from)
        Log.d(TAG, "Notifcation body" + remoteMessage.notification!!)

        PNExtension.getMessageFromJson(JSONObject(remoteMessage.data["message"]),
            object : CometChat.CallbackListener<BaseMessage>() {
                override fun onSuccess(baseMessage: BaseMessage?) {
                    when (baseMessage) {
                        is TextMessage -> {
                            // Convert BaseMessage to TextMessage
                            var textMessage = baseMessage
                            // Send notification for this text message here
                            val notificationId = 124952
                            Notify
                                .with(this@MyFirebaseMessagingService)
                                .content { // this: Payload.Content.Default
                                    title = textMessage.sender.name
                                    text = textMessage.text
                                }
                                .alerting("high_priority_notification") {
                                    channelImportance = Notify.IMPORTANCE_HIGH
                                }
                                .show(notificationId)

                        }
                    }
                }

                override fun onError(exception: CometChatException?) {
                    exception?.printStackTrace()
                }
            })
    }

        /*
        var notificationBuilder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.ic_menu_camera)
            .setContentTitle(getString(R.string.notis_sos))
            .setContentText(getString((R.string.notis_call_for_help)))
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_MAX)
        //.setContentIntent(PendingIntent.getActivity(this, 0, Intent(this)))
        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                getString(R.string.notis_sos_channel),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationBuilder.setChannelId(getString(R.string.default_notification_channel_id))

        Log.d(TAG, "Notification: " + notificationManager)
        notificationManager.notify(0, notificationBuilder.build())


    }

    override fun onMessageSent(pendingMessage: String) {
        super.onMessageSent(pendingMessage)
    }



    /*FirebaseInstanceId.getInstance().instanceId
    .addOnCompleteListener(OnCompleteListener
    {
        task ->
        if (!task.isSuccessful) {
            Log.w(TAG, "getInstanceId failed", task.exception)
            return@OnCompleteListener
        }

        // Get new Instance ID token
        val token = task.result?.token

        // Log and toast
        val msg = getString(R.string.msg_token_fmt, token)
        Log.d(TAG, msg)
        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
    })*/

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            //.setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            //.setAutoCancel(true)
            //.setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

     */

}