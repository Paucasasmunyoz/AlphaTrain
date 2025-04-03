package com.pcmdam.alphatrain

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pcmdam.alphatrain.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.let {
                                val userId = it.uid

                                db.collection("PlanesEntrenamiento").document(userId)
                                    .get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            db.collection("users").document(userId)
                                                .get()
                                                .addOnSuccessListener { userDocument ->
                                                    if (userDocument.exists() && userDocument.contains("objetivo")) {
                                                        val objetivo = userDocument.getString("objetivo")
                                                        if (!objetivo.isNullOrEmpty()) {
                                                            Toast.makeText(this@LoginActivity, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show()
                                                            val intent = Intent(this@LoginActivity, MainPageActivity::class.java)
                                                            startActivity(intent)
                                                            finish()
                                                        } else {
                                                            Toast.makeText(this@LoginActivity, "¡Bienvenido! Selecciona tu objetivo.", Toast.LENGTH_SHORT).show()
                                                            val intent = Intent(this@LoginActivity, ObjectiveActivity::class.java)
                                                            startActivity(intent)
                                                            finish()
                                                        }
                                                    } else {
                                                        Toast.makeText(this@LoginActivity, "¡Bienvenido! Selecciona tu objetivo.", Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this@LoginActivity, ObjectiveActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(this@LoginActivity, "Error al verificar el objetivo: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        } else {
                                            Toast.makeText(this, "¡Bienvenido! Elige tus días de entrenamiento.", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this, TrainingDaysActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al verificar el plan: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
