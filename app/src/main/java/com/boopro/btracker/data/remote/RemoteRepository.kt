package com.boopro.btracker.data.remote

import android.content.Context
import com.boopro.btracker.data.model.ComplaintModel
import com.boopro.btracker.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import android.net.Uri
import com.boopro.btracker.data.model.RegisterObject

// Class through which data stored on Firebase will be accessed
object RemoteRepository {

    //Firebase
    suspend fun login(email: String, password: String): Flow<UserModel> {
        return FirebaseWrapper.login(email, password)
    }

    suspend fun registerUser(email: String, password: String, username: String, firstname: String, lastname: String, phone: String, imageURI: Uri): Flow<RegisterObject> {
        return FirebaseWrapper.registerUser(email, password, username, firstname, lastname, phone, imageURI)
    }

    suspend fun getAllUsers(): Flow<List<UserModel>> {
        return FirebaseWrapper.getAllUsers()
    }

    suspend fun getUser(): Flow<UserModel> {
        return FirebaseWrapper.getUser()
    }

    suspend fun getUserComplaints(): Flow<List<ComplaintModel>> {
        return FirebaseWrapper.getUserComplaints()
    }

    suspend fun getUserFriends(): Flow<Map<String, UserModel>> {
        return FirebaseWrapper.getUserFriends()
    }

//    suspend fun updateUser(userId: String, user: UserModel) {
//        FirebaseWrapper.updateUser(userId, user)
//    }

    suspend fun updateComplaintAndUser(complaintId: String, complaint: ComplaintModel, user: UserModel) {
        FirebaseWrapper.updateComplaintAndUser(complaintId, complaint, user)
    }

    fun addComplaint(complaint: ComplaintModel) {
        FirebaseWrapper.addComplaint(complaint)
    }

    suspend fun getFriendsComplaints(): Flow<Map<String, ComplaintModel>> {
        return FirebaseWrapper.getFriendsComplaints()
    }

    suspend fun updateUserWithPicture(user: UserModel, imageURI: Uri?) {
        FirebaseWrapper.updateUserWithPicture(user, imageURI)
    }
    fun addFriend(friendId: String) {
        FirebaseWrapper.addFriend(friendId)
    }

    //Nearby Connection
    fun startAdvertising(context: Context) {
        NearbyConnectionWrapper.startAdvertising(context)
    }

    fun startDiscovery(context: Context) {
        NearbyConnectionWrapper.startDiscovery(context)
    }

    fun startAdvertisingDiscovery(context: Context, advertisingDiscovery: Boolean) {
        NearbyConnectionWrapper.startAdvertisingDiscovery(context, advertisingDiscovery)
    }

}