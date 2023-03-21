package com.boopro.btracker.data.model

data class UserModel(
    var firstname: String = "",
    var lastname: String = "",
    var username: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var imageURL: String = "",
    var friendList: MutableList<String> = arrayListOf(),
    var points: Int = 0
) {
    fun getFullName(): String {
        return "$firstname $lastname"
    }

    fun getPointsToString(): String {
        return points.toString() + "pts"
    }
}