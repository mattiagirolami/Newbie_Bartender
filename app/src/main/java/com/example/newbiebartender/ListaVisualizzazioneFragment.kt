package com.example.newbiebartender

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.newbiebartender.databinding.FragmentListaVisualizzazioneBinding
import com.example.newbiebartender.ui.MyProfileFragment
import com.example.newbiebartender.ui.VisualizzaRicettaFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

open class ListaVisualizzazioneFragment : Fragment() {

    open var db: FirebaseFirestore? = null
    var listAdapter: ListAdapter = ListAdapter(this.context)
    var listaFiltrata: ArrayAdapter<String>? = null
    var titoli = ArrayList<String>()

    var idL = ArrayList<String>()
    var idResultData = ArrayList<String>()
    var resultsData = ArrayList<String>()

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

        binding.ricerca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                listAdapter.filter.filter(s.toString())
            }
        })
        return view
    }

    private fun openFragment(idRecipe: String?, tipoCocktail: String?) {

        val fragment: VisualizzaRicettaFragment = VisualizzaRicettaFragment.newInstance(idRecipe, tipoCocktail)
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.add(R.id.visualizzaCocktailFragment, fragment, "VISUALIZZARECIPE_FRAGMENT").commit()
    }


    inner class ListAdapter(var context: Context?) : BaseAdapter(), Filterable {
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

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val results = FilterResults()
                    if (constraint.isNullOrBlank()) {
                        results.values = titoli
                        results.count = resultsData.size
                        resultsData.clear()
                        resultsData.addAll(titoli)
                    } else {
                        idResultData.clear()
                        val searchString = constraint.toString()
                                .toUpperCase(Locale.ROOT)
                        for (name in titoli) {
                            if (name.toUpperCase(Locale.ROOT).startsWith(searchString)) {
                                if (resultsData.contains(name)) {
                                    if (binding.ricerca.length() == 0) results.values = titoli
                                    resultsData.clear()
                                    break
                                }
                                resultsData.add(name)
                                idResultData.add(idL[titoli.indexOf(name)])
                                results.values = name
                                results.count = resultsData.size
                            }
                        }
                    }
                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    listaFiltrata = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, resultsData)
                    binding.listview.adapter = listaFiltrata
                    notifyDataSetChanged()
                    binding.listview.onItemClickListener =
                            AdapterView.OnItemClickListener { parent, view, position, id ->
                                idRecipe = idResultData[position]
                                openFragment(idRecipe, tipoCocktail)
                            }
                }
            }
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