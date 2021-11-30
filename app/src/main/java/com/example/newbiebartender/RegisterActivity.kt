package com.example.newbiebartender

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class RegisterActivity : AppCompatActivity() {

    var username: TextInputEditText? = null
    var email: TextInputEditText? = null
    var password: TextInputEditText? = null
    var ripetiPassword: TextInputEditText? = null
    var usernameL: TextInputLayout? = null
    var emailL: TextInputLayout? = null
    var passwordL: TextInputLayout? = null
    var ripetiPasswordL: TextInputLayout? = null
    var b: Button? = null
    var login: TextView? = null
    var mAuth: FirebaseAuth? = null
    var firebaseFirestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        username = findViewById(R.id.editTextTextUser)
        usernameL = findViewById(R.id.editTextTextUsernameLayout)
        email = findViewById(R.id.editTextTextEmailAddress)
        emailL = findViewById(R.id.editTextTextEmailAddressLayout)
        password = findViewById(R.id.editTextTextPassword)
        passwordL = findViewById(R.id.editTextTextPasswordLayout)
        ripetiPassword = findViewById(R.id.editTextTextPasswordConfirm)
        ripetiPasswordL = findViewById(R.id.editTextTextPasswordConfirmLayout)
        b = findViewById(R.id.registerButtonRegister)
        login = findViewById(R.id.back_to_login_textView)
        mAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        b!!.setOnClickListener(View.OnClickListener {
            val emails = email!!.text.toString().trim { it <= ' ' }
            val passwords = password!!.text.toString().trim { it <= ' ' }
            val ripetipasswords = ripetiPassword!!.text.toString().trim { it <= ' ' }
            val usernames = username!!.text.toString()
            if (TextUtils.isEmpty(usernames)) {
                if (TextUtils.isEmpty(emails)) {
                    if (TextUtils.isEmpty(passwords)) {
                        if (TextUtils.isEmpty(ripetipasswords)) {
                            ripetiPasswordL!!.error = "E' necessario ripetere la password"
                        }
                        passwordL!!.error = "E' richiesta la password"
                    }
                    emailL!!.error = "E' richiesta l'email"
                }
                usernameL!!.error = "E' richiesto l'username"
                return@OnClickListener
            }
            if (!TextUtils.equals(passwords, ripetipasswords)) {
                ripetiPasswordL!!.error = "La password ripetuta e' errata"
                return@OnClickListener
            }
            if (passwords.length < 6) {
                passwordL!!.error = "La password deve contenere almeno 6 caratteri"
                return@OnClickListener
            }
            b!!.visibility = View.INVISIBLE
            login!!.visibility = View.INVISIBLE
            mAuth!!.createUserWithEmailAndPassword(emails, passwords).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: MutableMap<String, Any> = HashMap()
                    user["username"] = usernames
                    firebaseFirestore!!.collection("users")
                            .document(emails)
                            .set(user).addOnSuccessListener { Toast.makeText(this, "Utente creato", Toast.LENGTH_SHORT).show() }.addOnFailureListener { e -> Log.d("Message2", "OnFailure :$e") }
                    startActivity(Intent(applicationContext, Navigation::class.java))
                } else {
                    Toast.makeText(this, "Impossibile creare l'utente", Toast.LENGTH_SHORT).show()
                    b!!.visibility = View.VISIBLE
                    login!!.visibility = View.VISIBLE
                }
            }
        })
        login!!.setOnClickListener { startActivity(Intent(applicationContext, LoginActivity::class.java)) }
    }

    override fun onBackPressed() {}


}
