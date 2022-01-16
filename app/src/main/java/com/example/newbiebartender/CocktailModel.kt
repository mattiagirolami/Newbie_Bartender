package com.example.newbiebartender


// Classe contenente il model del Cocktail, utilizzato per la recyclerView contenuta nello StatsFragment
public class CocktailModel(var immagine: String? = null,
                           var autore : String? = null,
                           var titolo : String? = null,
                           var id : String? = null,
                           var mediaValutazioni: Float? = null,
                           var tipoRicetta: String? = null)