@file:Suppress("DEPRECATION")

package com.example.newbiebartender.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.newbiebartender.*
import com.example.newbiebartender.databinding.FragmentMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class MyProfileFragment: Fragment(), AggiungiCocktailFragment.OnFragmentInteractionListener {
    
    private var _binding : FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    var db: FirebaseFirestore ?= null
    var storage: FirebaseStorage?= null
    var imageUri: Uri? = null
    var id: String? = null

    lateinit var session : LoginPref

    private var storageReference: StorageReference ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Verifico tramite il meccanismo delle session se l'utente ha effettuato il login
        session = LoginPref(this.requireContext())
        session.checkLogin()

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        
        val tool_log = binding.toolbarProfile.menu.getItem(0)

        // Mi collego alla collection "users" di Firestore per recuperare l'email e l'username dell'utente loggato,
        // che verranno poi mostrati nel fragment dedicato al profilo
        db!!.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user!!.email == document.id) {
                            id = document.id

                            // Il document.id corrisponde all'email dell'utente
                            binding.email.text = document.id
                            binding.nomeutente.text = document["username"].toString()

                            // Utilizzo Firebase Storage per immagazzinare le immagini
                            // Qui vado a recuperare l'immagine del profilo dell'utente e la inserisco
                            // nell'image view. Utilizzo anche la libreria Glide per effetuare il caricamento dell'immagine in modo molto semplice
                            storageReference!!.child("images/$id.jpg").downloadUrl
                                .addOnSuccessListener { uri ->
                                    val imageUrl = uri.toString()
                                    context?.let { Glide.with(it).load(imageUrl).into(binding.fotoprofilo) }
                                }
                                .addOnFailureListener { uri ->
                                    context?.let {Glide.with(it).load(R.drawable.standard_propic).into(binding.fotoprofilo)}
                                }
                        }
                    }
                } else {
                    Log.w(ContentValues.TAG, "Errore nel recupero dei documenti", task.exception)
                }
            }

        // Listener che si aziona se clicco sul pulsante per effettuare il logout, posizionato sulla toolbar
        tool_log!!.setOnMenuItemClickListener {
            val mAuth = FirebaseAuth.getInstance()
            // Effettuo il logout tramite la funzione di Firebase Auth e tramite la session
            mAuth.signOut()
            session.logoutUser()
            startActivity(Intent(context, LoginActivity::class.java))
            Toast.makeText(context, "Logout effettuato", Toast.LENGTH_SHORT).show()
            true
        }

        // Se clicco sull'icona per cambiare l'immagine viene eseguita la funzione cambiaImmagine()
        binding.bottonemodifica.setOnClickListener { cambiaImmagine() }

        // Se clicco sull'icona per scattare la foto viene eseguita la funzione chiediPermessiFotocamera()
        binding.scattafoto.setOnClickListener { chiediPermessiFotocamera() }

        // Se clicco sul button "Preferiti" vengo mandato nel fragment dedicato alla visualizzazione dei preferiti
        binding.gotoFavourites.setOnClickListener{
            binding.root.findNavController()
                    .navigate(R.id.action_navigation_profile_to_favouriteCocktailFragment_)

        }


        // Se clicco sul button Modifica Password, viene creata una Dialog in cui posso inserire la nuova password
        binding.modificaPassword.setOnClickListener { v ->

            val resetPassword = EditText(v.context)
            val passwordreset = AlertDialog.Builder(v.context)

            passwordreset.setTitle("MODIFICA PASSWORD")
            passwordreset.setMessage("Inserisci la nuova password: ")
            passwordreset.setView(resetPassword)


            passwordreset.setPositiveButton("SALVA") { dialog, which ->

                val user = FirebaseAuth.getInstance().currentUser
                val psw = resetPassword.text.toString()

                if (psw.length<6) Toast.makeText(context, "Inserisci una password di 6 caratteri/numeri", Toast.LENGTH_LONG).show()
                else {
                    user!!.updatePassword(psw)
                    Toast.makeText(context, "Password modificata", Toast.LENGTH_LONG).show()
                }

            }
            passwordreset.create().show()
        }
        return view
    }

    // Funzione che verifica se ci sono i permessi per aprire la fotocamera.
    // Se i permessi non sono presenti vengono richiesti all'utente e viene successivamente eseguita la funzione apriFotocamera()
    private fun chiediPermessiFotocamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf(android.Manifest.permission.CAMERA), 100)
        } else {
            apriFotocamera()
        }
    }

    // Se ci sono i permessi viene aperta la fotocamera
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                apriFotocamera()
            } else {
                Toast.makeText(context, "Richiesta autorizzazione fotocamera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Funzione che apre la fotocamera
    private fun apriFotocamera() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera, CAMERA_PERM_CODE)
    }

    // Funzione che cambia l'immagine del profilo dell'utente
    private fun cambiaImmagine() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        @Suppress("DEPRECATION")
        startActivityForResult(intent, 1)
        Toast.makeText(context, "Immagine del profilo modificata", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.fotoprofilo.setImageURI(imageUri)
            caricaImmagine()
        }
        if (requestCode == CAMERA_PERM_CODE) {
            if (BuildConfig.DEBUG && data == null) {
                error("Assertion failed")
            }
            val image = data!!.extras!!["data"] as Bitmap?
            binding.fotoprofilo.setImageBitmap(image)
            uploadImageToFirebase(image)
        }
    }

    // Funzione che carica l'immagine del profilo dell'utente su Firebase Storage, utilizzando il formato .jpg
    private fun uploadImageToFirebase(image: Bitmap?) {
        var images = image
        val imageReference = storageReference!!.child("images/$id.jpg")
        binding.fotoprofilo.isDrawingCacheEnabled = true
        binding.fotoprofilo.buildDrawingCache()
        images = (binding.fotoprofilo.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        images.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageReference.putBytes(data)
        uploadTask.addOnFailureListener { Toast.makeText(context, "Immagine non modificata", Toast.LENGTH_SHORT).show() }
            .addOnSuccessListener {
                Toast.makeText(context, "Immagine modificata correttamente", Toast.LENGTH_SHORT).show()
            }
    }

    // Funzione che carica l'immagine presa dallo Storage all'interno dell'ImageView
    private fun caricaImmagine() {
        val pd = ProgressDialog(context)
        pd.setTitle("Caricamento immagine...")
        pd.show()
        val imageRef = storageReference!!.child("images/$id.jpg")
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { pd.dismiss() }
            .addOnFailureListener {
                pd.dismiss()
                Toast.makeText(context, "Impossibile caricare immagine", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onFragmentInteraction(backText: String?) {
        requireActivity().onBackPressed()
    }

    companion object {
        const val CAMERA_PERM_CODE = 101

    }

}