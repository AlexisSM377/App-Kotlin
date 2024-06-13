package com.alexdevs.librosappkotlin.Administrador

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.R
import com.alexdevs.librosappkotlin.databinding.ActivityActualizarLibroBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActualizarLibro : AppCompatActivity() {

    private lateinit var binding: ActivityActualizarLibroBinding

    private var idLibro = ""

    private lateinit var progressDialog: ProgressDialog

    //Titulos y Ids
    private lateinit var catTituloArrayList: ArrayList<String>
    private lateinit var catIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityActualizarLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        idLibro = intent.getStringExtra("idLibro")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere un momento")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarCategorias()
        cargarInformacion()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.TvCategoriaLibro.setOnClickListener {
            dialogCategorias()
        }

        binding.BtnActualizarLibro.setOnClickListener {
            validarInformacion()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obetner la informacion en tiempo real del lirbo seleccionado
                    val titulo = snapshot.child("titulo").value.toString()
                    val descripcion = snapshot.child("descripcion").value.toString()
                    id_seleccionado = snapshot.child("categoria").value.toString()

                    //Setear los datos en la interfaz
                    binding.EtTituloLibro.setText(titulo)
                    binding.EtDescripcionLibro.setText(descripcion)

                    val refCategorias = FirebaseDatabase.getInstance().getReference("Categorias")
                    refCategorias.child(id_seleccionado)
                        .addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //Obtener categoria
                                val categoria = snapshot.child("categoria").value
                                binding.TvCategoriaLibro.text = categoria.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private var titulo = ""
    private var descripcion = ""
    private fun validarInformacion() {
        //obtener datos ingresados
        titulo = binding.EtTituloLibro.text.toString().trim()
        descripcion = binding.EtDescripcionLibro.text.toString().trim()

        //validar datos
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Ingrese un titulo", Toast.LENGTH_SHORT).show()
        } else if (descripcion.isEmpty()) {
            Toast.makeText(this, "Ingrese descripcion", Toast.LENGTH_SHORT).show()
        } else if (id_seleccionado.isEmpty()) {
            Toast.makeText(this, "Seleccione una categoria", Toast.LENGTH_SHORT).show()
        } else {
            actualizarInformacion()
        }
    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Actualizando informacion")
        progressDialog.show()
        val hashMap = HashMap<String, Any>()
        hashMap["titulo"] = "$titulo"
        hashMap["descripcion"] = "$descripcion"
        hashMap["categoria"] = "$id_seleccionado"

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Informacion actualizada exitosamente", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Error al actualizar la informacion ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private var id_seleccionado = ""
    private var titulo_seleccionado = ""
    private fun dialogCategorias() {
        val categoriaArray = arrayOfNulls<String>(catTituloArrayList.size)
        for (i in catTituloArrayList.indices) {
            categoriaArray[i] = catTituloArrayList[i]
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona una categoria")
            .setItems(categoriaArray) { dialog, posicion ->
                id_seleccionado = catIdArrayList[posicion]
                titulo_seleccionado = catTituloArrayList[posicion]
                binding.TvCategoriaLibro.text = titulo_seleccionado
            }
            .show()
    }

    private fun cargarCategorias() {
        catTituloArrayList = ArrayList()
        catIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                catTituloArrayList.clear()
                catIdArrayList.clear()
                for (ds in snapshot.children) {
                    val id = "" + ds.child("id").value
                    val categoria = "" + ds.child("categoria").value
                    catTituloArrayList.add(categoria)
                    catIdArrayList.add(id)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}