package com.example.newbiebartender

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.ImageView

class StartScreenActivity : AppCompatActivity() {
    var logo: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_start_screen)

        logo = findViewById(R.id.imageView_startScreen)

        // Viene eseguita una semplice animazione all'avvio dell'app,
        // facendo ruotare il logo dell'app di 360 gradi
        logo!!.animate().apply {
            duration = 1400
            rotationYBy(360f)
            rotationY(360f)
        }.start()

        // Terminata l'animazione, si viene mandati all' Activity di Login nel caso in cui non avessimo effettuato il login,
        // altiemnti si viene mandati all' homepage
        Handler().postDelayed({
            val intent = Intent(this@StartScreenActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }

}