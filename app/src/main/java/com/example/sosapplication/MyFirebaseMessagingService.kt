package com.example.sosapplication

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var msgService: FirebaseInstanceId

    val TAG = "FCM Service"

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From" + remoteMessage!!.from)
        Log.d(TAG, "Notifcation body" + remoteMessage.notification!!)

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

}