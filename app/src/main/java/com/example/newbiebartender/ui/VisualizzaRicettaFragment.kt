package com.example.newbiebartender.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
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
import java.util.*
import kotlin.collections.HashMap


class VisualizzaRicettaFragment : ListaVisualizzazioneFragment() {

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

    private lateinit var documentSnapshot : DocumentSnapshot


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {

            // Recupero i parametri passati come parametro nel bundle creato nel fragment che rimanda a questo
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

        // Funzione che mi permette di tornare al fragment precedente
        setupToolbarWithNavigation()

        // Listener sullo spinner che mi permette di scegliere la valutazione da dare al cocktail
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

        docref = db!!.collection("cocktail").document(idRicetta!!)

        // Recupero tutte le informazioni relative al cocktail selezionato e le inserisco all'interno dei loro campi
        docref!!.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val document = task.result

                if (document!!.exists()) {

                    documentSnapshot = document

                    // Inserisco all'interno del campo Rating la media delle valutazioni del cocktail,
                    // calcolata tramite la funzione calculateAvg()
                    binding.ratingTofill.text = calculateAvg(document).toString() + " su 5"

                    binding.nomeCockTw.text = document["titolo"].toString()
                    binding.difficoltaTofill.text = document["difficoltà"].toString()
                    binding.procedimentoView.text = document["descrizione"].toString().capitalize(Locale.ROOT)
                    binding.tipologiaTofill.text = tipoCocktail.toString().capitalize(Locale.ROOT)
                    binding.recipeByUser.text = "Ricetta di ${document["autore"].toString()}"

                    val arrayIngr = document["ingredienti"] as ArrayList<String>?
                    var ingreds = ""
                    if (arrayIngr != null){
                        for (ing in arrayIngr) {
                            ingreds = ingreds+ing.capitalize()+"\n"
                        }
                    }
                    binding.ingredienti.text = ingreds

                    // Se il cocktail è stato già valutato dall'utente, esso non potrà valutarlo una seconda volta.
                    // Le componenti grafiche dedite all'inserimento del rating vengono quindi nascoste ma
                    // non prima di aver controllato la presenza della recensione da parte di quell'utente sul database (attraverso la funzione checkRatings)
                    if(checkRatings(document)){
                        binding.btnSalvaValutazione.visibility = View.GONE
                        binding.ratingTw.visibility = View.GONE
                        binding.spinnerValutazione.visibility = View.GONE
                    }

                    // Tramite la funzione checkFavourites controllo se l'utente ha inserito o meno il cocktail selezionato nei suoi preferiti
                    //
                    // Se il cocktail è presente nei preferiti verrà mostrata una "stella piena" e al click sulla stella stessa
                    // verrà eseguita la funzione removeFav(), che rimuoverà il cocktail dai preferiti.
                    // Successivamente viene richiamato il fragment stesso per poter mostrare l' icona aggiornata
                    if (checkFavourite(document)){
                        binding.showRecipeToolbar.menu.getItem(0)
                            .setIcon(R.drawable.ic_full_star)
                        binding.showRecipeToolbar.menu.getItem(0)
                            .setOnMenuItemClickListener {

                                removeFav()
                                Toast.makeText(requireContext(), "Rimosso dai preferiti", Toast.LENGTH_SHORT).show()
                                val bundle = bundleOf("idRicetta" to idRicetta, "tipoCocktail" to tipoCocktail)
                                binding.root.findNavController().navigate(R.id.action_visualizzaRicettaCocktail_frag_self, bundle)
                                true
                            }
                    } else {

                        // Se il cocktail non è presente nei preferiti verrà mostrata una "stella vuota" e al click sulla stella stessa
                        // verrà eseguita la funzione addToFav(), che aggiungerà il cocktail ai preferiti.
                        // Successivamente viene richiamato il fragment stesso per poter mostrare l' icona aggiornata

                        binding.showRecipeToolbar.menu.getItem(0)
                            .setIcon(R.drawable.ic_empty_star)
                        binding.showRecipeToolbar.menu.getItem(0)
                            .setOnMenuItemClickListener {
                                addToFav()
                                Toast.makeText(requireContext(), "Aggiunto ai preferiti", Toast.LENGTH_SHORT).show()
                                var bundle = bundleOf("idRicetta" to idRicetta, "tipoCocktail" to tipoCocktail)
                                binding.root.findNavController().navigate(R.id.action_visualizzaRicettaCocktail_frag_self, bundle)
                                true
                            }
                    }

                    // Carico l'immagine del cocktail nell'ImageView, presa da Firebase Storage
                    // Se l'immagine del cocktail non è presente nello storage, ne viene inserita una di default
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

        // Cliccando sul tasto Salva, la valutazione inserita verrà caricata all'interno del documento corrispondente al cocktail sul database
        // Viene inserita nel db la coppia email-voto per poter associare ad ogni valutazione l'user che l'ha inserita
        binding.btnSalvaValutazione.setOnClickListener {
            val mVal: MutableMap<String, Any?> = HashMap()
            mVal["email"] = auth.email.toString()
            mVal["voto"] = valutazione
            FirebaseFirestore.getInstance().collection("cocktail")
                .document(idRicetta!!)
                .update("valutazioni", FieldValue.arrayUnion(mVal))
            Toast.makeText(context, "Hai inserito una valutazione di $valutazione/5", Toast.LENGTH_SHORT).show()

            // Funzione utilizzata per calcolare la media
            calculateAvg(documentSnapshot)

            // Richiamo il fragment stesso per poterlo refreshare e visualizzare la media delle valutazioni aggiornata
            val bundle = bundleOf("idRicetta" to idRicetta, "tipoCocktail" to tipoCocktail)
            binding.root.findNavController().navigate(R.id.action_visualizzaRicettaCocktail_frag_self, bundle)
        }

        return view
    }

    private fun setupToolbarWithNavigation() {
        val toolbar = binding.showRecipeToolbar
        toolbar.setNavigationOnClickListener {
            val bundle = bundleOf("idRicetta" to idRicetta, "tipoCocktail" to tipoCocktail)
            findNavController().navigate(R.id.action_visualizzaRicettaCocktail_frag_to_listaVisualizzazione_frag, bundle)
        }
    }

    // Funzione che recupera tutte le valutazioni relative al cocktail nel database,
    // le somma e successivamente le divide per il numero delle valutazioni, calcolando la media
    private fun calculateAvg(document: DocumentSnapshot) : Float {

        var somma = 0
        var counter = 0
        var media = 0F

        for(valutazione in document["valutazioni"] as ArrayList<HashMap<String, String>>){
            somma += valutazione["voto"]!!.toInt()
            counter++
        }


        media = if(somma == 0) 0F
        else (somma/counter).toFloat()

        // Salvo la media delle valutazioni nel database
        FirebaseFirestore.getInstance().collection("cocktail")
                .document(idRicetta!!)
                .update("mediaValutazioni", media)

        return media

    }

    // Funzione che controlla se è presente la valutazione dell'utente nel database
    private fun checkRatings(document: DocumentSnapshot) : Boolean {
        for(valutazione in document["valutazioni"] as ArrayList<HashMap<String, String>>){
            if (valutazione["email"] == auth.email.toString()) isRated = true
        }
        return isRated
    }

    // Funzione che controlla se l'utente ha inserito il cocktail nei suoi preferiti
    private fun checkFavourite(document: DocumentSnapshot) : Boolean {

        for(favourite in document["preferiti"] as ArrayList<String>) {
            if(favourite == auth.email) {
                isFav = true
            }
        }
        return isFav
    }

    // Funzione che aggiunge il cocktail nei preferiti,
    // inserendo l'email dell'utente all'interno del campo (array di String) preferiti nel database
    private fun addToFav() {

        isFav = true
        FirebaseFirestore.getInstance()
                .collection("cocktail")
                .document(idRicetta!!)
                .update("preferiti", FieldValue.arrayUnion(auth.email.toString()))

    }

    // Funzione che rimuove il cocktail dai preferiti,
    // rimuovendo l'email dell'utente dal campo (array di String) preferiti nel database
    private fun removeFav() {
        isFav = false

        FirebaseFirestore.getInstance()
                .collection("cocktail")
                .document(idRicetta!!)
                .update("preferiti", FieldValue.arrayRemove(auth.email.toString()))

    }


}