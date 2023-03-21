package com.boopro.btracker.helper

import com.boopro.btracker.data.model.ComplaintModel
import com.boopro.btracker.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

//Constants variables for project
object Consts {

    //val currentUserId = "1"
    //lateinit var currentUser: UserModel
    //val currentUser = UserModel("Pera", "Peric", "pera", "pera@pera.com", "061346591", "https://firebasestorage.googleapis.com/v0/b/b-tracker-ed5ca.appspot.com/o/profilePictures%2F1.jpg?alt=media&token=6261c84e-5c16-4a36-b99a-2b9f04f96e0e", listOf("2", "3", "4", "6", "JMZZrZIt5jf73v9IhQFVvMulCcu2"), 12 )

    lateinit var currentUser: UserModel
    var currentUserId: String? = null

//    lateinit var currentUserFriends: Map<String, UserModel> HERE
    var currentUserFriends: Map<String, UserModel> = emptyMap<String, UserModel>()
    var currentUserComplaints: MutableList<ComplaintModel> = mutableListOf<ComplaintModel>()
    lateinit var friendsComplaints: Map<String, ComplaintModel>

    val storage = Firebase.storage.reference
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val database = Firebase.database.reference
    val userCollection = database.child("users")
    var currentUserRef = auth.currentUser?.uid?.let { Consts.database.child("users").child(it) }
    val complaintCollection = database.child("complaints")
    val currentUserComplaintsRef = complaintCollection.orderByChild("userId").equalTo("HZZwlnMwCKQ51VGwYaww9Qns7Ql1")


    lateinit var listener: ValueEventListener
    const val USER = "user"
    const val CHECK = "checkbox"
    const val USER_COMPLAINTS = "user_complaints"

}