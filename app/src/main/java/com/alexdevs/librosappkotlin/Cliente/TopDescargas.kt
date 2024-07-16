package com.alexdevs.librosappkotlin.Cliente

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.Administrador.Modelopdf
import com.alexdevs.librosappkotlin.R
import com.alexdevs.librosappkotlin.databinding.ActivityTopDescargasBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TopDescargas : AppCompatActivity() {

    private lateinit var binding: ActivityTopDescargasBinding
    private lateinit var pdfArrayList: ArrayList<Modelopdf>
    private lateinit var adaptadorPdfCliente: AdaptadorPdfCliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTopDescargasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topDescargados()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun topDescargados() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.orderByChild("contadorDescargas").limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val modelo = ds.getValue(Modelopdf::class.java)
                        pdfArrayList.add(modelo!!)
                    }
                    adaptadorPdfCliente = AdaptadorPdfCliente(this@TopDescargas, pdfArrayList)
                    binding.RvTopDescargados.adapter = adaptadorPdfCliente
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}