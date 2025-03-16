package com.example.siliconacademy.models

import java.io.Serializable

class Teacher : Serializable {

    var id: Int? = null
    var title: String? = null
    var desc: String? = null
    var age:String?=null
    var subject:String?=null

    constructor(id: Int?, title: String?, desc: String?,age:String?,subject:String?) {
        this.id = id
        this.title = title
        this.desc = desc
        this.age=age
        this.subject=subject
    }

    constructor(title: String?, desc: String?,age: String?,subject: String?) {
        this.title = title
        this.desc = desc
        this.age=age
        this.subject=subject
    }

    constructor()
}