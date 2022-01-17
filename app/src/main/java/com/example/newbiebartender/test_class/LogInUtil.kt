package com.example.newbiebartender.test_class

import androidx.core.util.PatternsCompat

// Questo oggetto verrà utilizzato nella fase di test del login
object LogInUtil {

    fun checkFields( email:String,
                     password:String,
                    ) : Boolean {

        if(email.isEmpty()) return false

        if(password.isEmpty()) return false

        if(password.length<6) return false

        if(!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) return false

        return true
    }
}