package com.pcmdam.alphatrain

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pcmdam.alphatrain.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val nombre = binding.etName.text.toString().trim()
            val edad = binding.etEdad.text.toString().toIntOrNull() ?: 0
            val altura = binding.etAltura.text.toString().toFloatOrNull() ?: 0f
            val peso = binding.etPeso.text.toString().toFloatOrNull() ?: 0f

            if (email.isNotEmpty() && password.isNotEmpty() && nombre.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.let {
                                val userId = it.uid
                                val userMap = hashMapOf(
                                    "email" to email,
                                    "nombre" to nombre,
                                    "edad" to edad,
                                    "altura" to altura,
                                    "peso" to peso,
                                    "objetivo" to "",
                                    "Dieta" to hashMapOf(
                                        "Calorías" to 111,
                                        "Proteínas" to 33
                                    ),
                                    "plan_entrenamiento" to "/PlanesEntrenamiento/$userId",
                                    "progreso" to "/Progreso/progreso1"
                                )

                                db.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()

                                        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
                                        val editor = sharedPref.edit()
                                        editor.putString("email", email)
                                        editor.putString("password", password)
                                        editor.apply()

                                        val intent = Intent(this, TrainingDaysActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
