package com.example.newbiebartender

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class ActivityTest {

    // Test per la verifica della visualizzazione del contenuto della schermata di login
    @Test
    fun loginView() {
        launch(LoginActivity::class.java)
        onView(withId(R.id.login_activity)).check(matches(isDisplayed()))
    }

    // Test per la verifica della visualizzazione del contenuto della schermata di registrazione
    @Test
    fun signupView() {
        launch(RegisterActivity::class.java)
        onView(withId(R.id.register_activity)).check(matches(isDisplayed()))
    }


    // Test per verificare la visualizzazione di alcune componenti della UI della RegisterActivity
    @Test
    fun checkRegisterActivity(){

        launch(RegisterActivity::class.java)

        onView(withId(R.id.editTextTextUser)).check(matches(withHint("Username")))
        onView(withId(R.id.register_activity)).check(matches(isDisplayed()))
        onView(withId(R.id.registerButtonRegister)).perform(click())
        onView(withId(R.id.backToLoginTextView)).check(matches(withText(R.string.sei_gi_registrato_torna_al_login)))

    }
}