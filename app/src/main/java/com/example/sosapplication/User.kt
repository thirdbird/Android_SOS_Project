package com.example.sosapplication

import androidx.annotation.NonNull
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

data class User(
    var Username: String,
    var Email: String,
    var Password: String,
    var Alert: Boolean
)

private val database = FirebaseFirestore.getInstance()
private val users = mutableListOf<String>()

fun writeNewUser(Username: String, Email: String, Password: String, Alert: Boolean) {
    val user = User(Username, Email, Password, Alert)
    database.collection("Users").add(user)

}


fun readAllUsers(): MutableList<String> {
    if (users.count() != 0)
        //users.clear()
        database.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for( document in result){
                    println(document.get("username"))
                }
            }
    //getAllUsers()

    if (users.count() != 0) {
        println("THERE ARE " + users.count() + " MOTHERFUCKING STRINGS IN HERE")
        return users
    } else {
        println("FAILED TO ADD VALUES")
        return users
    }

}

fun getAllUsers(){
    database.collection("Users")
        .get()
        .addOnSuccessListener { Users ->
            for( Username in Users){
                println("ADDED LIST")
            }
        }

}




