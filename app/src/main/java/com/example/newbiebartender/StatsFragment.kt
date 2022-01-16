package com.example.newbiebartender

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiebartender.databinding.FragmentStatsBinding
import com.google.firebase.firestore.*

class StatsFragment : Fragment(), MyAdapter.OnItemClickListener {

    private lateinit var cocktailArrayList : ArrayList<CocktailModel>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db : FirebaseFirestore


    private  var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Setto il Linear Layout al layoutManager della RecyclerView
        binding.myRecyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.myRecyclerView.setHasFixedSize(true)


        cocktailArrayList = arrayListOf()

        myAdapter = MyAdapter(requireContext(), cocktailArrayList, this)

        binding.myRecyclerView.adapter = myAdapter

        // Eseguo la funzione EventChangeListener
        EventChangeListener()

        return view
    }


    // Funzione che recupera e mostra i cocktail più apprezzati (con una valutazione compresa tra 4 e 5 stelle)
    // all'interno della recylerView

    private fun EventChangeListener() {

        // Mi collego a Firestore per recuperare i cocktail con una buona valutazione dalla collection dedicata, ordinati in modo decrescente
        // in base alla loro media delle valutazioni
        db = FirebaseFirestore.getInstance()
        db.collection("cocktail").orderBy("mediaValutazioni", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("mediaValutazioni", 4.0).whereLessThanOrEqualTo("mediaValutazioni", 5.0)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    // Controllo se ci sono errori relativi al database
                    if(error != null){
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for(doc: DocumentChange in value?.documentChanges!!){

                        if(doc.type == DocumentChange.Type.ADDED){

                            // Vengono aggiunti i cocktail risultanti dalla query nell'arrayList dedicato
                            cocktailArrayList.add(doc.document.toObject(CocktailModel::class.java))

                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
            })
    }

    // Funzione che ci permette di visualizzare i dettagli delle ricette visualizzate nella Recyler View
    // cliccando sulle card che contengono i cocktail
    override fun onItemClick(position: Int) {
        val clickedItem = cocktailArrayList[position]
        val id = clickedItem.id
        val tipoCocktail = clickedItem.tipoRicetta

        // Creo un bundle contenente l'id della ricetta del cocktail e il tipo del cocktail
        // Il bundle verrà passato come parametro al metodo navigate() e i due attributi verranno poi recuperati nel fragment di destinazione
        val bundle = bundleOf("idRicetta" to id, "tipoCocktail" to tipoCocktail)
        binding.root.findNavController().navigate(R.id.action_navigation_stats_to_visualizzaRicettaCocktail_frag, bundle)


    }

}