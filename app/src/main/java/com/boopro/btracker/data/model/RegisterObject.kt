package com.boopro.btracker.data.model

data class RegisterObject(
    var username: String = "",
    var password: String = "",
    var repeatPassword: String = "",
    var email: String = "",
    var firstname: String = "",
    var lastname: String = "",
    var phoneNumber: String = "",
    var friendList: List<String> = listOf(),
    var points: Int = 0,
    var imageURL: String = "https://firebasestorage.googleapis.com/v0/b/b-tracker-ed5ca.appspot.com/o/profilePictures%2Fdefault.jpg?alt=media&token=2b83a354-21c3-4aef-9c06-5b7860fc7242"
)

