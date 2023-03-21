package com.boopro.btracker.data.local

import android.content.Context
import com.boopro.btracker.data.model.UserModel

// Class through which data stored locally (local database/shared preferences/in memory cache) will be accessed
object LocalRepository {

    fun getUserInfo(context: Context): UserModel {
        return PrefUtility.getUserInfo(context)
    }

    fun saveUserInfo(context: Context, user: UserModel) {
        PrefUtility.saveUserInfo(context, user)
    }

    fun getChecked(context: Context): Boolean {
        return PrefUtility.getChecked(context)
    }

    fun saveChecked(context: Context, check: Boolean) {
        PrefUtility.saveChecked(context, check)
    }
}