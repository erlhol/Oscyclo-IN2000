package com.example.sykkelapp.ui.profile

class User {

    private var firstName: String = ""
    private var lastName: String = ""
    private var fullName: String = ""
//    private var image: String = ""
//    private var uid: String = ""

    constructor()

    constructor(firstName: String, lastName: String) {
        this.firstName = firstName
        this.lastName = lastName
        this.fullName = "$firstName  $lastName"
//        this.image = image
//        this.uid = uid
    }

    fun getFullName(): String {
        return  fullName
    }

    fun setFullName(fullName: String) {
        this.fullName = fullName
    }

    fun getFirstName(): String {
        return  firstName
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun getLastName(): String {
        return  lastName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

//    fun getImage(): String {
//        return  image
//    }
//
//    fun setImage(image: String) {
//        this.image = image
//    }

//    fun getUID(): String {
//        return  uid
//    }
//
//    fun setUID(uid: String) {
//        this.uid = uid
//    }
}