package com.example.newbiebartender

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class LoginPref {

    //TODO: commentare (?)
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var con: Context
    var PRIVATEMODE : Int = 0

    constructor(con:Context){
        this.con = con
        pref = con.getSharedPreferences(PREF_NAME, PRIVATEMODE)
        editor = pref.edit()
    }

    companion object{
        const val PREF_NAME = "Login Preference"
        const val IS_LOGIN = "isLoggedIn"
        const val KEY_EMAIL = "username"
        const val KEY_PASSWORD = "password"
    }

    fun createLoginSession(email: String, password: String){
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password)
        editor.commit()
    }

    fun checkLogin(){
        if(!this.isLoggedIn()){
            val i: Intent = Intent(con, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            con.startActivity(i)
        }
    }

    fun getUserDetails(): HashMap<String, String>{
        val user: Map<String, String> = HashMap()
        (user as HashMap)[KEY_EMAIL] = pref.getString(KEY_EMAIL, null)!!
        user[KEY_PASSWORD] = pref.getString(KEY_PASSWORD, null)!!
        return user
    }

    fun logoutUser(){
        editor.clear()
        editor.commit()
        val i = Intent(con, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity(i)
    }

    fun isLoggedIn(): Boolean{
        return pref.getBoolean(IS_LOGIN, false)
    }
}