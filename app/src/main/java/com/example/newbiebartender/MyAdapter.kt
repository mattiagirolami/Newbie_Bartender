package com.example.newbiebartender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val cocktailList : ArrayList<Cocktail>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {

        val cocktail : Cocktail = cocktailList[position]
        holder.nomeCocktail.text = cocktail.nome
    }

    override fun getItemCount(): Int {
        return cocktailList.size
    }

    public class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val nomeCocktail : TextView = itemView.findViewById(R.id.list_item_nome)

    }

}