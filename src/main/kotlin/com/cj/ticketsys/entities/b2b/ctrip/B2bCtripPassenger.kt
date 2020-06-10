package com.cj.ticketsys.entities.b2b.ctrip

import com.cj.ticketsys.entities.b2b.IB2bOta
import java.util.*

class B2bCtripPassenger : IB2bOta {
    var id:Int = 0
    override var otaId:String = ""
    var itemId:String = ""
    var ageType: String? = null
    var birthDate: Date? = null
    var birthPlace: String? = null
    var cardIssueCountry: String? = null
    var cardIssueDate: Date? = null
    var cardIssuePlace: String? = null
    var cardNo: String? = null
    var cardType: String? = null
    var cardValidDate: Date? = null
    var firstName: String? = null
    var gender: String? = null
    var height: Double? = null
    var intlCode: String? = null
    var lastName: String? = null
    var mobile: String? = null
    var myopiaDegreeL: Double? = null
    var myopiaDegreeR: Double? = null
    var name: String? = null
    var nationalityCode: String? = null
    var nationalityName: String? = null
    var shoeSize: Double? = null
    var weight: Double? = null
}