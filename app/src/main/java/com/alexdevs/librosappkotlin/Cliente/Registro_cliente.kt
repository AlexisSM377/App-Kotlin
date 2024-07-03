package com.alexdevs.librosappkotlin.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.MainActivityCliente
import com.alexdevs.librosappkotlin.R
import com.alexdevs.librosappkotlin.databinding.ActivityRegistroClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registro_cliente : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroClienteBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnRegistrarCliente.setOnClickListener {
            validarInformacion()
        }

        binding.TxtTengoCuenta.setOnClickListener {
            startActivity(Intent(this@Registro_cliente, Login_Cliente::class.java))
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    var nombres = ""
    var edad = ""
    var email = ""
    var password = ""
    var r_password = ""
    private fun validarInformacion() {

        nombres = binding.EtNombresCliente.text.toString().trim()
        edad = binding.EtEdadCliente.text.toString().trim()
        email = binding.EtEmailCliente.text.toString().trim()
        password = binding.EtPasswordCliente.text.toString().toString()
        r_password = binding.EtPasswordConfirmCliente.text.toString().toString()

        if (nombres.isEmpty()) {
            binding.EtNombresCliente.error = "Por favor ingrese su nombre"
            binding.EtNombresCliente.requestFocus()
        } else if (edad.isEmpty()) {
            binding.EtEdadCliente.error = "Por favor ingrese su edad"
            binding.EtEdadCliente.requestFocus()
        } else if (email.isEmpty()) {
            binding.EtEmailCliente.error = "Por favor ingrese su email"
            binding.EtEmailCliente.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EtEmailCliente.error = "Por favor ingrese un email valido"
            binding.EtEmailCliente.requestFocus()
        } else if (password.isEmpty()) {
            binding.EtPasswordCliente.error = "Por favor ingrese su contraseña"
            binding.EtPasswordCliente.requestFocus()
        } else if (password.length < 6) {
            binding.EtPasswordCliente.error = "La contraseña debe contener al menos 6 caracteres"
            binding.EtPasswordCliente.requestFocus()
        } else if (r_password.isEmpty()) {
            binding.EtPasswordConfirmCliente.error = "Confirme su contraseña"
            binding.EtPasswordConfirmCliente.requestFocus()
        } else if (password != r_password) {
            binding.EtPasswordConfirmCliente.error = "Las contraseñas no coinciden"
            binding.EtPasswordConfirmCliente.requestFocus()
        } else {
            crearCuentaCliente(email, password)
        }

    }

    private fun crearCuentaCliente(email: String, password: String) {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                agregarInfoDB()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Ha fallado el registro del cliente debido a: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun agregarInfoDB() {
        progressDialog.setMessage("Guardando informacion...")

        val tiempo = System.currentTimeMillis()
        val uid = firebaseAuth.uid!!

        val datos_cliente: HashMap<String, Any?> = HashMap()
        datos_cliente["uid"] = uid
        datos_cliente["nombres"] = nombres
        datos_cliente["edad"] = edad
        datos_cliente["email"] = email
        datos_cliente["rol"] = "cliente"
        datos_cliente["tiempo_registro"] = tiempo
        datos_cliente["imagen"] = ""

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid)
            .setValue(datos_cliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Cliente registrado correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Ha fallado el registro del cliente debido a: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}