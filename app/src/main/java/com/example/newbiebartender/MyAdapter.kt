package com.example.newbiebartender

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MyAdapter(private var context: Context,
                private val cocktailList: ArrayList<CocktailModel>,
                private val listener: OnItemClickListener)
    : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    private lateinit var storageReference : StorageReference


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // Eseguo l'inflate del layout dei singoli componenti della RecyclerView
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.stats_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //Inserisco i dati dei cocktail all'interno degli elementi della RecyclerView
        val cocktail: CocktailModel = cocktailList[position]
        holder.titolo.text = cocktail.titolo
        holder.autore.text = "Ricetta di ${cocktail.autore}"
        holder.ratingBar.rating = cocktail.mediaValutazioni!!.toFloat()

        storageReference = FirebaseStorage.getInstance().reference
        storageReference
            .child("${cocktail.tipoRicetta}/${cocktail.id}.jpg").downloadUrl
            .addOnSuccessListener { uri ->
                cocktail.immagine = uri.toString()
            }

        // È stato inserito un delay di 10 secondi nel recupero delle immagini dal database perchè
        // il recupero delle immagini da Firebase Storage può essere lento e alcune immagini venivano
        // sostituite da drink_default prima di poter essere recuperate.
        Handler().postDelayed({
            if(cocktail.immagine == null) {
                holder.immagine.setImageResource(R.drawable.drink_default)
            } else {
                Glide.with(context).load(cocktail.immagine).into(holder.immagine)
            }
        }, 10000)



    }

    override fun getItemCount(): Int {
        return cocktailList.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        // Prendo i singoli elementi di layout delle card utilizzate nella RecyclerView
        val titolo: TextView = itemView.findViewById(R.id.post_title)
        val autore: TextView = itemView.findViewById(R.id.post_byuser)
        val immagine: ImageView = itemView.findViewById(R.id.post_image)
        val ratingBar: RatingBar = itemView.findViewById(R.id.post_rating_bar)

        init {
            itemView.setOnClickListener(this)
        }

        // Funzione che viene eseguita al click dell'utente su un elemento della RecyclerView
        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION)  listener.onItemClick(position)
            }
        }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}