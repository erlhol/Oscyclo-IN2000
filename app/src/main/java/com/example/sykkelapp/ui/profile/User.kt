package com.example.sykkelapp.ui.profile

class User {

    private var firstName: String = ""
    private var lastName: String = ""
    private var fullName: String = ""
    private var email: String = ""
//    private var image: String = ""
//    private var uid: String = ""

    constructor()

    constructor(firstName: String, lastName: String, email: String) {
        this.firstName = firstName
        this.lastName = lastName
        this.fullName = "$firstName  $lastName"
        this.email = email
//        this.image = image
//        this.uid = uid
    }

    fun getFullName(): String {
        return  fullName
    }

    fun getFirstName(): String {
        return  firstName
    }

    fun getLastName(): String {
        return  lastName
    }

    fun getEmail(): String {
        return  email
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