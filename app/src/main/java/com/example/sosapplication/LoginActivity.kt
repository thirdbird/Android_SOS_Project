package com.example.sosapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_register
import kotlinx.android.synthetic.main.activity_login.login_password
import kotlinx.android.synthetic.main.activity_login.login_email


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var TAG = "CometChat"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        val appID:String="150750f6f6d76d9"
        val region:String="eu"

        val appSettings = AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region).build()

        CometChat.init(this,appID,appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                Log.d(TAG, "Initialization completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d(TAG, "Initialization failed with exception: " + p0?.message)
            }

        })
        btn_login.setOnClickListener {
            performLogin()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun performLogin() {
        if (!Patterns.EMAIL_ADDRESS.matcher(login_email.text.toString()).matches()) {
            login_email.error = getString(R.string.not_valid_email)
            login_email.requestFocus()
            return
        }

        if (login_password.text.toString().isEmpty()) {
            login_password.error = getString(R.string.no_password)
            login_password.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(login_email.text.toString(), login_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val UID:String=user!!.uid // UID of the user to login
                    val apiKey:String="efeac4787187ffcacf354f5c498b0a8c364a7a78" // API Key

                    if (CometChat.getLoggedInUser() == null) {
                        CometChat.login(UID,apiKey, object : CometChat.CallbackListener<User>() {
                            override fun onSuccess(user: User?) {
                                Log.d(TAG, "Login Successful : " + user?.toString())

                                val topicId = getString(R.string.comet_app_id) + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" + user?.uid
                                FirebaseMessaging.getInstance().subscribeToTopic(topicId)

                                val GUID:String="allusers"
                                val groupType:String=CometChatConstants.GROUP_TYPE_PUBLIC
                                val password:String=""

                                CometChat.joinGroup(GUID,groupType,password,object:CometChat.CallbackListener<Group>(){
                                    override fun onSuccess(p0: Group?) {
                                        Log.d(TAG, p0.toString())
                                    }
                                    override fun onError(p0: CometChatException?) {
                                        Log.d(TAG, "Group joining failed with exception: " + p0?.message)
                                    }
                                })

                            }

                            override fun onError(user: CometChatException?) {
                                Log.d(TAG, "Login failed with exception: " +  user?.message)
                            }

                        })
                    }else{
                        // User already logged in
                    }


                    updateUI(user)
                } else {
                    Toast.makeText(
                        baseContext, getString(R.string.login_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    baseContext, getString(R.string.login_verify),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                baseContext, getString(R.string.login_fail),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
