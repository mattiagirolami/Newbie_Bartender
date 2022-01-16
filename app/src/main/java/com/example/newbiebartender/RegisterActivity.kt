package com.example.newbiebartender

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newbiebartender.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Creo due variabili che contengono rispettivamente l'istanza dell'utente su Firebase
        // e l'istanza del database Firebase Firestore
        val mAuth = FirebaseAuth.getInstance()
        val firebaseFirestore = FirebaseFirestore.getInstance()

        // Cliccando sul button "Registrati" si avvia il processo di registrazione di un nuovo utente
        binding.registerButtonRegister
                .setOnClickListener(View.OnClickListener {

                    // Vengono prese le informazioni dai vari campi, rimuovendo gli eventuali spazi bianchi
                    // inseriti erroneamente alla fine di ogni campo
                    val emails = binding.editTextTextEmailAddress.text.toString().trim { it <= ' ' }
                    val passwords = binding.editTextTextPassword.text.toString().trim { it <= ' ' }
                    val ripetipasswords = binding.editTextTextPasswordConfirm.text.toString().trim { it <= ' ' }
                    val usernames = binding.editTextTextUser.text.toString()


                    // Vengono eseguiti i vari controlli sui campi:
                    // campi vuoti, lunghezza della password non sufficiente e password ripetuta che non
                    // corrisponde con la prima
                    if (TextUtils.isEmpty(usernames)) {
                        if (TextUtils.isEmpty(emails)) {
                            if (TextUtils.isEmpty(passwords)) {
                                if (TextUtils.isEmpty(ripetipasswords)) {
                                    binding.editTextTextPasswordConfirm.error = "E' necessario ripetere la password"
                                }
                                binding.editTextTextPasswordLayout.error = "E' richiesta la password"
                            }
                            binding.editTextTextEmailAddressLayout.error = "E' richiesta l'email"
                        }
                        binding.editTextTextUsernameLayout.error = "E' richiesto l'username"
                        return@OnClickListener
                    }
                    if (!TextUtils.equals(passwords, ripetipasswords)) {
                        binding.editTextTextPasswordConfirmLayout.error = "La password ripetuta e' errata"
                        return@OnClickListener
                    }

                    if (passwords.length < 6) {
                        binding.editTextTextPasswordLayout.error = "La password deve contenere almeno 6 caratteri"
                        return@OnClickListener
                    }
                    binding.registerButtonRegister.visibility = View.INVISIBLE
                    binding.backToLoginTextView.visibility = View.INVISIBLE

                    // Viene creato l'utente su Firebase
                    mAuth.createUserWithEmailAndPassword(emails, passwords).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            // Se la creazione va a buon fine, viene inserito l'username dell'utente all'interno della
                            // collection "username" su Firestore
                            val user: MutableMap<String, Any> = HashMap()
                            user["username"] = usernames
                            firebaseFirestore.collection("users")
                                    .document(emails)
                                    .set(user)
                                    .addOnSuccessListener { Toast.makeText(this, "Utente creato", Toast.LENGTH_SHORT).show() }
                                    .addOnFailureListener { e -> Log.d("Message2", "OnFailure :$e") }

                            //Una volta avvenuta la registrazione, si deve effettuare il login
                            startActivity(Intent(applicationContext, LoginActivity::class.java))

                        } else {
                            // Se si è verificato qualche errore, viene mostrato un Toast
                            Toast.makeText(this, "Impossibile creare l'utente", Toast.LENGTH_SHORT).show()
                            //TODO: togliere
                            binding.registerButtonRegister.visibility = View.VISIBLE
                            binding.backToLoginTextView.visibility = View.VISIBLE
                        }
                    }
                })

        // Se si vuole tornare all'activity di Login si può cliccare sulla stringa "Sei già registrato? Torna al login"
        binding.backToLoginTextView.setOnClickListener { startActivity(Intent(applicationContext, LoginActivity::class.java)) }
    }

}
