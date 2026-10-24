package com.example.proxysafeiot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Principal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val usuario = findViewById<EditText>(R.id.txtusu)
        val contrasena = findViewById<EditText>(R.id.txtcon)
        val iniciarSesion = findViewById<Button>(R.id.btnini)
        val crearCuenta = findViewById<TextView>(R.id.tvcrearcuenta)

        val referencia = FirebaseDatabase.getInstance()
            .getReference("Usuario")

        iniciarSesion.setOnClickListener {
            val email = usuario.text.toString().trim()
            val password = contrasena.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(
                    this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            iniciarSesion.isEnabled = false

            referencia.addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        iniciarSesion.isEnabled = true

                        var usuarioEncontrado = false

                        for (registro in snapshot.children) {
                            val correoGuardado =
                                registro.child("email").getValue()?.toString()

                            val claveGuardada =
                                registro.child("contraseña").getValue()?.toString()

                            if (
                                correoGuardado == email &&
                                claveGuardada == password
                            ) {
                                usuarioEncontrado = true
                                break
                            }
                        }

                        if (usuarioEncontrado) {
                            Toast.makeText(
                                this@Principal,
                                "Inicio de sesión exitoso",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intentMenu = Intent(
                                this@Principal,
                                Menu::class.java
                            )

                            intentMenu.putExtra("emailUsuario", email)
                            startActivity(intentMenu)
                            finish()

                        } else {
                            Toast.makeText(
                                this@Principal,
                                "Correo o contraseña incorrectos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        iniciarSesion.isEnabled = true

                        Toast.makeText(
                            this@Principal,
                            "Error al consultar Firebase: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }

        crearCuenta.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }
    }
}