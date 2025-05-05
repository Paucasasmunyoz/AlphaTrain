package com.pcmdam.alphatrain

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alphatrain.app.WorkoutDayActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Arrays

class MainPageActivity : AppCompatActivity() {
    private var layoutDiasEntrenamiento: LinearLayout? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var userObjective: String = "Mantener peso" // Inicializamos con un valor por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        layoutDiasEntrenamiento = findViewById(R.id.layoutDiasEntrenamiento)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Obtener el objetivo del usuario (ejemplo: desde SharedPreferences)
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        userObjective = prefs.getString("objective", "Mantener peso") ?: "Mantener peso"

        cargarDiasEntrenamiento()

        val settingsImageView: ImageView = findViewById(R.id.settingsImageView)
        settingsImageView.setOnClickListener {
            val popupMenu = PopupMenu(this@MainPageActivity, settingsImageView)
            popupMenu.menu.add("Dieta")
            popupMenu.setOnMenuItemClickListener { item ->
                if (item.title == "Dieta") {
                    val intent = Intent(this@MainPageActivity, UserInfoActivity::class.java)
                    startActivity(intent)
                    true
                } else {
                    false
                }
            }
            popupMenu.show()
        }
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
                                    .toTypedArray()
                            )

                            val diaTextViews = arrayOf(
                                findViewById<TextView>(R.id.dia1TextView),
                                findViewById<TextView>(R.id.dia2TextView),
                                findViewById<TextView>(R.id.dia3TextView),
                                findViewById<TextView>(R.id.dia4TextView),
                                findViewById<TextView>(R.id.dia5TextView)
                            )

                            for (i in dias.indices) {
                                if (i < diaTextViews.size) {
                                    val dayOfWeek = dias[i].trim()
                                    diaTextViews[i].text = dayOfWeek
                                    diaTextViews[i].setOnClickListener {
                                        val intent = Intent(this@MainPageActivity, WorkoutDayActivity::class.java)
                                        intent.putExtra("day", dayOfWeek)
                                        intent.putExtra("objective", userObjective)
                                        startActivity(intent)
                                    }
                                }
                            }
                            for (i in dias.size until diaTextViews.size) {
                                diaTextViews[i].visibility = View.GONE
                            }
                        } else {
                            val mensajeTextView = TextView(this@MainPageActivity)
                            mensajeTextView.text = "Aún no has seleccionado tus días de entrenamiento."
                            layoutDiasEntrenamiento!!.addView(mensajeTextView)
                        }
                    } else {
                        val mensajeTextView = TextView(this@MainPageActivity)
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