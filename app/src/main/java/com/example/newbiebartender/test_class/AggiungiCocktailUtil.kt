package com.example.newbiebartender.test_class


// Questo oggetto verrà utilizzato nella fase di test di aggiunta di un cocktail
object AggiungiCocktailUtil {

    fun checkAggiungiCocktail( nomeCocktail: String,
                               tipologia: String,
                               difficolta: String,
                               ingredienti: String,
                               procedimento: String,
                               urlImmagine: String
    ) : Boolean{

        if (nomeCocktail.isEmpty()) return false
        if (procedimento.isEmpty()) return false
        if (ingredienti.isEmpty()) return false

        return true
    }
}