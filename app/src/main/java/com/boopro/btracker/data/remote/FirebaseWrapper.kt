package com.boopro.btracker.data.remote

import android.net.Uri
import android.util.Log
import com.boopro.btracker.data.Repository
import com.boopro.btracker.data.model.*
import com.boopro.btracker.helper.Consts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*

// Class which will handle everything related to Firebase
object FirebaseWrapper {

    suspend fun login(email: String, password: String): Flow<UserModel> = callbackFlow {
        val login = Consts.auth.signInWithEmailAndPassword(email, password).await()
        val uid = login.user?.uid

        Consts.currentUserId = uid;
        var user = UserModel()

        if (uid != null) {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userData = snapshot.child(uid)
                        // val userData = snapshot.child("1") // za demo i proveru
                        val u = userData.getValue(UserModel::class.java)

                        if (u != null) {
                            user = u
                        }
                    }
                    trySend(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }

            Consts.userCollection.addValueEventListener(listener)
            awaitClose { Consts.userCollection.removeEventListener(listener) }
        } else {
            trySend(user)
        }
    }

    suspend fun registerUser(email: String, password: String, username: String, firstname: String, lastname: String, phone: String, imageURI: Uri): Flow<RegisterObject> = flow {
        val regUser = Consts.auth.createUserWithEmailAndPassword(email, password).await()
        val userID = regUser.user?.uid

        val storageReference = Consts.storage.child("profilePictures/$userID")
        storageReference.putFile(imageURI).await()

        if (userID != null) {
            val u = RegisterObject()

            u.firstname = firstname
            u.lastname = lastname
            u.username = username
            u.phoneNumber = phone
            u.email = email
            storageReference.downloadUrl.addOnCompleteListener { u.imageURL = it.result.toString() }.addOnFailureListener { u.imageURL = "https://firebasestorage.googleapis.com/v0/b/b-tracker-ed5ca.appspot.com/o/profilePictures%2Fdefault.jpg?alt=media&token=2b83a354-21c3-4aef-9c06-5b7860fc7242" }.await()

            val userRef = Consts.userCollection.child(userID)

            val user: MutableMap<String, Any> = HashMap()
            user["email"] = u.email
            user["username"] = u.username
            user["firstname"] = u.firstname
            user["lastname"] = u.lastname
            user["phoneNumber"] = u.phoneNumber
            user["points"] = 0
            user["friendList"] = emptyList<String>()
            user["imageURL"] = u.imageURL
            userRef.setValue(user)

            emit(u)
        }
    }

    suspend fun getAllUsers(): Flow<List<UserModel>> = callbackFlow {
        val rankList: MutableList<UserModel> = mutableListOf()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    rankList.clear()

                    for (data in snapshot.children) {
                        val user = data.getValue(UserModel::class.java)

                        if (user != null) {
                            rankList.add(user)
                        }
                    }
                }

                trySend(rankList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        Consts.userCollection.addValueEventListener(listener)
        awaitClose { Consts.userCollection.removeEventListener(listener) }
    }

    suspend fun getUser(): Flow<UserModel> = callbackFlow {
        var user = UserModel();

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = Consts.auth.currentUser?.uid?.let { snapshot.child(it) }
                    val u = userData?.getValue(UserModel::class.java)

                    if (u != null) {
                        user = u
                    }
                }

                trySend(user)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        Consts.userCollection.addValueEventListener(listener)
        awaitClose { Consts.userCollection.removeEventListener(listener) }
    }

    suspend fun getUserComplaints(): Flow<List<ComplaintModel>> = callbackFlow {
        val complaintList = mutableListOf<ComplaintModel>()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    complaintList.clear()

                    for (data in snapshot.children) {
                        val complaint = data.getValue(ComplaintModel::class.java)

                        if (complaint != null ) {
                            Log.i("ComplaintModel", complaint.toString())
                            complaintList.add(complaint)
                        }
                    }
                }

                trySend(complaintList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }


        var id = FirebaseAuth.getInstance().currentUser?.uid;

        Firebase.database.reference.child("complaints").orderByChild("userId").equalTo(id).addValueEventListener(listener);
        awaitClose { Firebase.database.reference.child("complaints").orderByChild("userId").equalTo(id).removeEventListener(listener) }

    }


    suspend fun getUserFriends(): Flow<Map<String, UserModel>> = callbackFlow {
        val friendsList: MutableMap<String, UserModel> = hashMapOf()
        val friendsId = Consts.currentUser.friendList

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    friendsList.clear()

                    for (data in snapshot.children) {
                        if (friendsId.contains(data.key)) {
                            val friend = data.getValue(UserModel::class.java)

                            if (friend != null) {
                                data.key?.let { friendsList.put(it, friend) }
                            }
                        }
                    }
                }

                trySend(friendsList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }

        }

        Firebase.database.reference.child("users").addValueEventListener(listener);
        awaitClose { Firebase.database.reference.child("users").removeEventListener(listener) }

