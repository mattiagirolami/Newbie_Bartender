package com.example.newbiebartender.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
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

    //TODO: vedere se unificare tutti i cocktail sotto un'unica cartella su firebase

    private var storageReference: StorageReference ?= null

    private lateinit var auth: FirebaseUser

    var isRated: Boolean = false

    private var isFav = false

    var valutazione : String? = null

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

        setupToolbarWithNavigation()

        binding.spinnerValutazione.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                valutazione = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
        
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        docref = db!!.collection(tipoCocktail!!).document(idRicetta!!)

        docref!!.get()
                .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                if (document!!.exists()) {

                    binding.ratingTofill.text = calculateAvg(document).toString() + " su 5"

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

                    //checkRatings(document)
                    if(checkRatings(document)){
                        binding.btnSalvaValutazione.visibility = View.GONE
                        binding.ratingTw.visibility = View.GONE
                        binding.spinnerValutazione.visibility = View.GONE
                    }

                    //checkFavourite(document)

                    if (checkFavourite(document)){
                        binding.showRecipeToolbar.menu.getItem(0)
                            .setIcon(R.drawable.ic_full_star)
                        binding.showRecipeToolbar.menu.getItem(0)
                            .setOnMenuItemClickListener {
                                removeFav()
                                Toast.makeText(requireContext(), "Rimosso dai preferiti", Toast.LENGTH_SHORT).show()
                                true
                            }
                    } else {
                        binding.showRecipeToolbar.menu.getItem(0)
                            .setIcon(R.drawable.ic_empty_star)
                        binding.showRecipeToolbar.menu.getItem(0)
                            .setOnMenuItemClickListener {
                                addToFav()
                                Toast.makeText(requireContext(), "Aggiunto ai preferiti", Toast.LENGTH_SHORT).show()
                                true
                            }
                    }

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

        binding.btnSalvaValutazione.setOnClickListener {
            val mPacca: MutableMap<String, Any?> = HashMap()
            mPacca["email"] = auth.email.toString()
            mPacca["voto"] = valutazione
            FirebaseFirestore.getInstance()
                .collection(tipoCocktail!!)
                .document(idRicetta!!)
                .update("valutazioni", FieldValue.arrayUnion(mPacca))
            Toast.makeText(context, "Hai inserito una valutazione di $valutazione/5", Toast.LENGTH_SHORT).show()
        }



        return view
    }


    private fun setupToolbarWithNavigation() {
        val toolbar = binding.showRecipeToolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun calculateAvg(document: DocumentSnapshot) : Float {

        var somma: Int = 0
        var counter = 0

        for(valutazione in document["valutazioni"] as ArrayList<HashMap<String, String>>){
            somma += valutazione["voto"]!!.toInt()
            counter++
        }

        return if (somma == 0) 0F
        else (somma/counter).toFloat()

    }

    private fun checkRatings(document: DocumentSnapshot) : Boolean {

        for(valutazione in document["valutazioni"] as ArrayList<HashMap<String, String>>){

            if (valutazione["email"] == auth.email.toString()) isRated = true

        }
        return isRated

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