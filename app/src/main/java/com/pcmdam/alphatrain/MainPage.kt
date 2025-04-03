package com.pcmdam.alphatrain

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Arrays

class MainPageActivity : AppCompatActivity() {
    private var layoutDiasEntrenamiento: LinearLayout? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        layoutDiasEntrenamiento = findViewById(R.id.layoutDiasEntrenamiento)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        cargarDiasEntrenamiento()
    }

    private fun cargarDiasEntrenamiento() {
        val userId = auth!!.currentUser!!.uid
        if (userId != null) {
            db!!.collection("PlanesEntrenamiento").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val diasSemanaString = documentSnapshot.getString("dias_semana")
                        if (diasSemanaString != null && !diasSemanaString.isEmpty()) {
                            val dias = Arrays.asList(
                                *diasSemanaString.split(", ".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray())
                            for (dia in dias) {
                                val diaTextView =
                                    TextView(this@MainPageActivity)
                                diaTextView.text = dia.trim { it <= ' ' }
                                diaTextView.textSize = 18f
                                diaTextView.setTextColor(
                                    resources.getColor(
                                        R.color.purple_500,
                                        theme
                                    )
                                )
                                layoutDiasEntrenamiento!!.addView(diaTextView)
                            }
                        } else {
                            val mensajeTextView =
                                TextView(this@MainPageActivity)
                            mensajeTextView.text =
                                "Aún no has seleccionado tus días de entrenamiento."
                            layoutDiasEntrenamiento!!.addView(mensajeTextView)
                        }
                    } else {
                        val mensajeTextView =
                            TextView(this@MainPageActivity)
                        mensajeTextView.text = "Aún no has configurado tu plan de entrenamiento."
                        layoutDiasEntrenamiento!!.addView(mensajeTextView)
                    }
                }
                .addOnFailureListener { e: Exception? ->
                    val errorTextView = TextView(this@MainPageActivity)
                    errorTextView.text = "Error al cargar los días de entrenamiento."
                    layoutDiasEntrenamiento!!.addView(errorTextView)
                }
        }
    }
}