package com.example.proxysafeiot

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference
import com.google.android.material.textfield.TextInputLayout

class Usuarios : AppCompatActivity() {

    private lateinit var referencia: DatabaseReference

    private lateinit var txtId: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtContrasena: EditText
    private lateinit var spTipo: Spinner
    private lateinit var listado: LinearLayout

    private val tipos = arrayOf(
        "Administrador",
        "Prevencionista",
        "Supervisor"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)

        referencia = FirebaseDatabase.getInstance().getReference("Usuario")

        txtId = findViewById(R.id.txtIdUsuario)
        txtNombre = findViewById(R.id.txtNombreUsuario)
        txtEmail = findViewById(R.id.txtEmailUsuario)
        txtContrasena = findViewById(R.id.txtContrasenaUsuario)
        spTipo = findViewById(R.id.spTipoUsuario)
        listado = findViewById(R.id.listadoUsuarios)

        spTipo.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tipos
        )

        findViewById<Button>(R.id.btnRegistrarUsuario).setOnClickListener {
            registrarUsuario()
        }

        findViewById<Button>(R.id.btnModificarUsuario).setOnClickListener {
            modificarUsuario()
        }

        findViewById<Button>(R.id.btnEliminarUsuario).setOnClickListener {
            confirmarEliminarUsuario()
        }

        findViewById<Button>(R.id.btnVolverUsuarios).setOnClickListener {
            finish()
        }

        cargarUsuarios()
    }

    private fun registrarUsuario() {
        val id = txtId.text.toString().trim()
        val nombre = txtNombre.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val contrasena = txtContrasena.text.toString()
        val tipo = spTipo.selectedItem.toString()

        if (id.isEmpty() || nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
            mensaje("Completa todos los campos")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensaje("Ingresa un correo válido")
            return
        }

        if (contrasena.length < 6) {
            mensaje("La contraseña debe tener al menos 6 caracteres")
            return
        }

        referencia.get().addOnSuccessListener { snapshot ->
            if (snapshot.child(id).exists()) {
                mensaje("Ya existe un usuario con ese ID")
                return@addOnSuccessListener
            }

            var emailExiste = false

            for (registro in snapshot.children) {
                val emailExistente = registro.child("email").value?.toString() ?: ""
                if (emailExistente.equals(email, ignoreCase = true)) {
                    emailExiste = true
                    break
                }
            }

            if (emailExiste) {
                mensaje("Ese correo ya está registrado")
                return@addOnSuccessListener
            }

            val usuario = mapOf(
                "nombre" to nombre,
                "email" to email,
                "contraseña" to contrasena,
                "tipo" to tipo
            )

            referencia.child(id).setValue(usuario)
                .addOnSuccessListener {
                    mensaje("Usuario registrado correctamente")
                    limpiarFormulario()
                    cargarUsuarios()
                }
                .addOnFailureListener {
                    mensaje("Error al registrar usuario")
                }
        }.addOnFailureListener {
            mensaje("No se pudo consultar Firebase")
        }
    }

    private fun modificarUsuario() {
        val id = txtId.text.toString().trim()
        val nombre = txtNombre.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val contrasena = txtContrasena.text.toString()
        val tipo = spTipo.selectedItem.toString()

        if (id.isEmpty() || nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
            mensaje("Completa todos los campos")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensaje("Ingresa un correo válido")
            return
        }

        if (contrasena.length < 6) {
            mensaje("La contraseña debe tener al menos 6 caracteres")
            return
        }

        referencia.get().addOnSuccessListener { snapshot ->
            if (!snapshot.child(id).exists()) {
                mensaje("No existe un usuario con ese ID")
                return@addOnSuccessListener
            }

            var emailExiste = false

            for (registro in snapshot.children) {
                if (registro.key == id) continue

                val emailExistente = registro.child("email").value?.toString() ?: ""
                if (emailExistente.equals(email, ignoreCase = true)) {
                    emailExiste = true
                    break
                }
            }

            if (emailExiste) {
                mensaje("Ese correo pertenece a otro usuario")
                return@addOnSuccessListener
            }

            val cambios = mapOf(
                "nombre" to nombre,
                "email" to email,
                "contraseña" to contrasena,
                "tipo" to tipo
            )

            referencia.child(id).updateChildren(cambios)
                .addOnSuccessListener {
                    mensaje("Usuario modificado correctamente")
                    limpiarFormulario()
                    cargarUsuarios()
                }
                .addOnFailureListener {
                    mensaje("Error al modificar usuario")
                }
        }.addOnFailureListener {
            mensaje("No se pudo consultar Firebase")
        }
    }

    private fun confirmarEliminarUsuario() {
        val id = txtId.text.toString().trim()

        if (id.isEmpty()) {
            mensaje("Selecciona un usuario del listado")
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Eliminar usuario")
            .setMessage("¿Seguro que deseas eliminar el usuario con ID $id?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarUsuario(id)
            }
            .show()
    }

    private fun eliminarUsuario(id: String) {
        referencia.child(id).removeValue()
            .addOnSuccessListener {
                mensaje("Usuario eliminado correctamente")
                limpiarFormulario()
                cargarUsuarios()
            }
            .addOnFailureListener {
                mensaje("Error al eliminar usuario: ${it.message}")
            }
    }

    private fun cargarUsuarios() {
        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listado.removeAllViews()

                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    val vacio = TextView(this@Usuarios)
                    vacio.text = "No hay usuarios registrados"
                    vacio.textSize = 16f
                    vacio.setTextColor(Color.GRAY)
                    vacio.gravity = Gravity.CENTER
                    vacio.setPadding(12, 24, 12, 24)
                    listado.addView(vacio)
                    return
                }

                for (registro in snapshot.children) {
                    val id = registro.key ?: continue
                    val nombre = registro.child("nombre").value?.toString() ?: ""
                    val email = registro.child("email").value?.toString() ?: ""
                    val contrasena = registro.child("contraseña").value?.toString() ?: ""
                    val tipo = registro.child("tipo").value?.toString() ?: ""

                    val fila = TextView(this@Usuarios)
                    fila.text = "ID: $id\nNombre: $nombre\nCorreo: $email\nTipo: $tipo"
                    fila.textSize = 15f
                    fila.setTextColor(Color.DKGRAY)
                    fila.setPadding(16, 16, 16, 16)
                    fila.setBackgroundColor(Color.rgb(245, 245, 245))

                    val parametros = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    parametros.setMargins(0, 0, 0, 12)
                    fila.layoutParams = parametros

                    fila.setOnClickListener {
                        txtId.setText(id)
                        txtNombre.setText(nombre)
                        txtEmail.setText(email)
                        txtContrasena.setText(contrasena)

                        val posicion = tipos.indexOfFirst {
                            it.equals(tipo, ignoreCase = true)
                        }

                        spTipo.setSelection(if (posicion >= 0) posicion else 0)
                    }

                    listado.addView(fila)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                mensaje("Error al cargar usuarios: ${error.message}")
            }
        })
    }

    private fun limpiarFormulario() {
        txtId.text.clear()
        txtNombre.text.clear()
        txtEmail.text.clear()
        txtContrasena.text.clear()
        spTipo.setSelection(0)
    }

    private fun mensaje(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()
    }
}