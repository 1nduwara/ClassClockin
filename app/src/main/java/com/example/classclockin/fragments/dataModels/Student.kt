package com.example.classclockin.fragments.dataModels

data class Student(
    val studentId: String = "",
    val studentName: String = "",
    val studentPhoto: String = "",
    var studentAttendance: Float = 0.0f
)
