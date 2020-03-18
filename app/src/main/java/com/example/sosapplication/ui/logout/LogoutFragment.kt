package com.example.sosapplication.ui.logout

import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.sosapplication.LoginActivity
import com.example.sosapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class LogoutFragment : Fragment() {

    private lateinit var logoutViewModel: LogoutViewModel
    private lateinit var auth: FirebaseAuth

    private val TAG = "CometChat LOGOUT"

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

        CometChat.logout(object : CometChat.CallbackListener<String>(){
            override fun onSuccess(p0: String?) {
                Log.d(TAG, "Logout completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Logout failed with exception: " + p0?.message)
            }
        })

        startActivity(Intent(activity, LoginActivity::class.java))
    }
}