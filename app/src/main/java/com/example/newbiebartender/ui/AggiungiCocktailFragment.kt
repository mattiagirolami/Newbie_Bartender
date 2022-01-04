package com.example.newbiebartender.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.*
import androidx.fragment.app.Fragment
import com.example.newbiebartender.Navigation
import com.example.newbiebartender.databinding.FragmentAggiungiCocktailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList




class AggiungiCocktailFragment : Fragment(), OnItemSelectedListener {

    private lateinit var editorIngredienti : EditText

    private  var _binding: FragmentAggiungiCocktailBinding? = null
    private val binding get() = _binding!!

    var storage: FirebaseStorage?= null
    private var storageReference: StorageReference ?= null
    //var ingredientiAl = ArrayList<String>()
    var descrizioneAl = ArrayList<String>()
    var id = GenerateRandomString.randomString(20)
    var imageUri: Uri? = null
    var difficolta: String? = null

    var tipologia: String? = null
    lateinit var tipo_drink: String

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentAggiungiCocktailBinding.inflate(inflater, container, false)
        val view = binding.root
         
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        binding.imgButtonAdd.setOnClickListener { cambiaImmagine() }

        binding.spinnerDifficolta.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                difficolta = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(context, "Selezionare la difficoltà", Toast.LENGTH_SHORT).show()
            }
        }

        binding.spinnerTipologia.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                tipologia = parent.getItemAtPosition(position).toString()
                tipo_drink = if (tipologia == "Analcolico") "analcolico"
                else "alcolico"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(context, "Selezionare la tipologia", Toast.LENGTH_SHORT).show()
            }
        }

        var ingredienti: ArrayList<String> = arrayListOf()
        binding.ingrediente.setOnClickListener { view ->
            var append = editorIngredienti.text.toString()
            ingredienti.add(append)
            editorIngredienti.text.clear()
            Toast.makeText(context, "Hai aggiunto: $append", Toast.LENGTH_SHORT).show()
        }

        binding.buttonSalvaRicetta.setOnClickListener(View.OnClickListener {

            val titoloString = binding.nomeCocktailEditText.text.toString().trimEnd()

            if (titoloString.isEmpty()) {
                if (ingredienti.isEmpty()) {
                    if (descrizioneAl.isEmpty()) {
                        binding.nomeCocktailEditText.error = "E' richiesto il titolo"
                    }
                    Toast.makeText(context, "Inserire gli ingredienti", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(context, "Inserire il procedimento", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            val ricettaMap: MutableMap<String, Any?> = HashMap()
            ricettaMap["titolo"] = titoloString
            ricettaMap["difficoltà"] = difficolta
            ricettaMap["ingredienti"] = ingredienti
            ricettaMap["descrizione"] = descrizioneAl
            val db = FirebaseFirestore.getInstance()
            db.collection(tipo_drink).document(id).set(ricettaMap).addOnSuccessListener {
                Toast.makeText(context, "Ricetta salvata correttamente", Toast.LENGTH_LONG).show()
                val back = Intent(context, Navigation::class.java)
                startActivity(back)
            }.addOnFailureListener { Toast.makeText(context, "Ricetta non salvata", Toast.LENGTH_SHORT).show() }
        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View,
                                pos: Int, id: Long) {
        val item = parent.getItemAtPosition(pos).toString()
        tipo_drink = if (item == "ANALCOLICO") {
            "analcolico"
        } else {
            "alcolico"
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(context, "Occorre selezionare il tipo di cocktail", Toast.LENGTH_SHORT).show()
    }

    private fun cambiaImmagine() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    private fun caricaImmagine() {
        val pd = ProgressDialog(context)
        pd.setTitle("Caricamento immagine...")
        pd.show()
        val imageRef = storageReference!!.child("$tipo_drink/$id.jpg")
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { pd.dismiss() }
            .addOnFailureListener {
                pd.dismiss()
                Toast.makeText(context, "Impossibile caricare immagine", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.imgButtonAdd.setImageURI(imageUri)
            caricaImmagine()
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(backText: String?)
    }

    //Inner class
    object GenerateRandomString {
        const val DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var RANDOM = Random()
        fun randomString(len: Int): String {
            val sb = StringBuilder(len)
            for (i in 0 until len) {
                sb.append(DATA[RANDOM.nextInt(DATA.length)])
            }
            return sb.toString()
        }
    }

    companion object {
        fun newInstance(): AggiungiCocktailFragment {
            val fragment = AggiungiCocktailFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}