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
        logo!!.animate().apply {
            duration = 1400
            rotationYBy(360f)
            rotationY(360f)
        }.start()

        Handler().postDelayed({
            val intent = Intent(this@StartScreenActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}