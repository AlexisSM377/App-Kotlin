package com.alexdevs.librosappkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.Fragmentos_Cliente.Fragment_cliente_chat
import com.alexdevs.librosappkotlin.Fragmentos_Cliente.Fragment_cliente_cuenta
import com.alexdevs.librosappkotlin.Fragmentos_Cliente.Fragment_cliente_favoritos
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

        verFragmentoDashboard()

        binding.BottomNavCliente.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Menu_dashboard_cl -> {
                    verFragmentoDashboard()
                    true
                }

                R.id.Menu_favoritos_cl -> {
                    verFragmentoFavoritos()
                    true
                }

                R.id.Menu_ia_cl -> {
                    verFragmentoChatIA()
                    true
                }

                R.id.Menu_cuenta_cl -> {
                    verFragmentoCuenta()
                    true
                }

                else -> {
                    false
                }
            }
        }

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
        } else {
            Toast.makeText(
                applicationContext,
                "Bienvenido(a) ${firebaseUser.email}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun verFragmentoDashboard(){
        val nombre_titulo = "Dashboard"
        binding.TituloRLCliente.text = nombre_titulo

        val fragment = Fragment_cliente_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsCliente.id, fragment, "Fragment dashboard")
        fragmentTransaction.commit()

    }

    private fun verFragmentoFavoritos(){
        val nombre_titulo = "Favoritos"
        binding.TituloRLCliente.text = nombre_titulo

        val fragment = Fragment_cliente_favoritos()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsCliente.id, fragment, "Fragment favoritos")
        fragmentTransaction.commit()
    }

    private fun verFragmentoChatIA(){
        val nombre_titulo = "Chat IA"
        binding.TituloRLCliente.text = nombre_titulo

        val fragment = Fragment_cliente_chat()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsCliente.id, fragment, "Fragment chat")
        fragmentTransaction.commit()
    }

    private fun verFragmentoCuenta(){
        val nombre_titulo = "Cuenta"
        binding.TituloRLCliente.text = nombre_titulo

        val fragment = Fragment_cliente_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsCliente.id, fragment, "Fragment cuenta")
        fragmentTransaction.commit()
    }
}