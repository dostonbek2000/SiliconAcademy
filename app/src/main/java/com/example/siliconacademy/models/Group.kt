import com.example.siliconacademy.models.Teacher

class Group {

    var id: Int? = null
    var groupPosition: Int? = null
    var groupTitle: String? = null
    var groupTime: String? = null
    var groupDay: String? = null
    var courseId: Teacher? = null


    constructor(
        id: Int?,
        groupPosition: Int?,
        groupTitle: String?,

        groupTime: String?,
        groupDay: String?,

        courseId: Teacher
    ) {
        this.id = id
        this.groupPosition = groupPosition
        this.groupTitle = groupTitle

        this.groupTime = groupTime
        this.groupDay = groupDay

        this.courseId = courseId
    }

    constructor(
        groupPosition: Int?,
        groupTitle: String?,

        groupTime: String?,
        groupDay: String?,

        courseId: Teacher
    ) {
        this.groupPosition = groupPosition
        this.groupTitle = groupTitle

        this.groupTime = groupTime
        this.groupDay = groupDay

        this.courseId = courseId
    }

    constructor()
}