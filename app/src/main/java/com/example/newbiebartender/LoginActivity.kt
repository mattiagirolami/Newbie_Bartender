package com.example.newbiebartender

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
            var i : Intent = Intent(applicationContext, Navigation::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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

        if (email.isEmpty()) {
            binding.emailEdittextLogin.error = "Inserisci l'email."
            return
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEdittextLogin.error = "Il formato è errato."
            return
        }
        else if (password.isEmpty()) {
            binding.passwordEdittextLogin.error = "Inserisci la password."
            return
        }
        else if (password.length < 6) {
            binding.passwordEdittextLogin.error = "Inserisci una password di 6 caratteri/numeri."
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

    }

}

/*
var email: EditText ?= null
var password: EditText? = null
var emailLayout: TextInputLayout? = null
var passwordLayout: TextInputLayout? = null
var registrati: TextView? = null
var passwordDimenicata: TextView? = null
var bLogin: Button? = null
var progressBar: ProgressBar? = null
var mAuth: FirebaseAuth? = null
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    setContentView(R.layout.activity_login)
    password = findViewById(R.id.editTextTextPassword)
    email = findViewById(R.id.editTextTextEmailAddress)
    emailLayout = findViewById(R.id.editTextTextEmailAddressLayout)
    passwordLayout = findViewById(R.id.password_edittext_login_layout)
    registrati = findViewById(R.id.back_to_register_textview)
    passwordDimenicata = findViewById(R.id.forgotten_pw)
    bLogin = findViewById(R.id.login_button_login)
    progressBar = findViewById(R.id.progressBar)
    mAuth = FirebaseAuth.getInstance()
    bLogin!!.setOnClickListener(View.OnClickListener {
        val emails = email!!.text.toString().trimEnd()
        val passwords = password!!.text.toString().trimEnd()
        if (TextUtils.isEmpty(emails)) {
            if (TextUtils.isEmpty(passwords)) {
                passwordLayout!!.error = "E' richiesta la password"
            }
            emailLayout!!.error = "E' richiesta l'email"
            return@OnClickListener
        }
        if (passwords.length < 6) {
            passwordLayout!!.error = "La password deve contenere almeno 6 caratteri"
            return@OnClickListener
        }
        passwordDimenicata!!.visibility = View.INVISIBLE
        registrati!!.visibility = View.INVISIBLE
        bLogin!!.visibility = View.INVISIBLE
        progressBar!!.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(emails, passwords).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Utente loggato", Toast.LENGTH_SHORT).show()
                val homePage = Intent(this, Navigation::class.java)
                homePage.putExtra("userId", emails)
                homePage.putExtra("psw", passwords)
                startActivity(homePage)
            } else {
                Toast.makeText(this, "Utente non loggato", Toast.LENGTH_SHORT).show()
                progressBar!!.visibility = View.GONE
                passwordDimenicata!!.visibility = View.VISIBLE
                registrati!!.visibility = View.VISIBLE
                bLogin!!.visibility = View.VISIBLE
            }
        }
    })
    registrati!!.setOnClickListener { startActivity(Intent(applicationContext, RegisterActivity::class.java)) }
    passwordDimenicata!!.setOnClickListener { v ->
        val resetEmail = EditText(v.context)
        val passwordReset = AlertDialog.Builder(v.context)
        passwordReset.setTitle("Desideri reimpostare la password?")
        passwordReset.setMessage("Inserisci la tua email per ottenere il link per il ripristino")
        passwordReset.setView(resetEmail)
        passwordReset.setPositiveButton("Sì") { dialog, which ->
            val mail = resetEmail.text.toString()
            mAuth!!.sendPasswordResetEmail(mail).addOnSuccessListener { Toast.makeText(this, "Link di reimpostazione inoltrato correttamente", Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener {
                        Toast.makeText(this, "Errore, link non inoltrato", Toast.LENGTH_SHORT).show()
                    }
        }
        passwordReset.setNegativeButton("No") { dialog, which -> }
        passwordReset.create().show()
    }

}

override fun onBackPressed() {

}
 */