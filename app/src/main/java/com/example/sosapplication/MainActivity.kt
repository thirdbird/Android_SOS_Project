package com.example.sosapplication

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.os.Build
import android.util.Log
import android.widget.*
import androidx.core.app.NotificationCompat
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.TextMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.varunest.sparkbutton.SparkButton
import com.varunest.sparkbutton.SparkEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class MainActivity : AppCompatActivity(),
    groups.OnFragmentInteractionListener,
    contacts.OnFragmentInteractionListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth
    private var database = FirebaseFirestore.getInstance()
    private lateinit var contactsUID: MutableList<String>

    val TAG = "FCM Service"
    val listenerID: String = "MainActivity"

    override fun onFragmentInteraction(uri: Uri) {

    }


    fun logoutUser(view: View) {
        auth = FirebaseAuth.getInstance()
        val topicId =
            getString(R.string.comet_app_id) + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" + auth.currentUser?.uid
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicId)
        auth.signOut()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    @SuppressLint("StringFormatInvalid")
    fun onSOSClick(view: View) {
        var userId = auth.currentUser?.uid.toString()
        var userRef = database.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            var alert = document["alert"]


            if (alert == true) {
                button.setBackgroundResource(R.mipmap.ic_sosbutton_inactive_foreground)
                userRef.update("alert", false)


            } else {
                button.setBackgroundResource(R.mipmap.ic_sosbutton_active_foreground)
                userRef.update("alert", true)
                val receiverID: String = "allusers"
                val messageText: String = "Call emergency services, I'm In trouble,  "
                val receiverType: String = CometChatConstants.RECEIVER_TYPE_GROUP
                val textMessage = TextMessage(receiverID, messageText, receiverType)

                CometChat.sendMessage(
                    textMessage,
                    object : CometChat.CallbackListener<TextMessage>() {
                        override fun onSuccess(p0: TextMessage?) {
                            Log.d(TAG, "Message sent successfully: " + p0?.toString())

                        }

                        override fun onError(p0: CometChatException?) {
                            Log.d(TAG, "Message sending failed with exception: " + p0?.message)
                        }

                    })
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        auth = FirebaseAuth.getInstance()
        var userRef = database.collection("users").document(auth.currentUser?.uid.toString())
        userRef.get().addOnSuccessListener { document ->
            var token = document["token"]
            if (token == null) {
                userRef.update("token", FirebaseInstanceId.getInstance().getToken())
            } else {
                //
            }
        }
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        if (auth.currentUser != null) {

        }
        var navHead = navView.getHeaderView(0)
        var navheadEmail = navHead.findViewById<TextView>(R.id.navhead_email)
        var navheadName = navHead.findViewById<TextView>(R.id.navhead_name)
        var name = auth.currentUser?.displayName
        var email = auth.currentUser?.email
        navheadEmail.setText(email.toString())
        navheadName.setText(name.toString())

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_contacts,
                R.id.nav_groups, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val appID: String = "150750f6f6d76d9" // Replace with your App Id.
        val region: String = "eu" // Replace with the region for your App.

        val appSettings =
            AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region)
                .build()

        CometChat.init(this, appID, appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                Log.d(TAG, "Initialization completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Initialization failed with exception: " + p0?.message)
            }

        })

    }

    override fun onResume() {
        super.onResume()
        CometChat.addMessageListener(listenerID, object : CometChat.MessageListener() {
            override fun onTextMessageReceived(message: TextMessage?) {
                Log.d(TAG, "Text message received successfully: " + message?.toString())

                var notificationBuilder = NotificationCompat.Builder(
                    this@MainActivity,
                    getString(R.string.default_notification_channel_id)
                )
                    .setSmallIcon(R.drawable.ic_app_icon)
                    .setContentTitle(getString(R.string.notis_sos))
                    .setContentText(message?.text + message?.sender?.name)
                    //.setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                //.setContentIntent(PendingIntent.getActivity(this, 0, Intent(this)))
                var notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
        })
    }

    override fun onPause() {
        super.onPause()
        CometChat.removeMessageListener(listenerID)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}


