package com.example.siliconacademy.models

data class Results(
    val id: Int? = null,
    val resultPosition:Int?=null,
    val name: String,
    val age:String,
    val testType:String,
    val teacherName:String,
    val subject: String,
    val image: String,
    var resultId: Test? = null
)
