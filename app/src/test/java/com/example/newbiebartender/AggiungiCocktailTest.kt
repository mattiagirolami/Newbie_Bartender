package com.example.newbiebartender

import com.example.newbiebartender.test_class.AggiungiCocktailUtil
import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Classe di test sui campi del fragment dedicato all'aggiunta di un nuovo cocktail
class AggiungiCocktailTest {

    val urlImmagine = "https://www.dissapore.com/wp-content/uploads/2018/04/manhattan.jpg"

    // Funzione che verifica se il nome non è stato inserito
    @Test
    fun emptyCocktailNome() {
        val check = AggiungiCocktailUtil.checkAggiungiCocktail(
            "",
            "analcolico",
            "Facile",
            "Acqua",
            "Lorem ipsum",
            urlImmagine
        )
        assertThat(check).isFalse()
    }

    // Funzione che verifica se il procedimento è assente
    @Test
    fun emptyCocktailProcedimento() {
        val check = AggiungiCocktailUtil.checkAggiungiCocktail(
            "Test",
            "analcolico",
            "Facile",
            "Acqua",
            "",
            urlImmagine
        )
        assertThat(check).isFalse()
    }

    // Funzione che verifica se non sono stati inseriti ingredienti
    @Test
    fun emptyCocktailIngredienti() {
        val check = AggiungiCocktailUtil.checkAggiungiCocktail(
            "Test",
            "analcolico",
            "Facile",
            "",
            "Lorem ipsum",
            urlImmagine
        )
        assertThat(check).isFalse()
    }

    // Funzione che verifica la correttezza di tutti i campi inseriti
    @Test
    fun checkAllCocktailFields() {
        val check = AggiungiCocktailUtil.checkAggiungiCocktail(
            "Test",
            "analcolico",
            "Facile",
            "Acqua",
            "Lorem ipsum",
            urlImmagine
        )
        assertThat(check).isTrue()
    }

}