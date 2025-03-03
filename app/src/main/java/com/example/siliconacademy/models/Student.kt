import Group

class Student {

    var id: Int? = null
    var name: String? = null
    var surname: String? = null
    var fatherName: String? = null
    var groupId: Group? = null
    var latestPaymentAmount: String? = null  // ✅ Stores latest payment amount
    var latestPaymentMonth: String? = null // ✅ Stores latest payment month

    constructor(
        id: Int?, name: String?, surname: String?, fatherName: String?, groupId: Group,
        latestPaymentAmount: String? = null, latestPaymentMonth: String? = null
    ) {
        this.id = id
        this.name = name
        this.surname = surname
        this.fatherName = fatherName
        this.groupId = groupId
        this.latestPaymentAmount = latestPaymentAmount
        this.latestPaymentMonth = latestPaymentMonth
    }

    constructor(name: String?, surname: String?, fatherName: String?, groupId: Group?) {
        this.name = name
        this.surname = surname
        this.fatherName = fatherName
        this.groupId = groupId
    }

    constructor()
}
