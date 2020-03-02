package com.example.sosapplication

import androidx.annotation.NonNull
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class User(
    var username: String,
    var email: String,
    var password: String
)

private val database = FirebaseDatabase.getInstance().getReference()
private val users = mutableListOf<String>()

fun writeNewUser(userId: String, name: String, email: String, password: String) {
    val user = User(name, email, password)
    database.child("Users").child(userId).setValue(user)
}


fun readAllUsers(): MutableList<String> {
    users.clear()
    database.child("Users").addValueEventListener(object : ValueEventListener {
        override fun onCancelled(snapshot: DatabaseError) {
            println("database Error")
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            val children = snapshot!!.children

            children.forEach {
                println(it.child("username").toString())
                users.add(it.child("username").getValue().toString())
            }
        }
    })
    if (users.count() != 0) {
        println("THERE ARE " + users.count() + " MOTHERFUCKING STRINGS IN HERE")
        return users
    } else {
        println("FAILED TO ADD VALUES")
        return users
    }

}



