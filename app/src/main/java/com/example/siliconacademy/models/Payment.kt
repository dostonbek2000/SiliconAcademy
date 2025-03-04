package com.example.siliconacademy.models

data class Payment(val id: Int? = null,
                   val studentId: Int,
                   val amount: String,
                   val month: String)
