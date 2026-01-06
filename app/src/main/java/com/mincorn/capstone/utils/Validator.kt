package com.mincorn.capstone.utils

import android.util.Patterns

object Validator {
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}