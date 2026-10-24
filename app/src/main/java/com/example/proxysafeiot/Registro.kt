
package com.example.proxysafeiot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val referencia = FirebaseDatabase.getInstance()
        .getReference("Usuario")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()

        val nombre = findViewById<EditText>(R.id.txtnomreg)
        val usuario = findViewById<EditText>(R.id.txtusureg)
        val email = findViewById<EditText>(R.id.txtemailreg)
        val contrasena = findViewById<EditText>(R.id.txtconreg)
        val confirmarContrasena =
            findViewById<EditText>(R.id.txtconfirmarreg)
        val registrar = findViewById<Button>(R.id.btnregistrar)
        val volverLogin = findViewById<TextView>(R.id.tvvolverlogin)

        registrar.setOnClickListener {
            val nombreTexto = nombre.text.toString().trim()
            val usuarioTexto = usuario.text.toString().trim()
            val emailTexto = email.text.toString().trim()
            val password = contrasena.text.toString()
            val confirmarPassword = confirmarContrasena.text.toString()

            if (
                nombreTexto.isBlank() ||
                usuarioTexto.isBlank() ||
                emailTexto.isBlank() ||
                password.isBlank() ||
                confirmarPassword.isBlank()
            ) {
                mensaje("Completa todos los campos")
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(emailTexto).matches()
            ) {
                mensaje("Ingresa un correo válido")
                return@setOnClickListener
            }

            if (password != confirmarPassword) {
                mensaje("Las contraseñas no coinciden")
                return@setOnClickListener
            }

            if (password.length < 6) {
                mensaje("La contraseña debe tener al menos 6 caracteres")
                return@setOnClickListener
            }

            registrar.isEnabled = false

            referencia.get()
                .addOnSuccessListener { snapshot ->

                    val correoExiste = snapshot.children.any {
                        it.child("email")
                            .getValue(String::class.java)
                            ?.trim()
                            ?.equals(emailTexto, ignoreCase = true) == true
                    }

                    if (correoExiste) {
                        registrar.isEnabled = true
                        mensaje("Ese correo ya está registrado")
                        return@addOnSuccessListener
                    }

                    auth.createUserWithEmailAndPassword(
                        emailTexto,
                        password
                    ).addOnCompleteListener(this) { task ->

                        if (!task.isSuccessful) {
                            registrar.isEnabled = true
                            mensaje(
                                task.exception?.localizedMessage
                                    ?: "No se pudo crear la cuenta"
                            )
                            return@addOnCompleteListener
                        }

                        val uid = auth.currentUser?.uid

                        if (uid == null) {
                            registrar.isEnabled = true
                            mensaje("No se pudo obtener el usuario")
                            return@addOnCompleteListener
                        }

                        val siguienteId = (
                                snapshot.children
                                    .mapNotNull { it.key?.toLongOrNull() }
                                    .maxOrNull() ?: 0L
                                ) + 1L

                        val datosUsuario = mapOf(
                            "nombre" to nombreTexto,
                            "usuario" to usuarioTexto,
                            "email" to emailTexto,
                            "contraseña" to password,
                            "tipo" to "Prevencionista",
                            "uid" to uid
                        )

                        referencia.child(siguienteId.toString())
                            .setValue(datosUsuario)
                            .addOnSuccessListener {
                                auth.signOut()

                                mensaje(
                                    "Cuenta creada correctamente con ID $siguienteId"
                                )

                                startActivity(
                                    Intent(
                                        this@Registro,
                                        Principal::class.java
                                    )
                                )
                                finish()
                            }
                            .addOnFailureListener { error ->
                                registrar.isEnabled = true
                                mensaje(
                                    "La cuenta se creó en Authentication, pero falló el guardado en Realtime Database: ${error.message}"
                                )
                            }
                    }
                }
                .addOnFailureListener { error ->
                    registrar.isEnabled = true
                    mensaje(
                        "Error al consultar usuarios: ${error.message}"
                    )
                }
        }

        volverLogin.setOnClickListener {
            startActivity(
                Intent(this@Registro, Principal::class.java)
            )
            finish()
        }
    }

    private fun mensaje(texto: String) {
        Toast.makeText(
            this,
            texto,
            Toast.LENGTH_LONG
        ).show()
    }
}
