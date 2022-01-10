package com.example.newbiebartender

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.example.newbiebartender.databinding.FragmentFavBinding
import com.google.firebase.auth.FirebaseUser


class FavFragment : Fragment() {

    private lateinit var binding: FragmentFavBinding
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private lateinit var auth: FirebaseUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentFavBinding.inflate(inflater, container, false)

        setupTBwithNavigation()

        return binding.root
    }

    private fun setupTBwithNavigation() {
        toolbar = binding.toolbarFav
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}