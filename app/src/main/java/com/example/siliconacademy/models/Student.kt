import Group
import java.io.Serializable

class Student:Serializable {

    var id: Int? = null
    var name: String? = null
    var surname: String? = null
    var fatherName: String? = null
    var age:String?=null
    var groupId: Group? = null
    var date: String? = null
    var accountBalance: Double? = 0.0



    constructor(
        id: Int?, name: String?, surname: String?, fatherName: String?,age:String?, groupId: Group, date: String,accountBalance:Double?
    ) {
        this.id = id
        this.name = name
        this.surname = surname
        this.fatherName = fatherName

        this.age=age
        this.groupId = groupId
        this.date=date
        this.accountBalance=accountBalance

    }

    constructor(name: String?, surname: String?, fatherName: String?,age:String?, groupId: Group?,date: String?,accountBalance: Double,) {
        this.name = name
        this.surname = surname
        this.fatherName = fatherName
        this.age=age
        this.groupId = groupId
        this.date=date
        this.accountBalance=accountBalance

    }

    constructor()
}
