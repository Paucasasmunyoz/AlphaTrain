package com.pcmdam.alphatrain

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class DietActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)

        val caloriesTextView: TextView = findViewById(R.id.caloriesTextView)
        val proteinTextView: TextView = findViewById(R.id.proteinTextView)
        val carbsTextView: TextView = findViewById(R.id.carbsTextView)
        val waterTextView: TextView = findViewById(R.id.waterTextView)
        val backButton: ImageView = findViewById(R.id.backButton)

        val weight = intent.getDoubleExtra("weight", 0.0)
        val age = intent.getIntExtra("age", 0)
        val height = intent.getIntExtra("height", 0)
        val objective = intent.getStringExtra("objective") ?: "Mantener peso"


        var bmr = (10 * weight + 6.25 * height - 5 * age + 5).roundToInt()
        var activityFactor = 1.55
        var calories = (bmr * activityFactor).roundToInt()

        var proteinGrams = (weight * 1.6).roundToInt()
        var waterLitros = (weight * 0.035).roundToInt()

        if (objective == "Perder peso") {
            calories -= 500
            proteinGrams = (weight * 1.8).roundToInt()
        } else if (objective == "Aumentar masa muscular") {
            calories += 300
            proteinGrams = (weight * 2.0).roundToInt()
        }

        val carbsGrams = (calories * 0.5 / 4).roundToInt()

        caloriesTextView.text = "$calories kcal"
        proteinTextView.text = "$proteinGrams g"
        carbsTextView.text = "$carbsGrams g"
        waterTextView.text = "$waterLitros litros"

        backButton.setOnClickListener {
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}