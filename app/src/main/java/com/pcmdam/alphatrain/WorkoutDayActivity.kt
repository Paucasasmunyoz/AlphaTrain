package com.alphatrain.app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pcmdam.alphatrain.Exercise
import com.pcmdam.alphatrain.ExerciseAdapter
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
                Exercise("Ejercicio 3", 3, "8-12"),
                Exercise("Ejercicio 4", 3, "8-12")
            )
            "tonificar" -> listOf(
                Exercise("Ejercicio 5", 2, "12-15"),
                Exercise("Ejercicio 6", 2, "12-15")
            )
            else -> emptyList()
        }
    }

    private fun getTuesdayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Martes para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Ejercicio 7", 3, "6-10"),
                Exercise("Ejercicio 8", 3, "6-10")
            )
            "definicion" -> listOf(
                Exercise("Ejercicio 9", 3, "8-12"),
                Exercise("Ejercicio 10", 3, "8-12")
            )
            "tonificar" -> listOf(
                Exercise("Ejercicio 11", 2, "12-15"),
                Exercise("Ejercicio 12", 2, "12-15")
            )
            else -> emptyList()
        }
    }

    private fun getWednesdayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Miércoles para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Ejercicio 13", 3, "6-10"),
                Exercise("Ejercicio 14", 3, "6-10")
            )
            "definicion" -> listOf(
                Exercise("Ejercicio 15", 3, "8-12"),
                Exercise("Ejercicio 16", 3, "8-12")
            )
            "tonificar" -> listOf(
                Exercise("Ejercicio 17", 2, "12-15"),
                Exercise("Ejercicio 18", 2, "12-15")
            )
            else -> emptyList()
        }
    }

    private fun getThursdayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Jueves para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Ejercicio 19", 3, "6-10"),
                Exercise("Ejercicio 20", 3, "6-10")
            )
            "definicion" -> listOf(
                Exercise("Ejercicio 21", 3, "8-12"),
                Exercise("Ejercicio 22", 3, "8-12")
            )
            "tonificar" -> listOf(
                Exercise("Ejercicio 23", 2, "12-15"),
                Exercise("Ejercicio 24", 2, "12-15")
            )
            else -> emptyList()
        }
    }

    private fun getFridayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Viernes para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Ejercicio 25", 3, "6-10"),
                Exercise("Ejercicio 26", 3, "6-10")
            )
            "definicion" -> listOf(
                Exercise("Ejercicio 27", 3, "8-12"),
                Exercise("Ejercicio 28", 3, "8-12")
            )
            "tonificar" -> listOf(
                Exercise("Ejercicio 29", 2, "12-15"),
                Exercise("Ejercicio 30", 2, "12-15")
            )
            else -> emptyList()
        }
    }

    private fun getSaturdayWorkout(objective: String?): List<Exercise> {
        Log.d("WorkoutDayActivity", "Obteniendo rutina de Sábado para: $objective")
        return when (objective) {
            "aumento de masa muscular" -> listOf(
                Exercise("Ejercicio A", 3, "6-10"),
                Exercise("Ejercicio B", 3, "6-10")
            )
            "definicion" -> listOf(
                Exercise("Ejercicio C", 3, "8-12"),
                Exercise("Ejercicio D", 3, "8-12")
            )
            "tonificar" -> listOf(
                Exercise("Ejercicio E", 2, "12-15"),
                Exercise("Ejercicio F", 2, "12-15")
            )
            else -> emptyList()
        }
    }
}
