package com.example.newbiebartender

import com.example.newbiebartender.test_class.LogInUtil
import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Test per verificare la correttezza dei campi nell'ActivityLogin
class LogInTest {

    // funzione che verifica che il campo email è vuoto
    @Test
    fun checkEmptyEmail(){
        val check = LogInUtil.checkFields(
                "",
                "123456"
        )
        assertThat(check).isFalse()
    }

    // funzione che verifica che il campo password è vuoto
    @Test
    fun checkEmptyPassword() {
        val check = LogInUtil.checkFields(
                "test@gmail.com",
                ""
        )
        assertThat(check).isFalse()
    }

    // funzione che verifica che l'email inserita ha un pattern errato
    @Test
    fun checkWrongEmailPattern() {
        val check = LogInUtil.checkFields(
                "test",
                "123456"
        )
        assertThat(check).isFalse()
    }

    // funzione che verifica se la password ha una lunghezza sufficiente
    @Test
    fun checkPasswordLength() {
        val check = LogInUtil.checkFields(
                "test@gmail.com",
                "123"
        )
        assertThat(check).isFalse()
    }

    // funziona che verifica se tutti i campi sono corretti
    @Test
    fun checkAllGood() {
        val check = LogInUtil.checkFields(
                "test@gmail.com",
                "123456"
        )
        assertThat(check).isTrue()
    }
}