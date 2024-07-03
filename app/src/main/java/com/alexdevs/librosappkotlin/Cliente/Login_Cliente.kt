package com.alexdevs.librosappkotlin.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alexdevs.librosappkotlin.MainActivityCliente
import com.alexdevs.librosappkotlin.R
import com.alexdevs.librosappkotlin.databinding.ActivityLoginClienteBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class Login_Cliente : AppCompatActivity() {

    private lateinit var binding: ActivityLoginClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLoginCliente.setOnClickListener {
            validarInformacion()
        }
        binding.BtnLoginGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun iniciarSesionGoogle() {
        val googleSignIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignIntent)
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){resultado->
        if (resultado.resultCode == RESULT_OK){
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                autenticarGoogleFirebase(cuenta.idToken)
            }catch (e: Exception){
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(applicationContext, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun autenticarGoogleFirebase(idToken: String?) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credencial)
            .addOnSuccessListener {authResult->
                if (authResult.additionalUserInfo!!.isNewUser){
                    GuardarInformacionBD()
                }else{
                    startActivity(Intent(this, MainActivityCliente::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e->
                Toast.makeText(applicationContext, "No se pudo iniciar sesión debido a: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun GuardarInformacionBD() {
        progressDialog.setMessage("Se esta registrando la información")
        progressDialog.show()

        //obtener la información del usuario de google
        val uidGoogle = firebaseAuth.uid
        val emailGoogle = firebaseAuth.currentUser?.email
        val nombreGoogle = firebaseAuth.currentUser?.displayName

        //convertir a string el nombre del usuario
        val nombre_usuario_google = nombreGoogle.toString()
        //obtener el tiempo
        val tiempo = System.currentTimeMillis()

        //guardar en la base de datos
        val datos_cliente = HashMap<String, Any?> ()
        datos_cliente["uid"] = uidGoogle
        datos_cliente["nombre"] = nombre_usuario_google
        datos_cliente["email"] = emailGoogle
        datos_cliente["edad"] = ""
        datos_cliente["tiempo_registro"] = tiempo
        datos_cliente["imagen"] = ""
        datos_cliente["rol"] = "cliente"

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidGoogle!!)
            .setValue(datos_cliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, MainActivityCliente::class.java))
                Toast.makeText(applicationContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener { e->
                Toast.makeText(applicationContext, "No se pudo registrar la información debido a: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private var email = ""
    private var password = ""
    private fun validarInformacion() {
        email = binding.EtEmailCliente.text.toString().trim()
        password = binding.EtPasswordCliente.text.toString().trim()

        //Validar
        if (email.isEmpty()){
            binding.EtEmailCliente.error = "El email es requerido"
            binding.EtEmailCliente.requestFocus()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailCliente.error = "Formato de email invalido"
            binding.EtEmailCliente.requestFocus()
        }
        else if (password.isEmpty()){
            binding.EtPasswordCliente.error = "La contraseña es requerida"
            binding.EtPasswordCliente.requestFocus()
        }
        else {
            loginCliente()
        }
    }

    private fun loginCliente() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@Login_Cliente, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo iniciar sesión debido a: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}