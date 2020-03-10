package com.example.sosapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
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
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(register_username.text.toString())
                        .build()
                    user?.updateProfile(profileUpdates)
                    val data = hashMapOf(
                        "name" to register_username.text.toString(),
                        "alert" to false,
                        "token" to  null
                    )
                    database.collection("users").document(user!!.uid).set(data)
                    user?.sendEmailVerification()
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
