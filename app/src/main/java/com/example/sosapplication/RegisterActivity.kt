package com.example.sosapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser(){
        if (!Patterns.EMAIL_ADDRESS.matcher(register_email.text.toString()).matches()){
            register_email.error = getString(R.string.not_valid_email)
            register_email.requestFocus()
            return
        }

        if (register_username.text.toString().isEmpty()){
            register_username.error = getString(R.string.no_username)
            register_username.requestFocus()
            return
        }

        if (register_password.text.toString().isEmpty()){
            register_password.error = getString(R.string.no_password)
            register_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(register_email.text.toString(), register_password.text.toString())
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){


                    //sign in success, update UI with signed-in user's information.
                    val firebaseUser = auth.currentUser

                    val apiKey = "efeac4787187ffcacf354f5c498b0a8c364a7a78"
                    val cometUser = com.cometchat.pro.models.User()
                    cometUser.uid = firebaseUser!!.uid
                    cometUser.name = register_username.text.toString()

                    CometChat.createUser(cometUser , apiKey, object: CometChat.CallbackListener<User>() {
                        override fun onSuccess(user: User) {
                            Log.d("createUser", user.toString())
                        }

                        override fun onError(e: CometChatException) {
                            Log.e("createUser", e.message)
                        }
                    })

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(register_username.text.toString())
                        .build()
                    firebaseUser?.updateProfile(profileUpdates)
                    val data = hashMapOf(
                        "name" to register_username.text.toString(),
                        "alert" to false,
                        "token" to  null
                    )
                    database.collection("users").document(firebaseUser!!.uid).set(data)
                    firebaseUser?.sendEmailVerification()
                        ?.addOnCompleteListener{task ->
                            if (task.isSuccessful){
                                startActivity(Intent(this,LoginActivity::class.java))
                                finish()
                            }
                        }
                } else {
                    //if sign in fails, display a message to the user.
                    Toast.makeText(baseContext, getString(R.string.registration_fail),
                        Toast.LENGTH_SHORT).show()
                }
            }

    }
}
