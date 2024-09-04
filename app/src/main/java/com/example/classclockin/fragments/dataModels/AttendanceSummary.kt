package com.example.classclockin.fragments.dataModels

data class AttendanceSummary(
    val date: String? = "",
    val totalPresent: String = "0",
    val totalAbsent: String = "0",
)
