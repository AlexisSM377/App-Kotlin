package com.alexdevs.librosappkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.Fragmentos_Admin.Fragment_admin_cuenta
import com.alexdevs.librosappkotlin.Fragmentos_Admin.Fragment_admin_dashboard
import com.alexdevs.librosappkotlin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        ComprobarSesion()
        VerFragmentoDashboard()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Menu_panel -> {
                    VerFragmentoDashboard()
                    true
                }

                R.id.Menu_cuenta -> {
                    VerFragmentoCuenta()
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

    private fun VerFragmentoDashboard() {
        val nombre_titulo = "Dashboard"
        binding.TituloRLAdmin.text = nombre_titulo

        val fragment = Fragment_admin_dashboard()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentAdmin.id, fragment, "Fragment Dashboard")
        fragmentTransaction.commit()
    }

    private fun VerFragmentoCuenta() {
        val nombre_titulo = "Mi Cuenta"
        binding.TituloRLAdmin.text = nombre_titulo

        val fragment = Fragment_admin_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentAdmin.id, fragment, "Fragment mi cuenta")
        fragmentTransaction.commit()
    }

    private fun ComprobarSesion() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, Elegir_rol::class.java))
            finishAffinity()
        } else {
            /*Toast.makeText(
                applicationContext,
                "Bienvenido(a) ${firebaseUser.email}",
                Toast.LENGTH_SHORT
            )
                .show()*/
        }
    }
}