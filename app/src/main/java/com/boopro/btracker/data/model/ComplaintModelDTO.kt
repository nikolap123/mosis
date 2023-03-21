package com.boopro.btracker.data.model

data class ComplaintModelDTO(
    var userId: String = "",
    var title: String = "",
    var content: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var likes: List<String> = emptyList()
) {
    constructor(complaint: ComplaintModel) : this(
        complaint.userId,
        complaint.title,
        complaint.content,
        complaint.latitude,
        complaint.longitude,
        complaint.likes
    )
}
