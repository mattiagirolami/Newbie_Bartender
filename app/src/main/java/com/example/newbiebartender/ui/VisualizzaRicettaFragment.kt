package com.example.newbiebartender.ui
/*
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.newbiebartender.ListaVisualizzazioneFragment
import com.example.newbiebartender.Navigation
import com.example.newbiebartender.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class VisualizzaRicettaFragment : ListaVisualizzazioneFragment() {

    private var storageReference: StorageReference ?= null
    //private var ingredientiL: List<String>? = ArrayList()
    private var ingredientiL: List<String>? = ArrayList()
    private var descrizioneL: List<String> = ArrayList()
    //private val mListener: OnFragmentInteractionListener? = null
    var adapterIngredienti: ArrayAdapter<String>? = null
    var adapterDescrizione: ArrayAdapter<String>? = null
    override var db: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var scrollView: ScrollView? = null

    //TODO: controllare questo problema
    override var titolo: String? = null

    var difficolta: TextView? = null
    var costo: TextView? = null
    var tempo: TextView? = null
    var dosi: TextView? = null
    var docref: DocumentReference? = null
    var fotodrink: ImageView? = null
    var descrizione: ListView? = null
    var ingredienti: ListView? = null
    var tornaIndietro: ImageButton? = null

    var idRicetta: String? = null
    override var tipoCocktail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            idRicetta = requireArguments().getString("idRicetta")
            tipoCocktail = requireArguments().getString("tipoCocktail")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_visualizza_ricetta, container, false)
        scrollView = root.findViewById(R.id.scrollvr)
        titolo = root.findViewById(R.id.nomecocktail)
        dosi = root.findViewById(R.id.dosi)
        costo = root.findViewById(R.id.costo)
        difficolta = root.findViewById(R.id.difficolta)
        tempo = root.findViewById(R.id.tempo)
        fotodrink = root.findViewById(R.id.fotodeldrink)
        descrizione = root.findViewById(R.id.visualizza_descrizione)
        ingredienti = root.findViewById(R.id.visualizza_ingredienti)
        tornaIndietro = root.findViewById(R.id.tornaIndietro)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        docref = db!!.collection(tipoCocktail!!).document(idRicetta!!)

        docref!!
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document!!.exists()) {
                            titolo = document["titolo"].toString()
                            costo!!.text = document["costo"].toString()
                            dosi!!.text = document["dosi"].toString()
                            difficolta!!.text = document["difficoltà"].toString()
                            tempo!!.text = document["tempo"].toString()

                            storageReference!!.child("$tipoCocktail/$idRicetta.jpg").downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                Glide.with(requireContext()).load(imageUrl).into(fotodrink!!)
                            }
                            //TODO: risolvere questo problema
                            ingredientiL = (document["ingredienti"] as List<String>?)!!
                            adapterIngredienti = ArrayAdapter(requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    ingredientiL!!)
                            ingredienti!!.adapter = adapterIngredienti
                            adapterIngredienti!!.notifyDataSetChanged()
                            descrizioneL = (document["descrizione"] as List<String>?)!!
                            adapterDescrizione = ArrayAdapter(requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    descrizioneL)
                            descrizione!!.adapter = adapterDescrizione
                            adapterDescrizione!!.notifyDataSetChanged()
                        }
                    }
                }
        scrollView!!.fullScroll(ScrollView.FOCUS_DOWN)
        scrollView!!.fullScroll(ScrollView.FOCUS_UP)
        tornaIndietro!!.setOnClickListener {
            val back = Intent(context, Navigation::class.java)
            startActivity(back)
        }
        return root
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

 */
