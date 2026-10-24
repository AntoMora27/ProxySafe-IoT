
package com.example.proxysafeiot

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Acciones : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acciones)

        val usuario = findViewById<TextView>(R.id.tvusuacciones)
        val accion1 = findViewById<TextView>(R.id.tvaccion1)
        val accion2 = findViewById<TextView>(R.id.tvaccion2)
        val accion3 = findViewById<TextView>(R.id.tvaccion3)

        usuario.text = "Historial de detección"

        accion1.text = "Detección de proximidad\nEsperando conexión con el sensor..."
        accion2.text = "Última detección\nTodavía no hay registros"
        accion3.text = "Estado del sistema\nESP32 pendiente de conexión"

        findViewById<Button>(R.id.btnvolacc).setOnClickListener {
            finish()
        }
    }
}