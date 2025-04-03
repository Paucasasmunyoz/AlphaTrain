package com.pcmdam.alphatrain

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ObjectiveActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var spinnerObjective: Spinner
    private lateinit var btnContinue: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var selectedObjective = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_objective)

        spinnerObjective = findViewById(R.id.spinnerObjective)
        btnContinue = findViewById(R.id.btnContinue)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.objectives_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerObjective.adapter = adapter
        spinnerObjective.onItemSelectedListener = this

        btnContinue.setOnClickListener {
            if (selectedObjective.isNotEmpty()) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    db.collection("users").document(userId)
                        .update("objetivo", selectedObjective)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@ObjectiveActivity,
                                "¡Objetivo guardado!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@ObjectiveActivity, MainPageActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@ObjectiveActivity,
                                "Error al guardar el objetivo: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this@ObjectiveActivity,
                    "Por favor, selecciona un objetivo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        selectedObjective = parent.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}
