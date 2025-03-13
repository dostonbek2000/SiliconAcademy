package com.example.siliconacademy.models

import java.io.Serializable

class Teacher : Serializable {

    var id: Int? = null
    var title: String? = null
    var desc: String? = null

    constructor(id: Int?, title: String?, desc: String?) {
        this.id = id
        this.title = title
        this.desc = desc
    }

    constructor(title: String?, desc: String?) {
        this.title = title
        this.desc = desc
    }

    constructor()
}