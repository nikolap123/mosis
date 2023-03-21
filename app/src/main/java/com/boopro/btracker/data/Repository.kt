package com.boopro.btracker.data

import android.net.Uri
import com.boopro.btracker.data.local.LocalRepository
import com.boopro.btracker.data.model.RegisterObject
import android.content.Context
import com.boopro.btracker.data.model.ComplaintModel
import com.boopro.btracker.data.model.UserModel
import com.boopro.btracker.data.remote.RemoteRepository
import kotlinx.coroutines.flow.Flow

// Class through which data will be accessed from the rest of the app, delegates to LocalRepository for locally stored data
// and RemoteRepository for fetching data from server/Firebase
object Repository {

    //Firebase
    suspend fun login(email: String, password: String): Flow<UserModel> {
        return RemoteRepository.login(email, password)
    }

    suspend fun registerUser(email: String, password: String, username: String, firstname: String, lastname: String, phone: String, imageURI: Uri): Flow<RegisterObject> {
        return RemoteRepository.registerUser(email, password, username, firstname, lastname, phone, imageURI)
    }

    suspend fun getAllUsers(): Flow<List<UserModel>> {
        return RemoteRepository.getAllUsers()
    }

    suspend fun getUser(): Flow<UserModel> {
        return RemoteRepository.getUser()
    }

    suspend fun getUserComplaints(): Flow<List<ComplaintModel>> {
        return RemoteRepository.getUserComplaints()
    }

    suspend fun getUserFriends(): Flow<Map<String, UserModel>> {
        return RemoteRepository.getUserFriends()
    }

//    suspend fun updateUser(userId: String, user: UserModel) {
//        RemoteRepository.updateUser(userId, user)
//    }

    suspend fun updateComplaintAndUser(complaintId: String, complaint: ComplaintModel, user: UserModel) {
        RemoteRepository.updateComplaintAndUser(complaintId, complaint, user)
    }

    fun addComplaint(complaint: ComplaintModel) {
        RemoteRepository.addComplaint(complaint)
    }

    suspend fun getFriendsComplaints(): Flow<Map<String, ComplaintModel>> {
        return RemoteRepository.getFriendsComplaints()
    }

    suspend fun updateUserWithPicture(user: UserModel, imageURI: Uri?) {
        RemoteRepository.updateUserWithPicture(user, imageURI)
    }

    //PrefUtility
    fun getUserInfo(context: Context): UserModel {
        return LocalRepository.getUserInfo(context)
    }

    fun saveUserInfo(context: Context, user: UserModel) {
        return LocalRepository.saveUserInfo(context, user)
    }

    fun getChecked(context: Context): Boolean {
        return LocalRepository.getChecked(context)
    }

    fun saveChecked(context: Context, check: Boolean) {
        LocalRepository.saveChecked(context, check)
    }

    fun addFriend(friendId: String) {
        RemoteRepository.addFriend(friendId)
    }

    //Nearby Connection
    fun startAdvertising(context: Context) {
        RemoteRepository.startAdvertising(context)
    }

    fun startDiscovery(context: Context) {
        RemoteRepository.startDiscovery(context)
    }

    fun startAdvertisingDiscovery(context: Context, advertisingDiscovery: Boolean) {
        RemoteRepository.startAdvertisingDiscovery(context, advertisingDiscovery)
    }

}