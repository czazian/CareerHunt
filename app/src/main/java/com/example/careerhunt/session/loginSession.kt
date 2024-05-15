package com.example.careerhunt.session

import android.content.Context
import android.content.SharedPreferences

class loginSession (context:Context?){
    // shared preference mode
    val PRIVATE_MODE = 0

    // shared preference filename
    private val PREF_NAME = "SharedLoginPreferences"
    private val IS_LOGIN = "is_login"

    val pref : SharedPreferences? = context?.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
    val editor : SharedPreferences.Editor? = pref?.edit()

    private val sharedPreferences: SharedPreferences = context?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)!!

    fun setLogin(isLogin: Boolean) {
        sharedPreferences.edit().putBoolean(IS_LOGIN, isLogin).apply()
    }

    fun isLogin(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGIN, false)
    }

    fun setID(id:String?){
        editor?.putString("id",id.toString())
        editor?.commit()
    }

    fun getID():String?{
        return pref?.getString("id","")
    }

    fun setType(type:String?){
        editor?.putString("type",type.toString())
        editor?.commit()
    }

    fun getType():String?{
        return pref?.getString("type","")
    }

    fun removeData(){
        editor?.clear()
        editor?.commit()
    }

}