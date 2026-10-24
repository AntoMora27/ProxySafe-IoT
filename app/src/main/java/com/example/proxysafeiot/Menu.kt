
package com.example.proxysafeiot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Menu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnManipular = findViewById<Button>(R.id.btn1)
        val btnAcciones = findViewById<Button>(R.id.btn2)
        val btnEstadisticas = findViewById<Button>(R.id.btn3)
        val btnPerfil = findViewById<Button>(R.id.btnperfil)
        val btnCerrarSesion = findViewById<Button>(R.id.btn4)
        val btnUsuarios = findViewById<Button>(R.id.btnUsuarios)

        val emailUsuario = intent.getStringExtra("emailUsuario")

        btnManipular.setOnClickListener {
            startActivity(Intent(this, ManipularESP32::class.java))
        }

        btnAcciones.setOnClickListener {
            startActivity(Intent(this, Acciones::class.java))
        }

        btnEstadisticas.setOnClickListener {
            startActivity(Intent(this, Estadisticas::class.java))
        }

        btnPerfil.setOnClickListener {
            if (emailUsuario.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    "No se recibió el correo. Inicia sesión nuevamente.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val intentPerfil = Intent(this, Perfil::class.java)
                intentPerfil.putExtra("emailUsuario", emailUsuario)
                startActivity(intentPerfil)
            }
        }

        btnUsuarios.setOnClickListener {
            startActivity(Intent(this, Usuarios::class.java))
        }

        btnCerrarSesion.setOnClickListener {
            val intentLogin = Intent(this, Principal::class.java)
            intentLogin.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentLogin)
            finish()
        }
    }
}
