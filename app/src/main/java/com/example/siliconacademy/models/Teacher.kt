

import com.example.siliconacademy.models.Course

class Teacher {

    var id: Int? = null
    var name: String? = null
    var surname: String? = null
    var fatherName: String? = null
    var courseId: Course? = null

    constructor(id: Int?, name: String?, surname: String?, fatherName: String?, courseId: Course?) {
        this.id = id
        this.name = name
        this.surname = surname
        this.fatherName = fatherName
        this.courseId = courseId
    }

    constructor(name: String?, surname: String?, fatherName: String?, courseId: Course?) {
        this.name = name
        this.surname = surname
        this.fatherName = fatherName
        this.courseId = courseId
    }

    constructor()
}