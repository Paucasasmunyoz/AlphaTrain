package com.pcmdam.alphatrain

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DietActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)

        val caloriesTextView: TextView = findViewById(R.id.caloriesTextView)
        val proteinTextView: TextView = findViewById(R.id.proteinTextView)
        val carbsTextView: TextView = findViewById(R.id.carbsTextView)
        val waterTextView: TextView = findViewById(R.id.waterTextView)
        val backButton: ImageView = findViewById(R.id.backButton)

        caloriesTextView.text = "1800 kcal"
        proteinTextView.text = "140 g"
        carbsTextView.text = "300 g"
        waterTextView.text = "3 litros"

        backButton.setOnClickListener {
            // Crea un Intent para volver a la MainPageActivity
            val intent = Intent(this, MainPageActivity::class.java)
            // Inicia la actividad
            startActivity(intent)
            // Opcional: Cierra la actividad actual para que no quede en la pila
            finish()
        }
    }
}