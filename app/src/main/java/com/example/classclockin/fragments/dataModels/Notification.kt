package com.example.classclockin.fragments.dataModels

data class Notification(
    val id: String,
    val message: String,
    val timestamp: Long,
    val marked: Boolean = false
)
