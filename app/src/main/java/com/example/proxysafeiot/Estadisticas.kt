package com.example.proxysafeiot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Estadisticas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        findViewById<Button>(R.id.btnvolestadisticas).setOnClickListener {
            startActivity(Intent(this, Menu::class.java))
            finish()
        }
    }
}