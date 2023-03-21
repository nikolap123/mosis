package com.boopro.btracker.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.boopro.btracker.data.model.UserModel
import com.boopro.btracker.helper.Consts
import com.google.gson.Gson

// Class for accessing Shared Preferences
object PrefUtility {
    private val gson = Gson()

    private val Context.defaultSharedPreferences: SharedPreferences?
        get() = PreferenceManager.getDefaultSharedPreferences(this)
    private lateinit var sharedPreferences: SharedPreferences

    fun getUserInfo(context: Context): UserModel {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val user = sharedPreferences.getString(Consts.USER, null)

        return gson.fromJson(user, UserModel::class.java)
    }

    fun saveUserInfo(context: Context, user: UserModel) {
        context.defaultSharedPreferences?.edit()?.remove(Consts.USER)?.apply()
        context.defaultSharedPreferences?.edit()?.putString(Consts.USER, Gson().toJson(user))
            ?.apply()
    }

    fun getChecked(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        return sharedPreferences.getBoolean(Consts.CHECK, false)
    }

    fun saveChecked(context: Context, check: Boolean) {
        context.defaultSharedPreferences?.edit()?.putBoolean(Consts.CHECK, check)?.apply()
    }

}