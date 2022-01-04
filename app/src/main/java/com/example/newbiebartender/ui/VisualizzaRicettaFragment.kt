package com.example.newbiebartender.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.newbiebartender.ListaVisualizzazioneFragment
import com.example.newbiebartender.Navigation
import com.example.newbiebartender.databinding.FragmentVisualizzaRicettaBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class VisualizzaRicettaFragment : ListaVisualizzazioneFragment() {

    private var storageReference: StorageReference ?= null

    private var ingredientiL: List<String>? = ArrayList()
    private var descrizioneL: List<String> = ArrayList()

    var adapterIngredienti: ArrayAdapter<String>? = null
    var adapterDescrizione: ArrayAdapter<String>? = null

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
    }

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
                    binding.nomecocktail.text = document["titolo"].toString()
                    binding.difficolta.text = document["difficoltà"].toString()

                    storageReference!!.child("$tipoCocktail/$idRicetta.jpg").downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Glide.with(requireContext()).load(imageUrl).into(binding.fotodeldrink)
                    }
                    //TODO: risolvere questo problema
                    ingredientiL = (document["ingredienti"] as List<String>?)!!
                    adapterIngredienti = ArrayAdapter(requireContext(),
                            android.R.layout.simple_list_item_1,
                            ingredientiL!!)
                    binding.visualizzaIngredienti.adapter = adapterIngredienti
                    adapterIngredienti!!.notifyDataSetChanged()
                    descrizioneL = (document["descrizione"] as List<String>?)!!
                    adapterDescrizione = ArrayAdapter(requireContext(),
                            android.R.layout.simple_list_item_1,
                            descrizioneL)
                    binding.visualizzaDescrizione.adapter = adapterDescrizione
                    adapterDescrizione!!.notifyDataSetChanged()
                }
            }
        }
        binding.scrollvr.fullScroll(ScrollView.FOCUS_DOWN)
        binding.scrollvr.fullScroll(ScrollView.FOCUS_UP)

        binding.tornaIndietro.setOnClickListener {
            val back = Intent(context, Navigation::class.java)
            startActivity(back)
        }
        return view
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