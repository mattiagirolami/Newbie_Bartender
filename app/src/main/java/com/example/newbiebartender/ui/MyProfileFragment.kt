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
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.newbiebartender.BuildConfig
import com.example.newbiebartender.LoginActivity
import com.example.newbiebartender.LoginPref
import com.example.newbiebartender.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class MyProfileFragment: Fragment(), AggiungiCocktailFragment.OnFragmentInteractionListener {

    var db: FirebaseFirestore ?= null
    var storage: FirebaseStorage?= null
    var imageUri: Uri? = null
    var fotoprofilo: ImageView? = null
    var modificaPassword: Button? = null
    var modificaImmagine: ImageButton? = null
    var bottonelogout: ImageButton? = null
    var scattaImmagine: ImageButton? = null
    var nomeutente: TextView? = null
    var email: TextView? = null
    var id: String? = null

    lateinit var session : LoginPref

    private var storageReference: StorageReference ?= null
    //TODO: vedere se mettere viewBinding https://developer.android.com/topic/libraries/view-binding
    // private lateinit var binding:

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_my_profile,container, false)

        session = LoginPref(this.requireContext())
        session.checkLogin()

        fotoprofilo = root.findViewById(R.id.fotoprofilo)
        modificaImmagine = root.findViewById(R.id.bottonemodifica)
        scattaImmagine = root.findViewById(R.id.scattafoto)
        bottonelogout = root.findViewById(R.id.bottonelogout)
        nomeutente = root.findViewById(R.id.nomeutente)
        email = root.findViewById(R.id.email)
        modificaPassword = root.findViewById(R.id.modifica_password)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        db!!.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user!!.email == document.id) {
                            id = document.id
                            //email.setText(document.id)
                            email!!.text = document.id
                            //nomeutente.setText(document["username"].toString())
                            nomeutente!!.text = document["username"].toString()
                            storageReference!!.child("images/$id.jpg").downloadUrl
                                .addOnSuccessListener { uri ->
                                    val imageUrl = uri.toString()
                                    context?.let { Glide.with(it).load(imageUrl).into(fotoprofilo!!) }
                                }
                        }
                    }
                } else {
                    Log.w(ContentValues.TAG, "Errore nel recupero dei documenti", task.exception)
                }
            }

        bottonelogout!!.setOnClickListener ({
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signOut()
            session.logoutUser()
            startActivity(Intent(context, LoginActivity::class.java))
            Toast.makeText(context, "Logout effettuato", Toast.LENGTH_SHORT).show()
        })

        modificaImmagine!!.setOnClickListener { cambiaImmagine() }

        scattaImmagine!!.setOnClickListener { chiediPermessiFotocamera() }

        modificaPassword!!.setOnClickListener { v ->
            val resetPassword = EditText(v.context)
            val passwordreset = AlertDialog.Builder(v.context)
            passwordreset.setTitle("MODIFICA PASSWORD")
            passwordreset.setMessage("Inserisci la nuova password: ")
            passwordreset.setView(resetPassword)
            passwordreset.setPositiveButton("SALVA") { dialog, which ->
                val user = FirebaseAuth.getInstance().currentUser
                val psw = resetPassword.text.toString()
                user!!.updatePassword(psw).addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Password modificata correttamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener {
                    val mAuth = FirebaseAuth.getInstance()
                    Toast.makeText(context, "Password non modificata.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(
                        context,
                        "Effettua nuovamente il login e riprova.",
                        Toast.LENGTH_SHORT
                    ).show()
                    mAuth.signOut()
                    startActivity(Intent(context, LoginActivity::class.java))
                }
            }
            passwordreset.create().show()
        }
        return root
    }

    private fun chiediPermessiFotocamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf(android.Manifest.permission.CAMERA), 100)
        } else {
            apriFotocamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                apriFotocamera()
            } else {
                Toast.makeText(context, "Richiesta autorizzazione fotocamera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun apriFotocamera() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera, CAMERA_PERM_CODE)
    }

    private fun cambiaImmagine() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
        Toast.makeText(context, "Immagine del profilo modificata", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            fotoprofilo!!.setImageURI(imageUri)
            caricaImmagine()
        }
        if (requestCode == CAMERA_PERM_CODE) {
            if (BuildConfig.DEBUG && data == null) {
                error("Assertion failed")
            }
            val image = data!!.extras!!["data"] as Bitmap?
            fotoprofilo!!.setImageBitmap(image)
            uploadImageToFirebase(image)
        }
    }

    private fun uploadImageToFirebase(image: Bitmap?) {
        var images = image
        val imageReference = storageReference!!.child("images/$id.jpg")
        fotoprofilo!!.isDrawingCacheEnabled = true
        fotoprofilo!!.buildDrawingCache()
        images = (fotoprofilo!!.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        images.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageReference.putBytes(data)
        uploadTask.addOnFailureListener { Toast.makeText(context, "Immagine non modificata", Toast.LENGTH_SHORT).show() }
            .addOnSuccessListener {
                Toast.makeText(context, "Immagine modificata correttamente", Toast.LENGTH_SHORT).show()
            }
    }

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