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
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newbiebartender.databinding.FragmentListaVisualizzazioneBinding
import com.example.newbiebartender.ui.MyProfileFragment
import com.example.newbiebartender.ui.VisualizzaRicettaFragment
import com.google.firebase.firestore.FirebaseFirestore

open class ListaVisualizzazioneFragment : Fragment() {

    open var db: FirebaseFirestore? = null
    var listAdapter: ListAdapter = ListAdapter(this.context)
    var titoli = ArrayList<String>()

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    var idL = ArrayList<String>()

    open var tipoCocktail: String? = null

    open var titolo: String? = null

    var id: String? = null
    var idRecipe: String? = null

    private  var _binding: FragmentListaVisualizzazioneBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments!=null){
            tipoCocktail = requireArguments().getString("tipoCocktail")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentListaVisualizzazioneBinding.inflate(inflater, container, false)
        val view = binding.root

        db = FirebaseFirestore.getInstance()

        binding.listaToolbar.title = tipoCocktail

        setupToolbarWithNavigation()

        db!!.collection(tipoCocktail!!)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (doc in task.result!!) {
                        titolo = doc["titolo"] as String?
                        //titolo!!.text = doc["titolo"] as String?
                        id = doc.id
                        titoli.add(titolo!!)
                        idL.add(id!!)
                    }
                    binding.listview.adapter = listAdapter
                } else {
                    val intent = Intent(this.context, MyProfileFragment::class.java)
                    startActivity(intent)
                }
            }

        binding.listview.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            idRecipe = idL[position]
            openFragment(idRecipe, tipoCocktail)
        }

        return view
    }

    private fun setupToolbarWithNavigation() {
        toolbar = binding.listaToolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun openFragment(idRecipe: String?, tipoCocktail: String?) {

        val fragment: VisualizzaRicettaFragment = VisualizzaRicettaFragment.newInstance(idRecipe, tipoCocktail)
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.add(R.id.visualizzaCocktailFragment, fragment, "VISUALIZZARECIPE_FRAGMENT").commit()
    }


    inner class ListAdapter(var context: Context?) : BaseAdapter() {
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

    companion object {
        fun newInstance(tipoCocktail: String?) : ListaVisualizzazioneFragment  {
            val fragment = ListaVisualizzazioneFragment()
            val args = Bundle()
            args.putString("tipoCocktail", tipoCocktail)
            fragment.arguments = args
            return fragment
        }
    }
}