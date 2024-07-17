package com.alexdevs.librosappkotlin.Administrador

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.Modelos.ModeloCategoria
import com.alexdevs.librosappkotlin.R
import com.alexdevs.librosappkotlin.databinding.ActivityAgregarPdfBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Agregar_Pdf : AppCompatActivity() {

    private lateinit var binging: ActivityAgregarPdfBinding
    private lateinit var categoriaArrayList: ArrayList<ModeloCategoria>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var pdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binging = ActivityAgregarPdfBinding.inflate(layoutInflater)
        setContentView(binging.root)

        firebaseAuth = FirebaseAuth.getInstance()
        CargarCategorias()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        binging.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binging.AdjuntarPdfIb.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@Agregar_Pdf,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                ElegirPdf()
            } else {
                SolicitarPermisos.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        binging.TvCategoriaLibro.setOnClickListener {
            SeleccionarCategoria()
        }

        binging.BtnSubirLibro.setOnClickListener {
            ValidarInformacion()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private var titulo = ""
    private var descripcion = ""
    private var categoria = ""
    private fun ValidarInformacion() {
        titulo = binging.EtTituloLibro.text.toString().trim()
        descripcion = binging.EtDescripcionLibro.text.toString().trim()
        categoria = binging.TvCategoriaLibro.text.toString().trim()

        if (titulo.isEmpty()) {
            Toast.makeText(this, "Ingrese un titulo", Toast.LENGTH_SHORT).show()
        } else if (descripcion.isEmpty()) {
            Toast.makeText(this, "Ingrese una descripcion", Toast.LENGTH_SHORT).show()
        } else if (categoria.isEmpty()) {
            Toast.makeText(this, "Seleccione una categoria", Toast.LENGTH_SHORT).show()
        } else if (pdfUri == null) {
            Toast.makeText(this, "Adjunte un libro", Toast.LENGTH_SHORT).show()
        } else {
            SubirPdfStore()
        }

    }


    private fun SubirPdfStore() {
        progressDialog.setMessage("Subiendo pdf")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()
        val ruta_libro = "Libros/$tiempo"
        val storageReference = FirebaseStorage.getInstance().getReference(ruta_libro)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener { tarea ->
                val uriTask: Task<Uri> = tarea.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val UrlPdfSubido = "${uriTask.result}"
                SubirPdfBD(UrlPdfSubido, tiempo)

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Error al subir el archivo debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }

    private fun SubirPdfBD(UrlPdfSubido: String, tiempo: Long) {
        progressDialog.setMessage("Subiendo pdf a la base de datos")
        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$tiempo"
        hashMap["titulo"] = titulo
        hashMap["descripcion"] = descripcion
        hashMap["categoria"] = id_categoria
        hashMap["url"] = UrlPdfSubido
        hashMap["tiempo"] = tiempo
        hashMap["contadorVistas"] = 0
        hashMap["contadorDescargas"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Libro subido correctamente", Toast.LENGTH_SHORT).show()
                binging.EtTituloLibro.setText("")
                binging.EtDescripcionLibro.setText("")
                binging.TvCategoriaLibro.setText("")
                pdfUri = null
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Error al subir el libro debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun CargarCategorias() {
        categoriaArrayList = ArrayList()
        val ref =
            FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriaArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private var id_categoria = ""
    private var titulo_categoria = ""
    private fun SeleccionarCategoria() {
        val categoriasArray = arrayOfNulls<String>(categoriaArrayList.size)
        for (i in categoriaArrayList.indices) {
            categoriasArray[i] = categoriaArrayList[i].categoria
        }

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Seleccionar Categoria")
            .setItems(categoriasArray) { dialog, which ->
                id_categoria = categoriaArrayList[which].id
                titulo_categoria = categoriaArrayList[which].categoria
                binging.TvCategoriaLibro.text = titulo_categoria
            }
            .show()
    }

    private fun ElegirPdf() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityRL.launch(intent)
    }

    val pdfActivityRL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                pdfUri = resultado.data!!.data
            } else {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }

    )

    private var SolicitarPermisos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { Permiso ->
            if (Permiso) {
                //Permiso Concedido
                ElegirPdf()
            } else {
                //Permiso Denegado
                Toast.makeText(
                    this,
                    "Permiso denegado para acceder a la galeria",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

}