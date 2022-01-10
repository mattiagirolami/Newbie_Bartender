package com.example.newbiebartender

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newbiebartender.databinding.FragmentFavouritesBinding
import com.example.newbiebartender.ui.MyProfileFragment
import com.example.newbiebartender.ui.VisualizzaRicettaFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FavouritesFragment : Fragment() {

    private lateinit var binding : FragmentFavouritesBinding
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var auth: FirebaseUser

    var listAdapter: ListAdapter = ListAdapter(this.context)
    var titoli = ArrayList<String>()
    var idList = ArrayList<String>()

    var db = FirebaseFirestore.getInstance()

    var titolo: String? = null
    var id: String? = null
    var idRecipe: String? = null
    var tipoCocktail: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        setupTBwithNavigation()

        auth = FirebaseAuth.getInstance().currentUser!!

        showAlcolico()
        showAnalcolico()

        return binding.root
    }


    private fun showAlcolico() {

        db.collection("alcolico").whereArrayContains("preferiti", auth.email!!.toString())
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        for(doc in task.result!!) {
                            titolo = doc["titolo"] as String?
                            id = doc.id
                            titoli.add(titolo!!)
                            idList.add(id!!)
                        }
                        binding.listviewFavourites.adapter = listAdapter
                    } else {
                        val intent = Intent(this.context, MyProfileFragment::class.java)
                        startActivity(intent)
                    }
                }

        binding.listviewFavourites.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            idRecipe = idList[position]
            openFragment(idRecipe!!, "alcolico")

        }
    }

    private fun showAnalcolico() {
        db.collection("analcolico").whereArrayContains("preferiti", auth.email!!.toString())
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        for(doc in task.result!!) {
                            titolo = doc["titolo"] as String?
                            id = doc.id
                            titoli.add(titolo!!)
                            idList.add(id!!)
                        }
                        binding.listviewFavourites.adapter = listAdapter
                    } else {
                        val intent = Intent(this.context, MyProfileFragment::class.java)
                        startActivity(intent)
                    }
                }

        binding.listviewFavourites.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            idRecipe = idList[position]
            openFragment(idRecipe!!, "analcolico")

        }
    }

    private fun openFragment(idRecipe: String?, tipoCocktail: String?) {

        val fragment: VisualizzaRicettaFragment = VisualizzaRicettaFragment.newInstance(idRecipe, tipoCocktail)
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.add(R.id.visualizzaCocktailFragment, fragment, "VISUALIZZARECIPE_FRAGMENT").commit()

    }

    private fun setupTBwithNavigation() {
        toolbar = binding.toolbarFav
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
                }
    }

    inner class ListAdapter (var context: Context?) : BaseAdapter() {
        override fun getCount(): Int {
            return titoli.size
        }

        override fun getItem(position: Int): Any {
            return titoli[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = inflate(context, R.layout.textviewlist, null)
            val titoloCocktail = view.findViewById<TextView>(R.id.titoloCocktail)
            titoloCocktail.text = titoli[position]
            return view
        }

    }

    companion object {

        fun newInstance(i: Int, s: String): FavouritesFragment {
            return FavouritesFragment()
        }
    }


}