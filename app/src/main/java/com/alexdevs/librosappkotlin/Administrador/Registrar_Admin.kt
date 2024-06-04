package com.alexdevs.librosappkotlin.Administrador

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.MainActivity
import com.alexdevs.librosappkotlin.R
import com.alexdevs.librosappkotlin.databinding.ActivityRegistrarAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registrar_Admin : AppCompatActivity() {

    private lateinit var binging: ActivityRegistrarAdminBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binging = ActivityRegistrarAdminBinding.inflate(layoutInflater)
        setContentView(binging.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)


        binging.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binging.BtnRegistrarAdmin.setOnClickListener {
            ValidarInformacion()
        }
    }

    var nombres = ""
    var email = ""
    var password = ""
    var r_password = ""


    private fun ValidarInformacion() {
        nombres = binging.EtNombresAdmin.text.toString().trim()
        email = binging.EtEmailAdmin.text.toString().trim()
        password = binging.EtPasswordAdmin.text.toString().trim()
        r_password = binging.EtPasswordConfirmAdmin.text.toString().trim()

        if (nombres.isEmpty()) {
            binging.EtNombresAdmin.error = "Ingrese su nombre"
            binging.EtNombresAdmin.requestFocus()
        } else if (email.isEmpty()) {
            binging.EtEmailAdmin.error = "Ingrese su email"
            binging.EtEmailAdmin.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binging.EtEmailAdmin.error = "Ingrese un email valido"
            binging.EtEmailAdmin.requestFocus()
        } else if (password.isEmpty()) {
            binging.EtPasswordAdmin.error = "Ingrese su contraseña"
            binging.EtPasswordAdmin.requestFocus()
        } else if (password.length < 6) {
            binging.EtPasswordAdmin.error = "La contraseña debe contener al menos 6 caracteres"
            binging.EtPasswordAdmin.requestFocus()
        } else if (r_password.isEmpty()) {
            binging.EtPasswordConfirmAdmin.error = "Reingrese su contraseña"
            binging.EtPasswordConfirmAdmin.requestFocus()
        } else if (password != r_password) {
            binging.EtPasswordConfirmAdmin.error = "Las contraseñas no coinciden"
            binging.EtPasswordConfirmAdmin.requestFocus()
        } else {
            CrearCuentaAdmin(email, password)
        }
    }


    private fun CrearCuentaAdmin(email: String, password: String) {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                AgregarInfoBD()
            }

            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Ha fallado la creacion de la cuenta debido a ${e.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }

    private fun AgregarInfoBD() {
        progressDialog.setMessage("Guardando informacion...")
        val tiempo = System.currentTimeMillis()
        val uid = firebaseAuth.uid

        val datos_admin: HashMap<String, Any?> = HashMap()
        datos_admin["uid"] = uid
        datos_admin["nombres"] = nombres
        datos_admin["email"] = email
        datos_admin["rol"] = "admin"
        datos_admin["tiempo_registro"] = tiempo
        datos_admin["imagen"] = ""

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datos_admin)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Cuenta creada exitosamente", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Ha fallado la creacion de la cuenta debido a ${e.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

    }
}