//        Consts.userCollection.addValueEventListener(listener)
//        awaitClose { Consts.userCollection.removeEventListener(listener) }

//        trySend(friendsList)
    }

//    suspend fun updateUser(userId: String, user: UserModel) {
//        Consts.userCollection.child(userId).setValue(UserModelDTO(user)).await()
//    }

    suspend fun updateComplaintAndUser(complaintId: String, complaint: ComplaintModel, user: UserModel) {
        //updateUser(complaint.userId, user)
        Consts.userCollection.child(complaint.userId).setValue(UserModelDTO(user)).await()
        Consts.complaintCollection.child(complaintId).setValue(ComplaintModelDTO(complaint)).await()
    }

    fun addComplaint(complaint: ComplaintModel) {
        Firebase.database.reference.child("complaints").child(UUID.randomUUID().toString()).setValue(complaint)
//        Consts.complaintCollection.child(UUID.randomUUID().toString()).setValue(complaint)
    }

    suspend fun getFriendsComplaints(): Flow<Map<String, ComplaintModel>> = callbackFlow {
        val complaintList: MutableMap<String, ComplaintModel> = hashMapOf()
        val friendsId = Consts.currentUser.friendList

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    complaintList.clear()

                    for (data in snapshot.children) {
                        if (friendsId.contains(data.child("userId").value)) {
                            val complaint = data.getValue(ComplaintModel::class.java)

                            if (complaint != null) {
                                data.key?.let { complaintList.put(it, complaint) }
                            }
                        }
                    }
                }

                trySend(complaintList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        Consts.complaintCollection.addValueEventListener(listener)
        awaitClose { Consts.complaintCollection.removeEventListener(listener) }

//        trySend(complaintList)
    }

//    suspend fun updateUser(user: UserModel) {
//        Consts.currentUserRef.setValue(UserModelDTO(user)).await()
//    }

    suspend fun updateUserWithPicture(user: UserModel, imageURI: Uri?) {
//        if (imageURI!=null) {
//            val storageReference = Consts.storage.child("profilePictures/${Consts.currentUserId}")
//            storageReference.delete()
//            storageReference.putFile(imageURI).await()
//
//            storageReference.downloadUrl.addOnCompleteListener { user.imageURL = it.result.toString() }.addOnFailureListener { }.await()
//        }

        //updateUser(Consts.currentUserId, user)
        Consts.auth.currentUser?.uid?.let { Consts.userCollection.child(it).setValue(UserModelDTO(user)).await() }
    }

    fun addFriend(friendId: String) {
        val user = UserModelDTO()

        user.firstname = Consts.currentUser.firstname
        user.lastname = Consts.currentUser.lastname
        user.username = Consts.currentUser.username
        user.email = Consts.currentUser.email
        user.phoneNumber = Consts.currentUser.phoneNumber
        user.imageURL = Consts.currentUser.imageURL
        user.points = Consts.currentUser.points

//        Consts.currentUser.friendList.add(friendId);

        val newFriendList = Consts.currentUser.friendList as MutableList<String>
        newFriendList.add(friendId)

        user.friendList = newFriendList

        println("friendList" + newFriendList)
        Consts.auth.currentUser?.uid?.let { Consts.userCollection.child(it).setValue(user) }

//        Consts.currentUserRef?.setValue(user)
    }
}