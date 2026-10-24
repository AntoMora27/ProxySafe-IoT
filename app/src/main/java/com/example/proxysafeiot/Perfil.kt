package com.example.proxysafeiot

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Perfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val nombre = findViewById<TextView>(R.id.tvnomper)
        val email = findViewById<TextView>(R.id.tvemaper)
        val tipo = findViewById<TextView>(R.id.tvtipper)
        val contrasena = findViewById<TextView>(R.id.tvconper)
        val imagen = findViewById<ImageView>(R.id.fotousuario)

        imagen.setImageResource(R.drawable.fotousuario)

        findViewById<Button>(R.id.btnvolper).setOnClickListener {
            finish()
        }

        val emailUsuario = intent.getStringExtra("emailUsuario")
            ?.trim()
            ?.lowercase()
            .orEmpty()

        if (emailUsuario.isBlank()) {
            Toast.makeText(
                this,
                "No se recibió el correo. Inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        FirebaseDatabase.getInstance()
            .getReference("Usuario")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    var usuarioEncontrado: DataSnapshot? = null

                    for (usuario in snapshot.children) {
                        val correoBD = usuario.child("email")
                            .getValue(String::class.java)
                            ?.trim()
                            ?.lowercase()

                        if (correoBD == emailUsuario) {
                            usuarioEncontrado = usuario
                            break
                        }
                    }

                    if (usuarioEncontrado == null) {
                        Toast.makeText(
                            this@Perfil,
                            "No se encontró el usuario en Realtime Database.",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    nombre.text = usuarioEncontrado.child("nombre")
                        .getValue(String::class.java) ?: "Sin nombre"

                    email.text = usuarioEncontrado.child("email")
                        .getValue(String::class.java) ?: "Sin correo"

                    tipo.text = usuarioEncontrado.child("tipo")
                        .getValue(String::class.java) ?: "Sin tipo"

                    contrasena.text = usuarioEncontrado.child("contraseña")
                        .getValue(String::class.java) ?: "No registrada"
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@Perfil,
                        "Error de Firebase: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}