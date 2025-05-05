package com.alphatrain.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pcmdam.alphatrain.Exercise
import com.pcmdam.alphatrain.ExerciseAdapter
import com.pcmdam.alphatrain.MainPageActivity
import com.pcmdam.alphatrain.R
import java.text.Normalizer

class WorkoutDayActivity : AppCompatActivity() {

    private lateinit var exercisesRecyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_day)

        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)

        val rawDay = intent.getStringExtra("day")?.trim()
        val day = normalize(rawDay)
        val workoutDayTitleTextView: TextView = findViewById(R.id.workoutDayTitle)
        workoutDayTitleTextView.text = rawDay  // Mostrar el día en pantalla

        val userId = auth.currentUser?.uid
        if (userId == null || day == null) {
            Log.e("WorkoutDayActivity", "Usuario no logueado o día nulo")
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val rawObjective = document.getString("objetivo")
                    val objective = normalize(rawObjective)

                    Log.d("WorkoutDayActivity", "Día normalizado: $day, Objetivo desde Firestore: $objective")

                    val exercises = getWorkoutForDay(day, objective)
                    Log.d("WorkoutDayActivity", "Lista de ejercicios creada. Tamaño: ${exercises.size}")

                    val adapter = ExerciseAdapter(exercises)
                    exercisesRecyclerView.adapter = adapter
                } else {
                    Log.e("WorkoutDayActivity", "Documento de usuario no existe")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("WorkoutDayActivity", "Error al obtener el objetivo desde Firestore", exception)
            }

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun normalize(text: String?): String? {
        if (text == null) return null
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
            .lowercase()
    }

    private fun getWorkoutForDay(day: String?, objective: String?): List<Exercise> {
        return when (day) {
            "lunes" -> getMondayWorkout(objective) as List<Exercise>
            "martes" -> getTuesdayWorkout(objective)
            "miercoles" -> getWednesdayWorkout(objective)
            "jueves" -> getThursdayWorkout(objective)
            "viernes" -> getFridayWorkout(objective)
            "sabado" -> getSaturdayWorkout(objective)
            else -> {
                Log.d("WorkoutDayActivity", "Día no reconocido o nulo: $day")
                emptyList()
            }
        }
    }

    private fun getMondayWorkout(objective: String?): Any {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Lunes para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Remo con barra Z", 3, "4-8"),
                Exercise("Jalón al pecho", 3, "4-8"),
                Exercise("Pull over", 3, "4-8"),
                Exercise("Deadlift", 2, "10-15"),
                Exercise("Curl de bíceps con barra Z", 4, "6-12"),
                Exercise("Curl martillo", 4, "6-12")
                )

            "definicion" -> listOf(
                Exercise("Remo con barra", 3, "12-15"),
                Exercise("Face pull", 3, "12-15"),
                Exercise("Pull-Up", 3, "12-15"),
                Exercise("Deadlift", 2, "12-15"),
                Exercise("Curl de biceps con mancuernas", 4, "10-15"),
                Exercise("Curl martillo", 4, "10-15"),
                )

            "tonificar" -> listOf(
                Exercise("Remo con mancuerna a una mano", 3, "8-12"),
                Exercise("Superman", 3, "8-12"),
                Exercise("Jalon al pecho", 3, "8-12"),
                Exercise("Peso muerto rumano", 3, "8-12"),
                Exercise("Curl de biceps con mancuerna", 4, "6-12"),
                Exercise("Curl de biceps con barra Z", 4, "6-12"),
                )
            else -> emptyList()
        }
    }

    private fun getTuesdayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Martes para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Press de banca plano", 4, "4-8"),
                Exercise("Press de banca inclinado con mancuernas", 3, "4-8"),
                Exercise("Aperturas en banco plano", 3, "4-8"),
                Exercise("Fondos en paralelas", 3, "6-10"),
                Exercise("Press de tríceps en cuerda", 4, "6-12"),
                Exercise("Extensiones de tríceps con mancuerna a una mano", 4, "6-12")
            )

            "definicion" -> listOf(
                Exercise("Press de banca plano", 3, "12-15"),
                Exercise("Aperturas con mancuernas en banco inclinado", 3, "12-15"),
                Exercise("Flexiones de pecho", 3, "12-15"),
                Exercise("Fondos en paralelas asistidos", 3, "12-15"),
                Exercise("Extensión de tríceps en cuerda", 4, "10-15"),
                Exercise("Rompecráneos", 4, "10-15")
            )

            "tonificar" -> listOf(
                Exercise("Flexiones de pecho", 3, "8-12"),
                Exercise("Press de banca en máquina", 3, "8-12"),
                Exercise("Aperturas con mancuernas", 3, "8-12"),
                Exercise("Fondos entre bancos", 3, "8-12"),
                Exercise("Extensión de tríceps por encima de la cabeza", 4, "6-12"),
                Exercise("Press de tríceps en máquina", 4, "6-12")
            )
            else -> emptyList()
        }
    }

    private fun getWednesdayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Miércoles para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Sentadillas", 4, "6-10"),
                Exercise("Prensa de pierna", 4, "6-10"),
                Exercise("Peso muerto", 3, "6-10"),
                Exercise("Zancadas con barra", 3, "6-10"),
                Exercise("Press militar con barra", 4, "6-10"),
                Exercise("Elevaciones laterales con mancuernas", 4, "6-10")
            )

            "definicion" -> listOf(
                Exercise("Sentadillas", 4, "12-15"),
                Exercise("Prensa de pierna", 3, "12-15"),
                Exercise("Extensiones de pierna", 3, "12-15"),
                Exercise("Curl de pierna", 3, "12-15"),
                // Hombro
                Exercise("Press militar con mancuernas", 4, "10-15"),
                Exercise("Elevaciones laterales", 4, "10-15")
            )

            "tonificar" -> listOf(
                Exercise("Sentadillas con mancuernas", 3, "12-15"),
                Exercise("Prensa de pierna", 3, "12-15"),
                Exercise("Zancadas", 3, "12-15"),
                Exercise("Curl femoral en máquina", 3, "12-15"),
                Exercise("Elevaciones frontales", 4, "12-15"),
                Exercise("Elevaciones laterales en cable", 4, "12-15")
            )
            else -> emptyList()
        }
    }

    private fun getThursdayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Jueves para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Jalón al pecho con agarre estrecho", 4, "4-8"),
                Exercise("Remo con barra", 4, "4-8"),
                Exercise("Pull-up (dominadas)", 3, "4-8"),
                Exercise("Pullover con mancuerna", 3, "4-8"),
                Exercise("Curl con barra recta", 4, "6-12"),
                Exercise("Curl de bíceps concentrado", 4, "6-12")
            )

            "definicion" -> listOf(
                Exercise("Jalón al pecho", 3, "12-15"),
                Exercise("Remo en máquina", 3, "12-15"),
                Exercise("Pull-up (dominadas)", 3, "12-15"),
                Exercise("Face pull", 3, "12-15"),
                // Bíceps
                Exercise("Curl con mancuernas", 4, "10-15"),
                Exercise("Curl martillo", 4, "10-15")
            )

            "tonificar" -> listOf(
                Exercise("Remo con mancuerna", 3, "8-12"),
                Exercise("Jalón al pecho", 3, "8-12"),
                Exercise("Superman", 3, "8-12"),
                Exercise("Pull-up asistido", 3, "8-12"),
                // Bíceps
                Exercise("Curl con mancuerna en banco inclinado", 4, "6-12"),
                Exercise("Curl con barra Z", 4, "6-12")
            )
            else -> emptyList()
        }
    }

    private fun getFridayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Viernes para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Press de banca con barra", 4, "4-8"),
                Exercise("Press inclinado con barra", 4, "4-8"),
                Exercise("Aperturas con mancuernas", 3, "4-8"),
                Exercise("Press declinado con barra", 3, "4-8"),
                Exercise("Press de tríceps en máquina", 4, "6-12"),
                Exercise("Extensiones de tríceps con barra Z", 4, "6-12")
            )

            "definicion" -> listOf(
                Exercise("Press de banca con mancuernas", 3, "12-15"),
                Exercise("Aperturas con mancuernas en banco inclinado", 3, "12-15"),
                Exercise("Pullover con mancuerna", 3, "12-15"),
                Exercise("Flexiones con palmas", 3, "12-15"),
                Exercise("Extensión de tríceps con cuerda en polea", 4, "10-15"),
                Exercise("Rompecráneos en banco plano", 4, "10-15")
            )

            "tonificar" -> listOf(
                Exercise("Flexiones de rodillas", 3, "8-12"),
                Exercise("Press de pecho en máquina", 3, "8-12"),
                Exercise("Aperturas con mancuernas en banco plano", 3, "8-12"),
                Exercise("Press de pecho en máquina con agarre neutro", 3, "8-12"),
                Exercise("Extensión de tríceps en polea baja", 4, "6-12"),
                Exercise("Patada de tríceps con mancuerna", 4, "6-12")
            )

            else -> emptyList()
        }
    }

    private fun getSaturdayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Sábado para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Hip thrust con barra", 4, "6-10"),
                Exercise("Peso muerto rumano con mancuernas", 4, "6-10"),
                Exercise("Zancadas caminando con mancuernas", 3, "6-10"),
                Exercise("Sentadilla frontal con barra", 3, "6-10"),
                Exercise("Press Arnold", 4, "6-10"),
                Exercise("Face pull con cuerda", 4, "6-10")
            )

            "definicion" -> listOf(
                Exercise("Step-ups con mancuernas", 3, "12-15"),
                Exercise("Curl femoral tumbado", 3, "12-15"),
                Exercise("Extensiones de cadera en máquina", 3, "12-15"),
                Exercise("Sentadillas búlgaras", 3, "12-15"),
                Exercise("Press militar en máquina", 4, "10-15"),
                Exercise("Pájaros con mancuernas", 4, "10-15")
            )

            "tonificar" -> listOf(
                Exercise("Elevaciones de talones de pie (gemelos)", 3, "12-15"),
                Exercise("Zancadas laterales", 3, "12-15"),
                Exercise("Sentadillas sumo con mancuerna", 3, "12-15"),
                Exercise("Curl de pierna sentado", 3, "12-15"),
                Exercise("Elevaciones en Y con mancuernas", 4, "12-15"),
                Exercise("Press militar con banda elástica", 4, "12-15")
            )

            else -> emptyList()
        }
    }
}
