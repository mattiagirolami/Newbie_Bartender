package com.example.newbiebartender

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.newbiebartender.ui.MyProfileFragment
import com.example.newbiebartender.ui.VisualizzaRicettaFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

open class ListaVisualizzazioneFragment : Fragment() {

    open var db: FirebaseFirestore? = null
    var listAdapter: ListAdapter = ListAdapter(this.context)
    var listafiltrata: ArrayAdapter<String>? = null
    var titoli = ArrayList<String>()

    var idL = ArrayList<String>()
    var idResultData = ArrayList<String>()
    var resultsData = ArrayList<String>()

    var visualizzaTitoli: ListView? = null
    var ricerca: EditText? = null
    open var tipoCocktail: String? = null

    //open var titoloTW: TextView? = null
    open var titolo: String? = null


    var id: String? = null
    open var idRicetta: String? = null
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tipoCocktail = requireArguments().getString("tipoCocktail")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_lista_visualizzazione, container, false)
        visualizzaTitoli = root.findViewById(R.id.listview)
        ricerca = root.findViewById(R.id.ricerca)
        db = FirebaseFirestore.getInstance()
        db!!.collection(tipoCocktail!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (doc in task.result!!) {
                    titolo = doc["titolo"] as String?
                    id = doc.id
                    titoli.add(titolo!!)
                    idL.add(id!!)
                }
                visualizzaTitoli!!.adapter = listAdapter
            }
            else {
                var intent = Intent(this.context, MyProfileFragment::class.java)
                startActivity(intent)
            }
        }

        visualizzaTitoli!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                idRicetta = idL[position]
                openFragment(idRicetta, tipoCocktail)
            }
        ricerca!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                listAdapter.filter.filter(s.toString())
            }
        })
        return root
    }

    private fun openFragment(id: String?, tipoCocktail: String?) {
        val fragment: VisualizzaRicettaFragment = VisualizzaRicettaFragment.Companion.newInstance(id, tipoCocktail)
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.add(R.id.visualizzaCocktailFragment, fragment, "VISUALIZZARICETTA_FRAGMENT").commit()
    }

    fun sendBack(backText: String?) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(backText)
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(backText: String?)
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

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            val view = View.inflate(getContext(), R.layout.textviewlist, null)
            val titoloRicetta = view.findViewById<TextView>(R.id.titoloricetta)
            titoloRicetta.text = titoli[position]
            return view
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    val results = FilterResults()
                    if (constraint.isEmpty()) {
                        results.values = titoli
                        results.count = resultsData.size
                        resultsData.clear()
                        resultsData.addAll(titoli)
                    } else {
                        idResultData.clear()
                        val searchStr = constraint.toString().toUpperCase(Locale.ROOT)
                        for (o in titoli) {
                            if (o.toUpperCase(Locale.ROOT).startsWith(searchStr)) {
                                if (resultsData.contains(o)) {
                                    if (ricerca!!.length() == 0) results.values = titoli
                                    resultsData.clear()
                                    break
                                }
                                resultsData.add(o)
                                idResultData.add(idL[titoli.indexOf(o)])
                                results.values = o
                                results.count = resultsData.size
                            }
                        }
                    }
                    return results
                }

                override fun publishResults(constraint: CharSequence, results: FilterResults) {
                    listafiltrata = ArrayAdapter(requireContext(),
                        android.R.layout.simple_list_item_1,
                        resultsData)
                    visualizzaTitoli!!.adapter = listafiltrata
                    listafiltrata!!.notifyDataSetChanged()
                    notifyDataSetChanged()
                    visualizzaTitoli!!.onItemClickListener =
                        AdapterView.OnItemClickListener { parent, view, position, id ->
                            idRicetta = idResultData[position]
                            openFragment(idRicetta, tipoCocktail)
                        }
                }
            }
        }
    }

    companion object {
        fun newInstance(tipoPiatto: String?): ListaVisualizzazioneFragment {
            val fragment = ListaVisualizzazioneFragment()
            val args = Bundle()
            args.putString("tipoPiatto", tipoPiatto)
            fragment.arguments = args
            return fragment
        }
    }
}