package com.example.sosapplication

import android.util.Log
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

    private val database = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    private val TAG = "FCM Service"

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
        var userId = auth.currentUser!!.uid
        database.collection("users").document(userId)
            .update("token", FirebaseInstanceId.getInstance().getToken())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From" + remoteMessage.from)
        Log.d(TAG, "Notifcation body" + remoteMessage.notification)

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
                                .content {
                                    // this: Payload.Content.Default
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

}