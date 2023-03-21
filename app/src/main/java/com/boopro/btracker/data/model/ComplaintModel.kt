package com.boopro.btracker.data.model

import com.boopro.btracker.helper.Consts

data class ComplaintModel(
    var userId: String = "",
    var title: String = "",
    var content: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var likes: MutableList<String> = arrayListOf()
) {
    fun isLiked(): Boolean {
        return likes.contains(Consts.currentUserId)
    }
}