package com.example.newbiebartender

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MyAdapter(private var context: Context, private val cocktailList: ArrayList<CocktailModel>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    private lateinit var storageReference : StorageReference


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.stats_row, parent, false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        //TODO: mettere foto
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

        if(cocktail.immagine == null) {
            holder.immagine.setImageResource(R.drawable.drink_default)
        } else {
            Glide.with(context).load(cocktail.immagine).into(holder.immagine)
        }


    }

    override fun getItemCount(): Int {
        return cocktailList.size
    }

    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val titolo: TextView = itemView.findViewById(R.id.post_title)
        val autore: TextView = itemView.findViewById(R.id.post_byuser)
        val immagine: ImageView = itemView.findViewById(R.id.post_image)
        val ratingBar: RatingBar = itemView.findViewById(R.id.post_rating_bar)



    }

}