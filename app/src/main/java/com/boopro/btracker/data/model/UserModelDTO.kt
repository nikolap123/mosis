package com.boopro.btracker.data.model

data class UserModelDTO(
    var firstname: String = "",
    var lastname: String = "",
    var username: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var imageURL: String = "",
    var friendList: List<String> = emptyList(),
    var points: Int = 0
) {
    constructor(user: UserModel) : this(
        user.firstname,
        user.lastname,
        user.username,
        user.email,
        user.phoneNumber,
        user.imageURL,
        user.friendList,
        user.points
    )
}
