package com.example.siliconacademy.models

import Teacher
import android.net.Uri
import java.io.Serializable

data class Results(
    val id: Int? = null,
    var resultPosition:Int?=null,
    var name: String,
    var age:String,
    var testType:String,
    var teacherName:String,
    var subject: String

) : Serializable
