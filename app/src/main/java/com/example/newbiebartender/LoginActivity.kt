package com.example.newbiebartender

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newbiebartender.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding
    lateinit var session: LoginPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        session = LoginPref(this)
        if (session.isLoggedIn()){
            val i = Intent(applicationContext, Navigation::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()
        }

        binding.loginButtonLogin.setOnClickListener { performLogin() }

        binding.backToRegisterTextview.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.forgottenPw.setOnClickListener { v ->
            val resetEmail = EditText(v.context)
            val passwordReset = AlertDialog.Builder(v.context)
            val mAuth : FirebaseAuth ?= null

            passwordReset.setTitle("Desideri reimpostare la password?")
            passwordReset.setMessage("Inserisci la tua email per ottenere il link per il ripristino")
            passwordReset.setView(resetEmail)
            passwordReset.setPositiveButton("Sì") { dialog, which ->
                val mail = resetEmail.text.toString()
                mAuth?.sendPasswordResetEmail(mail)?.addOnSuccessListener { Toast.makeText(this, "Link di reimpostazione inoltrato correttamente", Toast.LENGTH_SHORT).show()
                }?.addOnFailureListener {
                    Toast.makeText(this, "Errore, link non inoltrato", Toast.LENGTH_SHORT).show()
                }
            }
            passwordReset.setNegativeButton("No") { dialog, which -> }
            passwordReset.create().show()
        }

    }

    private fun performLogin() {
        val email = binding.emailEdittextLogin.text.toString().trim()
        val password = binding.passwordEdittextLogin.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            if (TextUtils.isEmpty(password)) {
                binding.passwordEdittextLoginLayout.error = "Inserisci la password."
                return
            }
            binding.emailEdittextLoginLayout.error = "Inserisci l'email."
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEdittextLoginLayout.error = "Il formato è errato."
            return
        }

        if (password.length < 6) {
            binding.passwordEdittextLoginLayout.error = "Inserisci una password di 6 caratteri/numeri."
            return
        }



        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")
                    session.createLoginSession(email, password)
                    val intent = Intent(this, Navigation::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Login fallito a causa di ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    override fun onBackPressed() {
        finish()
    }

}
