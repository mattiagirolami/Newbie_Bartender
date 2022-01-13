package com.example.newbiebartender.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.newbiebartender.R
import com.example.newbiebartender.databinding.FragmentHomePageBinding


class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding

    var analcolico = "analcolico"
    var alcolico = "alcolico"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)


        binding.analcolicoCard.setOnClickListener {
            val bundle = bundleOf("tipoCocktail" to analcolico)
            binding.root.findNavController().navigate(R.id.action_navigation_homepage_to_listaVisualizzazione_frag, bundle)

        }

        binding.alcoliciCard.setOnClickListener {
            val bundle = bundleOf("tipoCocktail" to alcolico)
            binding.root.findNavController().navigate(R.id.action_navigation_homepage_to_listaVisualizzazione_frag, bundle)
        }
        return binding.root
    }

}