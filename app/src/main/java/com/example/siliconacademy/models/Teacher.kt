package com.example.siliconacademy.models

import java.io.Serializable

class Teacher : Serializable {

    var id: Int? = null
    var title: String? = null
    var desc: String? = null
    var age: String? = null
    var subject: String? = null
    var toifa: String? = null
    var cerA: String? = null
    var gradeA: String? = null
    var cerB: String? = null
    var gradeB: String? = null

    constructor(
        id: Int?,
        title: String?,
        desc: String?,
        age: String?,
        subject: String?,
        toifa: String?,
        cerA: String?,
        gradeA: String?,
        cerB: String?,
        gradeB: String?
    ) {
        this.id = id
        this.title = title
        this.desc = desc
        this.age = age
        this.subject = subject
        this.toifa = toifa
        this.cerA = cerA
        this.gradeA = gradeA
        this.cerB = cerB
        this.gradeB = gradeB
    }

    constructor(
        title: String?,
        desc: String?,
        age: String?,
        subject: String?,
        toifa: String?,
        cerA: String?,
        gradeA: String?,
        cerB: String?,
        gradeB: String?
    ) {
        this.title = title
        this.desc = desc
        this.age = age
        this.subject = subject
        this.toifa = toifa
        this.cerA = cerA
        this.gradeA = gradeA
        this.cerB = cerB
        this.gradeB = gradeB
    }

    constructor()
}