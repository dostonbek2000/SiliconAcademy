import com.example.siliconacademy.models.Course

class Group {

    var id: Int? = null
    var groupPosition: Int? = null
    var groupTitle: String? = null
    var groupTeacherName: String? = null
    var groupTime: String? = null
    var groupDay: String? = null
    var teacherId: Teacher? = null
    var courseId: Course? = null

    constructor(
        id: Int?,
        groupPosition: Int?,
        groupTitle: String?,
        groupTeacherName: String?,
        groupTime: String?,
        groupDay: String?,
        teacherId: Teacher?,
        courseId: Course
    ) {
        this.id = id
        this.groupPosition = groupPosition
        this.groupTitle = groupTitle
        this.groupTeacherName = groupTeacherName
        this.groupTime = groupTime
        this.groupDay = groupDay
        this.teacherId = teacherId
        this.courseId = courseId
    }

    constructor(
        groupPosition: Int?,
        groupTitle: String?,
        groupTeacherName: String?,
        groupTime: String?,
        groupDay: String?,
        teacherId: Teacher?,
        courseId: Course
    ) {
        this.groupPosition = groupPosition
        this.groupTitle = groupTitle
        this.groupTeacherName = groupTeacherName
        this.groupTime = groupTime
        this.groupDay = groupDay
        this.teacherId = teacherId
        this.courseId = courseId
    }

    constructor()
}