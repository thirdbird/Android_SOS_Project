package com.example.sosapplication

import androidx.annotation.NonNull
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

data class User(
    var Username: String,
    var Email: String,
    var Password: String,
    var Alert: Boolean
)

private val database = FirebaseFirestore.getInstance()

fun getAllUsers(): Task<QuerySnapshot> {
    return database.collection("users")
        .get()
}




