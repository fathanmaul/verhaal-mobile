package dev.rushia.verhaal_mobile.utils

import android.text.TextUtils

object Validation {
    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return email.matches(emailRegex.toRegex())
    }

    fun isPasswordValid(password: CharSequence): Boolean {
        return !TextUtils.isEmpty(password) && password.length >= 8
    }

    fun isNameValid(name: String): Boolean {
        return !TextUtils.isEmpty(name)
    }

    fun loginValidate(email: String, password: String): Boolean {
        return isEmailValid(email) && isPasswordValid(password)
    }
}