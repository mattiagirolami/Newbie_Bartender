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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.newbiebartender.databinding.FragmentFavouritesBinding
import com.example.newbiebartender.ui.MyProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FavouritesFragment : Fragment() {

    private lateinit var binding : FragmentFavouritesBinding
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var auth: FirebaseUser

    var listAdapter: ListAdapter = ListAdapter(this.context)
    var listAdapterAnalcolico : ListAdapterAnalcolico = ListAdapterAnalcolico(this.context)
    var titoli = ArrayList<String>()
    var idList = ArrayList<String>()

    var tipoloString : String? = null
    var tipologia = ArrayList<String>()

    var db = FirebaseFirestore.getInstance()

    var titolo: String? = null
    var id: String? = null
    var idRecipe: String? = null
    var tipoCocktail: String? = null

    var count = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        setupTBwithNavigation()

        auth = FirebaseAuth.getInstance().currentUser!!

        showAlcolico()
        showAnalcolico()

        binding.listviewFavourites.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->


            /*idRecipe = idList[position]
            val tipoRicetta = tipologia[position]
            openFragment(idRecipe!!, tipoRicetta)
             */

            //fragmentManager?.beginTransaction()?.remove(FavouritesFragment())?.commitAllowingStateLoss()

                val bundle = bundleOf("idRicetta" to idList[position], "tipoCocktail" to tipologia[position])
                binding.root.findNavController()
                        .navigate(R.id.action_favouriteCocktailFragment__to_visualizzaRicettaCocktail_frag, bundle)


        }

        return binding.root
    }


    private fun showAlcolico() {

        var count = 0

        db.collection("alcolico").whereArrayContains("preferiti", auth.email!!.toString())
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        for(doc in task.result!!) {
                            titolo = doc["titolo"] as String?
                            id = doc.id
                            titoli.add(titolo!!)
                            idList.add(id!!)

                            tipoloString = doc["tipoRicetta"] as String
                            tipologia.add(tipoloString!!)
                            count ++
                        }
                        binding.listviewFavourites.adapter = listAdapter
                    } else {
                        val intent = Intent(this.context, MyProfileFragment::class.java)
                        startActivity(intent)
                    }
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
                        binding.listviewFavourites.adapter = listAdapterAnalcolico
                    } else {
                        val intent = Intent(this.context, MyProfileFragment::class.java)
                        startActivity(intent)
                    }
        }
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
            val view = inflate(getContext(), R.layout.textviewlist, null)
            val titoloCocktail = view.findViewById<TextView>(R.id.titoloCocktail)
            titoloCocktail.text = titoli[position]
            return view
        }
    }

    inner class ListAdapterAnalcolico (var context: Context?) : BaseAdapter(){
        override fun getCount(): Int {
            return titoli.size
        }

        override fun getItem(position: Int): Any {
            return titoli[position+ListAdapter(context).count]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = inflate(getContext(), R.layout.textviewlist, null)
            val titoloCocktail = view.findViewById<TextView>(R.id.titoloCocktail)
            titoloCocktail.text = titoli[position]
            return view
        }

    }

}