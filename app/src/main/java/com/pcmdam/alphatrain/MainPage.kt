package com.pcmdam.alphatrain

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.alphatrain.app.WorkoutDayActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.DocumentReference

class MainPageActivity : AppCompatActivity() {
    private var layoutDiasEntrenamiento: LinearLayout? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var userObjective: String = "Mantener peso"
    private var spinnerObjectives: Spinner? = null
    private var btnUpdateObjective: AppCompatButton? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        layoutDiasEntrenamiento = findViewById(R.id.layoutDiasEntrenamiento)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        userObjective = prefs.getString("objective", "Mantener peso") ?: "Mantener peso"

        spinnerObjectives = findViewById(R.id.spinner_objectives)
        btnUpdateObjective = findViewById(R.id.btn_update_objective)

        cargarDiasEntrenamiento()

        btnUpdateObjective?.setOnClickListener {
            val selectedObjective = spinnerObjectives?.selectedItem.toString()

            if (selectedObjective != "Selecciona tu objetivo") {
                actualizarObjetivoEnFirebase(selectedObjective)
            } else {
                Toast.makeText(this, "Por favor, selecciona un objetivo", Toast.LENGTH_SHORT).show()
            }
        }

        val settingsImageView: ImageView = findViewById(R.id.settingsImageView)
        settingsImageView.setOnClickListener {
            val popupMenu = PopupMenu(this@MainPageActivity, settingsImageView)
            popupMenu.menu.add("Dieta")
            popupMenu.menu.add("Objetivo")
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Dieta" -> {
                        val intent = Intent(this@MainPageActivity, UserInfoActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    "Objetivo" -> {
                        val intent = Intent(this@MainPageActivity, ObjectiveActivity2::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
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

                            layoutDiasEntrenamiento?.removeAllViews()

                            for (i in dias.indices) {
                                val diaTextView = TextView(this@MainPageActivity)
                                diaTextView.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    resources.getDimensionPixelSize(R.dimen.dia_height)
                                )
                                diaTextView.setBackgroundColor(resources.getColor(R.color.dia_background))
                                diaTextView.setPadding(16, 0, 0, 0)
                                diaTextView.text = dias[i].trim()
                                diaTextView.setTextColor(resources.getColor(android.R.color.white))
                                diaTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                                diaTextView.gravity = Gravity.CENTER_VERTICAL
                                diaTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER)


                                diaTextView.setBackgroundResource(R.drawable.dia_background)

                                val layoutParams = diaTextView.layoutParams as LinearLayout.LayoutParams
                                layoutParams.bottomMargin = 16
                                diaTextView.layoutParams = layoutParams

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

    private fun actualizarObjetivoEnFirebase(selectedObjective: String) {
        val userId = auth!!.currentUser!!.uid
        val userRef: DocumentReference = db!!.collection("users").document(userId)

        userRef.update("objective", selectedObjective)
            .addOnSuccessListener {
                val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString("objective", selectedObjective)
                editor.apply()

                Toast.makeText(this, "Objetivo actualizado correctamente", Toast.LENGTH_SHORT).show()
                userObjective = selectedObjective
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar el objetivo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
