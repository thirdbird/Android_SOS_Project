package com.example.sosapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.TextMessage
import com.example.sosapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var auth: FirebaseAuth
    private var database = FirebaseFirestore.getInstance()


    val TAG = "FCM Service"

    companion object {
        const val KEY_BOOLEAN = "BOOLEAN"
    }

    var boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null){
            savedInstanceState.getBoolean(KEY_BOOLEAN)
            var state = "$boolean"
            val button: Button = view.findViewById(R.id.button)

            if (state == "1"){
                button.setBackgroundResource(R.mipmap.ic_sosbutton_active_foreground)
            }

        }

    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_BOOLEAN, boolean)
    }

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
}