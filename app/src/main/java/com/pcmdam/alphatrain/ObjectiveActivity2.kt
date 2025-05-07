package com.pcmdam.alphatrain

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ObjectiveActivity2 : AppCompatActivity() {

    private lateinit var spinnerObjective: Spinner
    private lateinit var btnSaveObjective: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var selectedObjective = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_objective2)

        spinnerObjective = findViewById(R.id.spinner_objectives)
        btnSaveObjective = findViewById(R.id.btn_update_objective)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.objectives_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerObjective.adapter = adapter

        spinnerObjective.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedObjective = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        btnSaveObjective.setOnClickListener {
            if (selectedObjective.isNotEmpty()) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    db.collection("users").document(userId)
                        .update("objetivo", selectedObjective)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Objetivo actualizado", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, MainPageActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al actualizar el objetivo: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Selecciona un objetivo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
