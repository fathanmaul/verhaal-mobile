package dev.rushia.verhaal_mobile.utils

import android.content.Context
import android.content.SharedPreferences
import dev.rushia.verhaal_mobile.data.remote.response.LoginResult

class PreferenceManager(context: Context) {
    private var prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(
            Const.AUTH_PREFERENCES,
            Context.MODE_PRIVATE
        )
    private val editor = prefs.edit()

    private fun setStringPreference(prefKey: String, value: String) {
        editor.putString(prefKey, value)
        editor.apply()
    }

    fun setLoginPref(userItem: LoginResult) {
        userItem.let {
            setStringPreference(Const.TOKEN_KEY, it.token)
            setStringPreference(Const.USER_NAME, it.name)
        }
    }

    fun clearAllPreferences() {
        editor.remove(Const.TOKEN_KEY)
        editor.remove(Const.USER_NAME)
        editor.apply()
    }


    val getToken = prefs.getString(Const.TOKEN_KEY, "") ?: ""
    val name = prefs.getString(Const.USER_NAME, "") ?: ""

}