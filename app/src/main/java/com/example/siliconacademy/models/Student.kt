import Group

class Student {

    var id: Int? = null
    var name: String? = null
    var surname: String? = null
    var fatherName: String? = null
    var groupId: Group? = null
    var accountBalance: Double? = 0.0

    constructor(
        id: Int?, name: String?, surname: String?, fatherName: String?, groupId: Group,accountBalance:Double
    ) {
        this.id = id
        this.name = name
        this.surname = surname
        this.fatherName = fatherName
        this.groupId = groupId
        this.accountBalance=accountBalance
    }

    constructor(name: String?, surname: String?, fatherName: String?, groupId: Group?,accountBalance: Double) {
        this.name = name
        this.surname = surname
        this.fatherName = fatherName
        this.groupId = groupId
        this.accountBalance=accountBalance
    }

    constructor()
}
