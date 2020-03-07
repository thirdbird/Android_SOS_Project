package com.example.sosapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_register
import kotlinx.android.synthetic.main.activity_login.login_password
import kotlinx.android.synthetic.main.activity_login.login_email


class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

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
