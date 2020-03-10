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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

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
    //dMOwTpU2Txew7_3VVMoRl1:APA91bECk16D9Hi4N_kVnX79bXPt8Ct-IKklkJi8nwJns_pD_MDU7m31HrZnf_7aF4q0NPsqmsQZR_Yy58syAHYc_7wWopF3VMzUuaTSss-SndFZuYIV9xZQ7DwM4nQDKGiH63TF3S8L
    //fUui2aiYTXykWnKaqXlZs-:APA91bERRulpch2R7PSaXpNq_ou4gIJnRRLrP-1N_a4OxIfHsWC-30FwmXuoD915nBu0TzFt9OZ0zcr0TQH6yYnDE1tRo64i9_O9sBw6_eucTzlkldOCcMlIaew7bNajwDVtnjbZcDv2

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From" + remoteMessage!!.from)
        Log.d(TAG, "Notifcation body" + remoteMessage.notification!!)

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

}