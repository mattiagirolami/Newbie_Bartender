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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.newbiebartender.Navigation
import com.example.newbiebartender.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

class AggiungiCocktailFragment : Fragment(), OnItemSelectedListener, NumberPicker.OnScrollListener {

    var storage: FirebaseStorage?= null
    private var storageReference: StorageReference ?= null
    var ingredientiAl = ArrayList<String>()
    var adapter: ArrayAdapter<String> ?= null
    var descrizioneAl = java.util.ArrayList<String>()
    var adapter2: ArrayAdapter<String>? = null
    //TODO: vedere se aggiungere qualcosa
    val tipologia = arrayOf("ANALCOLICO", "ALCOLICO")
    var id = GenerateRandomString.randomString(20)
    var titolo: TextInputEditText? = null
    var titoloL: TextInputLayout? = null
    var inserisciImmagine: ImageButton? = null
    var tornaIndietro: ImageButton? = null
    var imageUri: Uri? = null
    var fotoCocktail: ImageView? = null
    var aggiuntaIngrediente: Button? = null
    var aggiuntaDescrizione: Button? = null
    var salvaRicetta: Button? = null
    var aggiungiIngrediente: ListView? = null
    var aggiungiDescrizione: ListView? = null
    var difficolta: String? = null
    var costo: String? = null
    var tipo_drink: String? = null
    var dosiS: String? = null
    var tempo = 0
    var spinnerDosi: Spinner? = null
    var spinnerDifficolta: Spinner? = null
    var spinnerCosto: Spinner? = null
    var numberPicker: NumberPicker? = null



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_aggiungi_cocktail, container, false)
        inserisciImmagine = root.findViewById(R.id.bottonemodificaimmagine)
        titolo = root.findViewById(R.id.titolotext)
        titoloL = root.findViewById(R.id.titolotextLayout)
        fotoCocktail = root.findViewById(R.id.fotoricetta)
        aggiuntaIngrediente = root.findViewById(R.id.button6)
        aggiungiIngrediente = root.findViewById(R.id.aggiungi_ingredienti)
        aggiuntaDescrizione = root.findViewById(R.id.button7)
        aggiungiDescrizione = root.findViewById(R.id.aggiungi_descrizione)
        spinnerDosi = root.findViewById(R.id.spinner_dosi)
        spinnerDifficolta = root.findViewById(R.id.spinner_difficolta)
        spinnerCosto = root.findViewById(R.id.spinner_costo)
        numberPicker = root.findViewById(R.id.numberPicker)
        salvaRicetta = root.findViewById(R.id.button8)
        numberPicker!!.maxValue = 250
        numberPicker!!.minValue = 0
        //numberPicker!!.textSize = 50F
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        val adp = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, tipologia)
        val scelta = AlertDialog.Builder(requireContext())
        scelta.setCancelable(false)
        scelta.setTitle("Scegli il tipo di cocktail")
        val sp = Spinner(context)
        sp.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        sp.adapter = adp
        sp.onItemSelectedListener = this
        scelta.setView(sp)
        scelta.setPositiveButton("SALVA") { dialog, which -> Toast.makeText(context, "Tipo di cocktail salvato correttamente", Toast.LENGTH_SHORT).show() }
        scelta.create().show()
        inserisciImmagine!!.setOnClickListener { cambiaImmagine() }
        aggiuntaIngrediente!!.setOnClickListener { v ->
            val ingrediente = EditText(v.context)
            val inserisciIngrediente = AlertDialog.Builder(v.context)
            inserisciIngrediente.setMessage("INSERISCI IL NUOVO INGREDIENTE")
            inserisciIngrediente.setView(ingrediente)
            inserisciIngrediente.setPositiveButton("INSERISCI") { dialog, which ->
                val ing = ingrediente.text.toString().trim { it <= ' ' }
                ingredientiAl.add(ing)
                adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    ingredientiAl
                )
                aggiungiIngrediente!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
                Toast.makeText(context, "Ingrediente aggiunto", Toast.LENGTH_SHORT).show()
            }
            inserisciIngrediente.create().show()
        }
        aggiuntaDescrizione!!.setOnClickListener { v ->
            val descrizione = EditText(v.context)
            val inserisciDescrizione = AlertDialog.Builder(v.context)
            inserisciDescrizione.setMessage("INSERISCI LA DESCRIZIONE")
            inserisciDescrizione.setView(descrizione)
            inserisciDescrizione.setPositiveButton("INSERISCI") { dialog, which ->
                val proc = descrizione.text.toString()
                descrizioneAl.add(proc)
                adapter2 = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    descrizioneAl
                )
                aggiungiDescrizione!!.adapter = adapter2
                adapter2!!.notifyDataSetChanged()
                Toast.makeText(context, "Procedimento aggiunto", Toast.LENGTH_SHORT).show()
            }
            inserisciDescrizione.create().show()
        }
        aggiungiDescrizione!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            AlertDialog.Builder(requireContext())
                    .setMessage("Sicuro di voler eliminare l'elemento selezionato?")
                    .setPositiveButton("SI") { dialog, which ->
                        descrizioneAl.removeAt(position)
                        adapter2!!.notifyDataSetChanged()
                        Toast.makeText(
                                context,
                                "Descrizione eliminata correttamente",
                                Toast.LENGTH_SHORT
                        ).show()
                    }.setNegativeButton("NO", null).create().show()
            true
        }
        aggiungiIngrediente!!.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, id ->
                AlertDialog.Builder(requireContext())
                    .setMessage("Sicuro di voler eliminare l'elemento selezionato?")
                    .setPositiveButton("SI") { dialog, which ->
                        ingredientiAl.removeAt(position)
                        adapter!!.notifyDataSetChanged()
                        Toast.makeText(
                            context,
                            "Ingrediente eliminato correttamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.setNegativeButton("NO", null).create().show()
                true
            }
        spinnerDosi!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                dosiS = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(context, "Occorre selezionare la dose", Toast.LENGTH_SHORT).show()
            }
        }
        spinnerDifficolta!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                difficolta = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(context, "Occorre selezionare la difficoltà", Toast.LENGTH_SHORT).show()
            }
        }
        spinnerCosto!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                costo = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(context, "Occorre selezionare il costo", Toast.LENGTH_SHORT).show()
            }
        }
        numberPicker!!.setOnValueChangedListener { picker, oldVal, newVal ->
            tempo = picker.value
        }
        salvaRicetta!!.setOnClickListener(View.OnClickListener {
            val titoloString = titolo!!.text.toString().trimEnd()
            if (titoloString.isEmpty()) {
                if (ingredientiAl.isEmpty()) {
                    if (descrizioneAl.isEmpty()) {
                        titoloL!!.error = "E' richiesto il titolo"
                    }
                    Toast.makeText(context, "Inserire gli ingredienti", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(context, "Inserire il procedimento", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val ricettaMap: MutableMap<String, Any?> = HashMap()
            ricettaMap["titolo"] = titoloString
            ricettaMap["difficoltà"] = difficolta
            ricettaMap["costo"] = costo
            ricettaMap["tempo"] = tempo
            ricettaMap["dosi"] = dosiS
            ricettaMap["ingredienti"] = ingredientiAl
            ricettaMap["descrizione"] = descrizioneAl
            val db = FirebaseFirestore.getInstance()
            db.collection(tipo_drink!!).document(id).set(ricettaMap).addOnSuccessListener {
                Toast.makeText(context, "Ricetta salvata correttamente", Toast.LENGTH_LONG).show()
                val back = Intent(context, Navigation::class.java)
                startActivity(back)
            }.addOnFailureListener { Toast.makeText(context, "Ricetta non salvata", Toast.LENGTH_SHORT).show() }
        })
        return root
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
            fotoCocktail!!.setImageURI(imageUri)
            caricaImmagine()
        }
    }

    override fun onScrollStateChange(view: NumberPicker, scrollState: Int) {}
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