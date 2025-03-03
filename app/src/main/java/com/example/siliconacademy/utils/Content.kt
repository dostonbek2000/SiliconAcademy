package com.example.siliconacademy.utils

import com.example.siliconacademy.models.Payment

object Content {

    const val DB_NAME: String = "codial.db"
    const val DB_VERSION: Int = 4

    const val COURSE_TABLE: String = "courses"
    const val COURSE_ID: String = "id"
    const val COURSE_TITLE: String = "title"
    const val COURSE_DESCRIPTION: String = "description"

    const val TEACHERS_TABLE: String = "teachers"
    const val TEACHERS_ID: String = "id"
    const val TEACHERS_NAME: String = "name"
    const val TEACHERS_SURNAME: String = "surname"
    const val TEACHERS_FATHER: String = "fatherName"
    const val TEACHERS_COURSE_ID: String = "courseId"

    const val GROUP_TABLE: String = "groups"
    const val GROUP_ID: String = "id"
    const val GROUP_POSITION: String = "groupPosition"
    const val GROUP_TITLE: String = "title"
    const val GROUP_TEACHER_NAME: String = "teacherName"
    const val GROUP_TIME: String = "time"
    const val GROUP_DAY: String = "day"
    const val GROUP_TEACHER_ID: String = "teacherId"
    const val GROUP_COURSE_ID: String = "courseId"

    const val STUDENT_TABLE: String = "students"
    const val STUDENT_ID: String = "id"
    const val STUDENT_NAME: String = "name"
    const val STUDENT_SURNAME: String = "surname"
    const val STUDENT_FATHER_NAME: String = "fatherName"
    const val STUDENT_GROUP_ID: String = "groupId"

    const val PAYMENT_TABLE:String="payment"
    const val ID:String="id"
    const val PAYMENT_ID:String="Pid"
    const val PAYMENT_AMOUNT:String="amount"
    const val PAYMENT_MONTH:String="month"

}