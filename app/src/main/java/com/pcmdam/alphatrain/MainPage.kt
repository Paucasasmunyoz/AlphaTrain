package com.pcmdam.alphatrain

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alphatrain.app.WorkoutDayActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class MainPageActivity : AppCompatActivity() {
    private var layoutDiasEntrenamiento: LinearLayout? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var userObjective: String = "Mantener peso" // Valor por defecto

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
                        if (!diasSemanaString.isNullOrEmpty()) {
                            val dias = diasSemanaString.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                            // Limpiar cualquier TextView previo
                            layoutDiasEntrenamiento?.removeAllViews()

                            // Crear dinámicamente los TextViews
                            for (i in dias.indices) {
                                val diaTextView = TextView(this@MainPageActivity)
                                diaTextView.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    resources.getDimensionPixelSize(R.dimen.dia_height)
                                )
                                diaTextView.setBackgroundColor(resources.getColor(R.color.dia_background)) // Fondo lila
                                diaTextView.setPadding(16, 0, 0, 0)
                                diaTextView.text = dias[i].trim()
                                diaTextView.setTextColor(resources.getColor(android.R.color.white))
                                diaTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                                diaTextView.gravity = Gravity.CENTER_VERTICAL
                                diaTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER)

                                // Agregar bordes redondeados y espaciado
                                diaTextView.setBackgroundResource(R.drawable.dia_background) // Para aplicar bordes redondeados

                                // Aumentar el margen inferior para más separación entre los días
                                val layoutParams = diaTextView.layoutParams as LinearLayout.LayoutParams
                                layoutParams.bottomMargin = 16 // Mayor separación entre días
                                diaTextView.layoutParams = layoutParams

                                // Añadir clic para la navegación al detalle del día
                                diaTextView.setOnClickListener {
                                    val intent = Intent(this@MainPageActivity, WorkoutDayActivity::class.java)
                                    intent.putExtra("day", dias[i].trim())
                                    intent.putExtra("objective", userObjective)
                                    startActivity(intent)
                                }

                                layoutDiasEntrenamiento?.addView(diaTextView)
                            }
                        } else {
                            mostrarMensaje("Aún no has seleccionado tus días de entrenamiento.")
                        }
                    } else {
                        mostrarMensaje("Aún no has configurado tu plan de entrenamiento.")
                    }
                }
                .addOnFailureListener { e: Exception ->
                    mostrarMensaje("Error al cargar los días de entrenamiento.")
                }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        val mensajeTextView = TextView(this@MainPageActivity)
        mensajeTextView.text = mensaje
        layoutDiasEntrenamiento!!.addView(mensajeTextView)
    }
}
