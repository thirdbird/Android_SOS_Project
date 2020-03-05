package com.example.sosapplication

import android.annotation.SuppressLint
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
import android.view.Menu
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_contacts.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.widget.*
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId


class MainActivity : AppCompatActivity(),
    groups.OnFragmentInteractionListener,
    contacts.OnFragmentInteractionListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth

    private var database = FirebaseFirestore.getInstance()


    override fun onFragmentInteraction(uri: Uri) {

    }


    fun onBtnClick(view: View) {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    @SuppressLint("StringFormatInvalid")
    fun onSOSClick(view: View) {
        var userId = auth.currentUser?.uid.toString()
        var userRef = database.collection("users").document(userId)

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
        auth = FirebaseAuth.getInstance()
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
