package com.example.siliconacademy.models

import android.net.Uri

data class Results(
    val id: Int? = null,
    val resultPosition:Int?=null,
    val name: String,
    val age:String,
    val testType:String,
    val teacherName:String,
    val subject: String,

)
