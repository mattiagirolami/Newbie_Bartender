package com.example.newbiebartender

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

open class ListaVisualizzazioneFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cocktailArrayList: ArrayList<Cocktail>
    private lateinit var myAdapter: MyAdapter
    lateinit var db: FirebaseFirestore

    private var layoutManager : RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<MyAdapter.MyViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_lista_visualizzazione, container, false)

        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        cocktailArrayList = arrayListOf()

        myAdapter = MyAdapter(cocktailArrayList)

        recyclerView.adapter = myAdapter

        EventChangeListener()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = MyAdapter(cocktailArrayList)
        }
    }

    private fun EventChangeListener(){

        db = FirebaseFirestore.getInstance()
        db.collection("analcolico")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(
                    value : QuerySnapshot?,
                    error : FirebaseFirestoreException?
                ){
                    if(error != null){
                        Log.e("Firestore error", error.message.toString())
                        return
                    }

                    for (dc : DocumentChange in value?.documentChanges!!){

                        if (dc.type == DocumentChange.Type.ADDED){

                            cocktailArrayList.add(dc.document.toObject(Cocktail::class.java))
                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
            })

    }


}
