package com.example.sosapplication

class ToDo (
    val id: Int,
    var name: String,
    var content: String
){
    override fun toString() = name
}