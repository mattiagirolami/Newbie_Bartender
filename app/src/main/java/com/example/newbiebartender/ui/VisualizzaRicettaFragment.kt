package com.example.newbiebartender.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable

import com.bumptech.glide.Glide
import com.example.newbiebartender.ListaVisualizzazioneFragment
import com.example.newbiebartender.R
import com.example.newbiebartender.databinding.FragmentVisualizzaRicettaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class VisualizzaRicettaFragment : ListaVisualizzazioneFragment() {

    private var storageReference: StorageReference ?= null

    private lateinit var auth: FirebaseUser

    private var isFav = false

    override var db: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    override var titolo: String? = null
    var docref: DocumentReference? = null
    var idRicetta: String? = null
    override var tipoCocktail: String? = null

    private  var _binding: FragmentVisualizzaRicettaBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            idRicetta = requireArguments().getString("idRicetta")
            tipoCocktail = requireArguments().getString("tipoCocktail")
        }

        auth = FirebaseAuth.getInstance().currentUser!!
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = FragmentVisualizzaRicettaBinding.inflate(inflater, container, false)
        val view = binding.root
        
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        docref = db!!.collection(tipoCocktail!!).document(idRicetta!!)

        docref!!.get()
                .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                if (document!!.exists()) {
                    binding.nomeCockTw.text = document["titolo"].toString()
                    binding.difficoltaTofill.text = document["difficoltà"].toString()
                    binding.procedimentoView.text = document["descrizione"].toString().capitalize()
                    binding.tipologiaTofill.text = tipoCocktail.toString().capitalize()
                    binding.recipeByUser.text = "Ricetta di ${document["autore"].toString()}"

                    val arrayIngr = document["ingredienti"] as ArrayList<String>?
                    var ingreds = ""
                    if (arrayIngr != null){
                        for (ing in arrayIngr) {
                            ingreds = ingreds+ing+"\n"
                        }
                    }
                    binding.ingredienti.text = ingreds

                    checkFavourite(document)

                    storageReference!!.child("$tipoCocktail/$idRicetta.jpg")
                            .downloadUrl
                            .addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                context.let{ Glide.with(requireContext()).load(imageUrl).into(binding.fotocock)}
                            }

                            .addOnFailureListener{ uri ->
                                Glide.with(requireContext())
                                        .load(R.drawable.drink_default).into(binding.fotocock)
                            }
                }
            }
        }


        return view
    }

    private fun checkFavourite(document: DocumentSnapshot) : Boolean {

        for(favourite in document["preferiti"] as ArrayList<String>) {
            if(favourite == auth.email) {
                isFav = true
            }
        }
        return isFav
    }

    private fun addToFav() {

        isFav = true
        FirebaseFirestore.getInstance()
                .collection(tipoCocktail!!)
                .document(idRicetta!!)
                .update("preferiti", FieldValue.arrayUnion(auth.email.toString()))

    }

    private fun removeFav() {
        isFav = false

        FirebaseFirestore.getInstance()
                .collection(tipoCocktail!!)
                .document(idRicetta!!)
                .update("preferiti", FieldValue.arrayRemove(auth.email.toString()))

    }


    companion object {
        fun newInstance(idRicetta: String?, tipoCocktail: String?): VisualizzaRicettaFragment {
            val fragment = VisualizzaRicettaFragment()
            val args = Bundle()
            args.putString("idRicetta", idRicetta)
            args.putString("tipoCocktail", tipoCocktail)
            fragment.arguments = args
            return fragment
        }
    }
}