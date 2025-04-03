package com.pcmdam.alphatrain;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pcmdam.alphatrain.databinding.ActivityTrainingDaysBinding;

class TrainingDaysActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingDaysBinding;
    private val auth = FirebaseAuth.getInstance();
    private val db = FirebaseFirestore.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrainingDaysBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val checkLunes = findViewById<CheckBox>(R.id.checkLunes);
        val checkMartes = findViewById<CheckBox>(R.id.checkMartes);
        val checkMiercoles = findViewById<CheckBox>(R.id.checkMiercoles);
        val checkJueves = findViewById<CheckBox>(R.id.checkJueves);
        val checkViernes = findViewById<CheckBox>(R.id.checkViernes);
        val checkSabado = findViewById<CheckBox>(R.id.checkSabado);
        val checkDomingo = findViewById<CheckBox>(R.id.checkDomingo);

        binding.btnSave.setOnClickListener {
            val diasSeleccionados = mutableListOf<String>();

            if (checkLunes.isChecked) diasSeleccionados.add("Lunes");
            if (checkMartes.isChecked) diasSeleccionados.add("Martes");
            if (checkMiercoles.isChecked) diasSeleccionados.add("Miércoles");
            if (checkJueves.isChecked) diasSeleccionados.add("Jueves");
            if (checkViernes.isChecked) diasSeleccionados.add("Viernes");
            if (checkSabado.isChecked) diasSeleccionados.add("Sábado");
            if (checkDomingo.isChecked) diasSeleccionados.add("Domingo");

            if (diasSeleccionados.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un día", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            val userId = auth.currentUser?.uid;
            if (userId != null) {
                val planEntrenamiento = hashMapOf(
                    "dias_semana" to diasSeleccionados.joinToString(", "),
                    "ejercicios" to listOf("ej1"),
                    "id" to "plan1",
                    "nivel" to "Principiante",
                    "nombre_plan" to "Full Body",
                    "uid_usuario" to "/usuarios/$userId"
                );

                db.collection("PlanesEntrenamiento").document(userId)
                    .set(planEntrenamiento)
                    .addOnSuccessListener {
                        val userRef = db.collection("users").document(userId);
                        userRef.update("plan_entrenamiento", "/PlanesEntrenamiento/$userId")
                            .addOnSuccessListener {
                                Toast.makeText(this, "Plan de entrenamiento guardado correctamente", Toast.LENGTH_SHORT).show();
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al actualizar el plan: ${e.message}", Toast.LENGTH_SHORT).show();
                            };
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar el plan de entrenamiento: ${e.message}", Toast.LENGTH_SHORT).show();
                    };
            } else {
                Toast.makeText(this, "Error: no se pudo obtener el usuario", Toast.LENGTH_SHORT).show();
            }
        };
    }
}