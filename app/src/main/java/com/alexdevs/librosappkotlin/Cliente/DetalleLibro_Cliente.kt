package com.alexdevs.librosappkotlin.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.Administrador.Constantes
import com.alexdevs.librosappkotlin.Administrador.MisFunciones
import com.alexdevs.librosappkotlin.LeerLibro
import com.alexdevs.librosappkotlin.R
import com.alexdevs.librosappkotlin.databinding.ActivityDetalleLibroClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class DetalleLibro_Cliente : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleLibroClienteBinding
    private var idLibro = ""

    private var tituloLibro = ""
    private var urlLibro = ""

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private var esFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetalleLibroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getStringExtra("idLibro")!!

        MisFunciones.incrementarVistas(idLibro)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere un momento")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLeerLibroC.setOnClickListener {
            val intent = Intent(this@DetalleLibro_Cliente, LeerLibro::class.java)
            intent.putExtra("idLibro", idLibro)
            startActivity(intent)
        }

        binding.BtnDescargarLibroC.setOnClickListener {
            if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                descargarLibro()
            }else{
                permisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        binding.BtnFavoritosLibroC.setOnClickListener {
            if (esFavorito){
                MisFunciones.eliminarFavoritos(this@DetalleLibro_Cliente, idLibro)
            }else{
                agregarFavoritos()
            }
        }

        comprobarFavoritos()
        cargarDetalleLibro()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun comprobarFavoritos() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idLibro)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    esFavorito = snapshot.exists()
                    if (esFavorito){
                        binding.BtnFavoritosLibroC.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_agregar_favorito,
                            0,
                            0
                        )
                        binding.BtnFavoritosLibroC.text = "Eliminar"
                    }else{
                        binding.BtnFavoritosLibroC.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_no_favorito,
                            0,
                            0
                        )
                        binding.BtnFavoritosLibroC.text = "Favoritos"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun agregarFavoritos() {
        val tiempo = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = idLibro
        hashMap["tiempo"] = tiempo

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idLibro)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Libro añadido a favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(applicationContext, "Error al añadir el libro a favoritos ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }


    private fun descargarLibro() {
        progressDialog.setMessage("Descargando Libro")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlLibro)
        storageReference.getBytes(Constantes.Maximo_bytes_pdf)
            .addOnSuccessListener { bytes ->
                guardarLibroDisp(bytes)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Error al descargar el libro ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarLibroDisp(bytes: ByteArray) {
        val nombreLibro_extencion = "$tituloLibro"+System.currentTimeMillis()+".pdf"
        try {
            val carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            carpeta.mkdirs()

            val archivo_ruta = carpeta.path+"/"+nombreLibro_extencion
            val out = FileOutputStream(archivo_ruta)
            out.write(bytes)
            out.close()


            Toast.makeText(applicationContext, "Libro descargado exitosamente", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            incrementarNumDesc()

        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error al descargar el libro ${e.message}", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }
    }

    private fun cargarDetalleLibro() {
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obetener la informacion del libro a traves del id
                    val categoria = "${snapshot.child("categoria").value}"
                    val contadorDescargas = "${snapshot.child("contadorDescargas").value}"
                    val contadorVistas = "${snapshot.child("contadorVistas").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    tituloLibro = "${snapshot.child("titulo").value}"
                    urlLibro = "${snapshot.child("url").value}"

                    //Formato de tiempo
                    val fecha = MisFunciones.formatoTiempo(tiempo.toLong())
                    //Cargar categoria del libro
                    MisFunciones.CargarCategoria(categoria, binding.categoriaD)
                    //Cargar la miniatura del libro,contador de paginas
                    MisFunciones.CargarPdfUrl(
                        "$urlLibro",
                        "$tituloLibro",
                        binding.VisualizadorPDF,
                        binding.progressBar,
                        binding.paginasD
                    )
                    //Cargar tamaño pdf
                    MisFunciones.CargarSizePDF("$urlLibro", "$tituloLibro", binding.sizeD)

                    //Cargar la informacion del libro
                    binding.tituloDetalleLibro.text = tituloLibro
                    binding.descripcionD.text = descripcion
                    binding.vistasD.text = contadorVistas
                    binding.descargasD.text = contadorDescargas
                    binding.fechaD.text = fecha

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun incrementarNumDesc(){
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.child(idLibro)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var contDescarActual = "${snapshot.child("contadorDescargas").value}"

                    if (contDescarActual == "" || contDescarActual == "null"){
                        contDescarActual = "0"
                    }
                    val nuevaDesc = contDescarActual.toLong() + 1

                    val hashMap = HashMap<String, Any>()
                    hashMap["contadorDescargas"] = nuevaDesc

                    val BDRef = FirebaseDatabase.getInstance().getReference("Libros")
                    BDRef.child(idLibro)
                        .updateChildren(hashMap)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private val permisoAlmacenamiento = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){permiso_concedido ->
        if (permiso_concedido){
            descargarLibro()
        }else{
            Toast.makeText(applicationContext, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }
}