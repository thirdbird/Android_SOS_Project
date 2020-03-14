package com.example.sosapplication.ui.logout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cometchat.pro.constants.CometChatConstants
import com.example.sosapplication.LoginActivity
import com.example.sosapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class LogoutFragment : Fragment() {

    private lateinit var logoutViewModel: LogoutViewModel
    private lateinit var auth: FirebaseAuth

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logoutViewModel =
            ViewModelProviders.of(this).get(LogoutViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_logout, container, false)
        val textView: TextView = root.findViewById(R.id.text_logout)
        logoutViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }*/

    override fun onStart() {
        super.onStart()
        logoutUser()
    }

    private fun logoutUser() {
        auth = FirebaseAuth.getInstance()
        val topicId =
            getString(R.string.comet_app_id) + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" + auth.currentUser?.uid
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicId)
        auth.signOut()

        startActivity(Intent(activity, LoginActivity::class.java))
    }
}