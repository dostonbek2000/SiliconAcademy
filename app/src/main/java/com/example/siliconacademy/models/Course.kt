package com.example.siliconacademy.models

import java.io.Serializable

class Course : Serializable {
    var id: Int? = null
    var title: String? = null
    var description: String? = null
    constructor(id: Int?, title: String?, desc: String?) {
        this.id = id
        this.title = title
        this.description= desc
    }

    constructor(title: String?, desc: String?) {
        this.title = title
        this.description = desc
    }

    constructor()
}


