import com.example.siliconacademy.models.Teacher

class Group {

    var id: Int? = null
    var groupPosition: Int? = null
    var groupTitle: String? = null
    var groupSubject: String? = null
    var groupTime: String? = null
    var groupDay: String? = null
    var courseId: Teacher? = null
    var fee: String? = null


    constructor(
        id: Int?,
        groupPosition: Int?,
        groupTitle: String?,
        groupSubject: String?,
        groupTime: String?,
        groupDay: String?,

        courseId: Teacher,
        fee: String?
    ) {
        this.id = id
        this.groupPosition = groupPosition
        this.groupTitle = groupTitle
        this.groupSubject = groupSubject
        this.groupTime = groupTime
        this.groupDay = groupDay

        this.courseId = courseId
        this.fee = fee
    }

    constructor(
        groupPosition: Int?,
        groupTitle: String?,
        groupSubject: String?,

        groupTime: String?,
        groupDay: String?,

        courseId: Teacher,
        fee: String?
    ) {
        this.groupPosition = groupPosition
        this.groupTitle = groupTitle
        this.groupSubject=groupSubject
        this.groupTime = groupTime
        this.groupDay = groupDay
        this.courseId = courseId
        this.fee = fee
    }

    constructor()
}