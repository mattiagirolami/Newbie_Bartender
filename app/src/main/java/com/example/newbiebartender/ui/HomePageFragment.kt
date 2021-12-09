package com.example.newbiebartender.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.newbiebartender.ListaVisualizzazioneFragment
import com.example.newbiebartender.R


class HomePageFragment : Fragment() {

    var analcolici: CardView ?= null
    var alcolici: CardView ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home_page, container, false)
        analcolici = root.findViewById(R.id.analcolicoCard)
        alcolici = root.findViewById(R.id.alcoliciCard)
        analcolici!!.setOnClickListener { openFragment("analcolico") }
        //analcolici!!.setOnClickListener {Navigation.findNavController(requireView()).navigate(R.id.listaVisualizzazioneanalcolici_frag)}
        alcolici!!.setOnClickListener { openFragment("alcolico") }
        return root
    }


    /*
    override fun onClick(view: View){
        Navigation.findNavController(view).navigate(R.id.visualizzaCocktailFragment)
    }*/

    private fun openFragment(tipoCocktail: String?) {
        val fragment: ListaVisualizzazioneFragment = ListaVisualizzazioneFragment.newInstance(tipoCocktail)
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.add(R.id.listaVisualizzazione, fragment, "LISTAVISUALIZZAZIONE FRAGMENT").commit()
    }

}