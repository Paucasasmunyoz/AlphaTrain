package com.pcmdam.alphatrain

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class UserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val weightEditText: EditText = findViewById(R.id.weightEditText)
        val ageEditText: EditText = findViewById(R.id.ageEditText)
        val heightEditText: EditText = findViewById(R.id.heightEditText)
        val objectiveSpinner: Spinner = findViewById(R.id.objectiveSpinner)
        val calculateButton: Button = findViewById(R.id.calculateButton)

        val objectiveOptions = arrayOf("Mantener peso", "Aumentar masa muscular", "Perder peso")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, objectiveOptions)
        objectiveSpinner.adapter = adapter

        calculateButton.setOnClickListener {
            val weight = weightEditText.text.toString().toDoubleOrNull()
            val age = ageEditText.text.toString().toIntOrNull()
            val height = heightEditText.text.toString().toIntOrNull()
            val objective = objectiveSpinner.selectedItem.toString()

            if (weight != null && age != null && height != null) {
                val intent = Intent(this, DietActivity::class.java)
                intent.putExtra("weight", weight)
                intent.putExtra("age", age)
                intent.putExtra("height", height)
                intent.putExtra("objective", objective)
                startActivity(intent)
                finish()
            } else {

            }
        }
    }
}