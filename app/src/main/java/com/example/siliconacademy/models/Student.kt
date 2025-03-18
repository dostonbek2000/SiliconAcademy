import Group
import java.io.Serializable


data class Student(
    val id: Int? = 0,
    var name: String? = null,
    var surname: String? = null,
    var fatherName: String? = null,
    val age: String? = null,
    val groupId: Group? = null,
    val date: String? = null,
    var accountBalance: Double? = 0.0,
    val removedStatus: String? = "not removed"
) : Serializable