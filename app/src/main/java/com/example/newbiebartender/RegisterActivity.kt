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

        val mAuth = FirebaseAuth.getInstance()
        val firebaseFirestore = FirebaseFirestore.getInstance()

        binding.registerButtonRegister.setOnClickListener(View.OnClickListener {
            val emails = binding.editTextTextEmailAddress.text.toString().trim { it <= ' ' }
            val passwords = binding.editTextTextPassword.text.toString().trim { it <= ' ' }
            val ripetipasswords = binding.editTextTextPasswordConfirm.text.toString().trim { it <= ' ' }
            val usernames = binding.editTextTextUser.text.toString()
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

            mAuth.createUserWithEmailAndPassword(emails, passwords).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: MutableMap<String, Any> = HashMap()
                    user["username"] = usernames
                    firebaseFirestore.collection("users")
                            .document(emails)
                            .set(user).addOnSuccessListener { Toast.makeText(this, "Utente creato", Toast.LENGTH_SHORT).show() }.addOnFailureListener { e -> Log.d("Message2", "OnFailure :$e") }
                    startActivity(Intent(applicationContext, Navigation::class.java))
                } else {
                    Toast.makeText(this, "Impossibile creare l'utente", Toast.LENGTH_SHORT).show()
                    binding.registerButtonRegister.visibility = View.VISIBLE
                    binding.backToLoginTextView.visibility = View.VISIBLE
                }
            }
        })
        binding.backToLoginTextView.setOnClickListener { startActivity(Intent(applicationContext, LoginActivity::class.java)) }
    }

    override fun onBackPressed() {}


}
