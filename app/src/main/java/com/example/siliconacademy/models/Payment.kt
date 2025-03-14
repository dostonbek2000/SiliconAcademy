package com.example.siliconacademy.models

import java.io.Serializable

data class Payment(val id: Int? = null,

                   var fullName:String?=null,

                   var amount: String,
                   var month: String):Serializable
