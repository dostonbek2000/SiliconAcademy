import Group

class Student {

    var id: Int? = null
    var name: String? = null
    var surname: String? = null
    var fatherName: String? = null
    var groupId: Group? = null
    var paymentStatus: String = "to'lov qilinmagan"

    constructor(
        id: Int?, name: String?, surname: String?, fatherName: String?, groupId: Group,paymentStatus:String
    ) {
        this.id = id
        this.name = name
        this.surname = surname
        this.fatherName = fatherName
        this.groupId = groupId
       this.paymentStatus=paymentStatus
    }

    constructor(name: String?, surname: String?, fatherName: String?, groupId: Group?,status:String) {
        this.name = name
        this.surname = surname
        this.fatherName = fatherName
        this.groupId = groupId
        this.paymentStatus=status
    }

    constructor()
}
