package com.alexdevs.librosappkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.databinding.ActivityMainClienteBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class MainActivityCliente : AppCompatActivity() {

    private lateinit var binding: ActivityMainClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun comprobarSesion() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, Elegir_rol::class.java))
            finishAffinity()
        }else{
            Toast.makeText(applicationContext, "Bienvenido(a) ${firebaseUser.email}" , Toast.LENGTH_SHORT).show()
        }
    }
}