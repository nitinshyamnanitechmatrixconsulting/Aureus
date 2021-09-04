package com.auresus.academy.model.local.preference

import android.content.SharedPreferences


/* Created by Sahil Bharti on 10/1/19.
 *
*/

class PreferenceHelper constructor(private val mSharedPreferences: SharedPreferences) {


    private val IS_USER_LOGGED_IN: String = "IS_USER_LOGGED_IN"
    private val USER_TYPE: String = "USER_TYPE"
    private val USER_EMAIL: String = "USER_EMAIL"
    private val USER_PASSWORD_: String = "USER_PASSWORD"
    private val USER_NAME: String = "USER_NAME"

    companion object {
        val PARENR_ID: String = "PARENR_ID"
    }

    fun put(key: String, value: String) {
        mSharedPreferences.edit().putString(key, value).apply()
    }

    fun put(key: String, value: Int) {
        mSharedPreferences.edit().putInt(key, value).apply()
    }

    fun put(key: String, value: Long) {
        mSharedPreferences.edit().putLong(key, value).apply()
    }

    fun put(key: String, value: Float) {
        mSharedPreferences.edit().putFloat(key, value).apply()
    }

    fun put(key: String, value: Boolean) {
        mSharedPreferences.edit().putBoolean(key, value).apply()
    }

    operator fun get(key: String): String {
        return mSharedPreferences.getString(key, "") ?: ""
    }

    operator fun get(key: String, defaultValue: Int): Int? {
        return mSharedPreferences.getInt(key, defaultValue)
    }

    operator fun get(key: String, defaultValue: Long): Long? {
        return mSharedPreferences.getLong(key, defaultValue)
    }

    operator fun get(key: String, defaultValue: Float): Float? {
        return mSharedPreferences.getFloat(key, defaultValue)
    }

    fun getBoolean(key: String): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }

    fun deleteSavedData(key: String) {
        mSharedPreferences.edit().remove(key).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return getBoolean(IS_USER_LOGGED_IN)
    }

    fun getUserType(): Int? {
        return get(USER_TYPE, -1)
    }

    fun getEmail(): String {
        return get(USER_EMAIL)
    }

    fun getPassword(): String {
        return get(USER_PASSWORD_)
    }

    fun getUserName(): String {
        return get(USER_NAME)
    }

    fun setUserLoggedIn(isLoggedIn: Boolean) {
        put(IS_USER_LOGGED_IN, isLoggedIn)
    }

    fun setUserType(userType: Int) {
        put(USER_TYPE, userType)
    }

    fun setUserEmail(email: String) {
        put(USER_EMAIL, email)
    }

    fun setUserPassword(password: String) {
        put(USER_PASSWORD_, password)
    }

    fun setUserName(password: String) {
        put(USER_NAME, password)
    }

    fun clearData() {
        mSharedPreferences.edit().clear().commit();
    }

}