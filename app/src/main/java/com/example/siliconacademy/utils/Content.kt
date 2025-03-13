package com.example.siliconacademy.utils


object Content {

    const val DB_NAME: String = "codial.db"
    const val DB_VERSION: Int = 12

    const val TEACHERS_TABLE: String = "teachers"
    const val TEACHERS_ID: String = "id"
    const val TEACHERS_NAME: String = "name"
    const val TEACHERS_DESCRIPTION: String = "description"

    const val COURSE_TABLE:String="course"
     const val COURSE_ID:String="id"
    const val COURSE_TITLE:String="title"
    const val COURSE_DESCRIPTION:String="description"

    const val GROUP_TABLE: String = "groups"
    const val GROUP_ID: String = "id"
    const val GROUP_POSITION: String = "groupPosition"
    const val GROUP_TITLE: String = "title"
    const val GROUP_TIME: String = "time"
    const val GROUP_DAY: String = "day"
    const val GROUP_COURSE_ID: String = "courseId"

    const val STUDENT_TABLE: String = "students"
    const val STUDENT_ID: String = "idd"
    const val STUDENT_NAME: String = "name"
    const val STUDENT_SURNAME: String = "surname"
    const val STUDENT_FATHER_NAME: String = "fatherName"
    const val STUDENT_GROUP_ID: String = "groupId"

    const val PAYMENT_TABLE:String="payment"

    const val PAYMENT_ID:String="id"
    const val PAYMENT_AMOUNT:String="amount"
    const val PAYMENT_MONTH:String="month"

    const val RESULT_TABLE:String="result"
    const val RESULT_ID:String="id"
    const val RESULT_S_NAME:String="sname"
    const val RESULT_AGE:String="age"
    const val RESULT_TYPE:String="type"
    const val RESULT_T_NAME:String="tname"
    const val RESULT_SUBJECT:String="subject"
    const val RESULT_POSITION:String="resultPosition"



}