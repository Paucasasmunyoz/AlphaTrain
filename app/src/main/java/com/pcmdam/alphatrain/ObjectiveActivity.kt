package com.pcmdam.alphatrain

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pcmdam.alphatrain.databinding.ActivityObjectiveBinding

class ObjectiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityObjectiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val objectives = listOf("Aumentar masa muscular", "Perder peso (definición)", "Tonificar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, objectives)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerObjective.adapter = adapter
    }

    fun onContinueClicked(view: android.view.View) {
        val selectedObjective = binding.spinnerObjective.selectedItem.toString()
        Toast.makeText(this, "Objetivo seleccionado: $selectedObjective", Toast.LENGTH_SHORT).show()

    }
}
