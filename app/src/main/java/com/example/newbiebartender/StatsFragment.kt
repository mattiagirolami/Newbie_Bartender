package com.example.newbiebartender

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiebartender.databinding.FragmentStatsBinding
import com.google.firebase.firestore.*

class StatsFragment : Fragment(), MyAdapter.OnItemClickListener {

    //private lateinit var recyclerView: RecyclerView
    private lateinit var cocktailArrayList : ArrayList<CocktailModel>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db : FirebaseFirestore


    private  var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.myRecyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.myRecyclerView.setHasFixedSize(true)

        //binding. myRecyclerView.addOnItemClickListener

        cocktailArrayList = arrayListOf()

        myAdapter = MyAdapter(requireContext(), cocktailArrayList, this)

        binding.myRecyclerView.adapter = myAdapter

        EventChangeListener()

        return view
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("cocktail").orderBy("mediaValutazioni", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("mediaValutazioni", 4.0).whereLessThanOrEqualTo("mediaValutazioni", 5.0)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if(error != null){
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for(doc: DocumentChange in value?.documentChanges!!){

                        if(doc.type == DocumentChange.Type.ADDED){

                            cocktailArrayList.add(doc.document.toObject(CocktailModel::class.java))
                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
            })
    }

    override fun onItemClick(position: Int) {
        val clickedItem = cocktailArrayList[position]
        var id = clickedItem.id
        var tipoCocktail = clickedItem.tipoRicetta
        var bundle = bundleOf("idRicetta" to id, "tipoCocktail" to tipoCocktail)
        binding.root.findNavController().navigate(R.id.action_navigation_stats_to_visualizzaRicettaCocktail_frag, bundle)


    }

}