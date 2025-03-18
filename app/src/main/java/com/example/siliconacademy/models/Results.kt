package com.example.siliconacademy.models

import android.net.Uri
import java.io.Serializable

data class Results(
    val id: Int? = null,
    var resultPosition:Int?=null,
    var name: String,
    var age:String,
    var testType:String,
    var teacherName:String,
    var subject: String,
    var imageUri: String? = null,
    var fileUri: String? = null

) : Serializable
