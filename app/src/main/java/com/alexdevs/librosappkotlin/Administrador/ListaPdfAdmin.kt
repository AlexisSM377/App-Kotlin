package com.alexdevs.librosappkotlin.Administrador

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.R

class ListaPdfAdmin : AppCompatActivity() {

    private var idCategoria = ""
    private var tituloCategoria = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_pdf_admin)

        val intent = intent
        idCategoria = intent.getStringExtra("idCategoria")!!
        tituloCategoria = intent.getStringExtra("tituloCategoria")!!

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}