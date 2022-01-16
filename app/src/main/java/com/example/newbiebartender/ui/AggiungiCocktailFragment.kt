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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList


class AggiungiCocktailFragment : Fragment(), OnItemSelectedListener {


    private var _binding: FragmentAggiungiCocktailBinding? = null
    private val binding get() = _binding!!

    var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    var id = GenerateRandomString.randomString(20)
    var imageUri: Uri? = null
    var difficolta: String? = null

    var auth = FirebaseAuth.getInstance().currentUser

    var tipologia: String? = null
    lateinit var tipo_drink: String

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentAggiungiCocktailBinding.inflate(inflater, container, false)
        val view = binding.root

        var user: DocumentSnapshot? = null

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        FirebaseFirestore.getInstance().collection("users")
                .document(auth!!.email!!).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user = task.result
                    }
                }

        // Eseguo la funzione cambiaImmagine se clicco sull'Image Button per aggiungere la foto
        binding.imgButtonAdd.setOnClickListener { cambiaImmagine() }

        // Recupera la difficoltà selezionata tramite lo spinner
        binding.spinnerDifficolta.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                difficolta = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(context, "Selezionare la difficoltà", Toast.LENGTH_SHORT).show()
            }
        }


        // Recupero la tipologia del cocktail selezionata
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


        val preferiti: ArrayList<String> = arrayListOf()

        val valutazioni: ArrayList<Map<String, *>> = ArrayList<Map<String, *>>()


        // Aggiungo ingredienti e quantità del cocktail, che verrano inseriti all interno di un'ArrayList di String
        val ingredienti: ArrayList<String> = arrayListOf()
        val editorIngredienti = binding.ingrediente

        // Cliccando sul button per aggiungere gli ingredienti, quello presente nell'editText verrà
        // inserito nell'arrayList e l'editText verrà svuotato
        binding.buttonAggiugniIng.setOnClickListener { view ->
            val append = editorIngredienti.text.toString().toLowerCase(Locale.ROOT)
            ingredienti.add(append)
            editorIngredienti.text.clear()
            Toast.makeText(context, "Hai aggiunto:  $append", Toast.LENGTH_SHORT).show()
        }

        // Premendo sul button Salva il cocktail verrà salvato all'interno del database
        binding.buttonSalvaRicetta.setOnClickListener(View.OnClickListener {

            val titoloString = binding.nomeCocktailEditText.text.toString().trim()

            val descrizione = binding.editTextTextMultiline.text.toString().trim()

            // Controllo se i campi non sono vuoti
            if (titoloString.isEmpty()) {
                if (ingredienti.isEmpty()) {
                    if (descrizione.isEmpty()) {
                        binding.nomeCocktailEditText.error = "Inserire la descrizione"
                    }
                    Toast.makeText(context, "Inserire almeno un ingrediente", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(context, "Inserire il nome del cocktail", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            // Creo una Map con tutte le informazioni inserite e la carico sul database
            val ricettaMap: MutableMap<String, Any?> = HashMap()
            ricettaMap["id"] = id
            ricettaMap["titolo"] = titoloString
            ricettaMap["difficoltà"] = difficolta
            ricettaMap["ingredienti"] = ingredienti
            ricettaMap["descrizione"] = descrizione
            ricettaMap["preferiti"] = preferiti
            ricettaMap["autore"] = user!!["username"].toString()
            ricettaMap["tipoRicetta"] = tipo_drink
            ricettaMap["valutazioni"] = valutazioni
            ricettaMap["mediaValutazioni"] = 0.0

            val db = FirebaseFirestore.getInstance()
            db.collection("cocktail").document(id).set(ricettaMap).addOnSuccessListener {
                Toast.makeText(context, "Ricetta salvata correttamente", Toast.LENGTH_LONG).show()
                val back = Intent(context, Navigation::class.java)
                startActivity(back)
            }.addOnFailureListener {
                Toast.makeText(context, "Ricetta non salvata", Toast.LENGTH_SHORT).show()
            }


        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun cambiaImmagine() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    // Funzione che carica l'immagine su Firebase Storage
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

    // Object che contiene una funzione, la quale mi permette di generare una stringa random di
    // 20 caratteri/numeri
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { }

    override fun onNothingSelected(parent: AdapterView<*>?) { }

}