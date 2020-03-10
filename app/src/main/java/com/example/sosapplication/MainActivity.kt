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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity(),
    groups.OnFragmentInteractionListener,
    contacts.OnFragmentInteractionListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth
    private var database = FirebaseFirestore.getInstance()
    private lateinit var contactsUID: MutableList<String>

    val TAG = "FCM Service"

    override fun onFragmentInteraction(uri: Uri) {

    }


    fun logoutUser(view: View) {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    @SuppressLint("StringFormatInvalid")
    fun onSOSClick(view: View) {
        var userId = auth.currentUser?.uid.toString()
        var userRef = database.collection("users").document(userId)

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


        database.collection("users").get().addOnSuccessListener { result ->
            for(document in result) {
                //contactsUID.add(document.id)
                //Log.d(TAG, "UserUID: " + FirebaseInstanceId.getInstance().getToken())
            }
        }

        userRef.get().addOnSuccessListener { document ->
            var alert = document["alert"]
            if (alert == true) {
                userRef.update("alert", false)
            } else {
                userRef.update("alert", true)

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
    }
    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
