package com.example.newbiebartender

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
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

        // Session
        // Verifico se l'utente ha precedentemente effettuato il login
        // Se si è già loggato, viene reindirizzato direttamente alla home page
        session = LoginPref(this)
        if (session.isLoggedIn()){
            val i = Intent(applicationContext, Navigation::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()
        }
        // Cliccando sul button "Login", viene eseguita la funzione performLogin()
        binding.loginButtonLogin.setOnClickListener { performLogin() }


        // Se non ci si è registrati, cliccando sulla TextView "Non sei registrato? Registrati ora!"
        // Si viene reindirizzati presso l'activity Register
        binding.backToRegisterTextview.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }



    // Funzione che effettua il login dell'utente
    private fun performLogin() {
        val email = binding.emailEdittextLogin.text.toString().trim()
        val password = binding.passwordEdittextLogin.text.toString().trim()

        // Controlli sui vari campi di inserimento:
        // Campi vuoti, formato email errato, lunghezza password
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

        // Viene utilizzata la piattaforma Firebase di Google, nello specifico FirebaseAuth,
        // per il login dell'utente.
        // Se il login va a buon fine, si viene reindirizzati all' Home page dell'app
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")

                    //Si utilizza la session per mantenere l'utente loggato
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
