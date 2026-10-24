package com.example.proxysafeiot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ManipularESP32 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manipular_esp32)

        findViewById<Button>(R.id.btnon).setOnClickListener {
            Toast.makeText(this, "Activación pendiente de conectar al ESP32", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnoff).setOnClickListener {
            Toast.makeText(this, "Desactivación pendiente de conectar al ESP32", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnvolman).setOnClickListener {
            startActivity(Intent(this, Menu::class.java))
            finish()
        }
    }
}