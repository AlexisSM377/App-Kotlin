package com.alexdevs.librosappkotlin.Fragmentos_Admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexdevs.librosappkotlin.Administrador.AdaptadorCategoria
import com.alexdevs.librosappkotlin.Administrador.Agregar_Categoria
import com.alexdevs.librosappkotlin.Administrador.ModeloCategoria
import com.alexdevs.librosappkotlin.R
import com.alexdevs.librosappkotlin.databinding.FragmentAdminDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Fragment_admin_dashboard : Fragment() {

    private lateinit var binding: FragmentAdminDashboardBinding
    private lateinit var mContext: Context

    private lateinit var  categoriaArrayList: ArrayList<ModeloCategoria>
    private lateinit var  adaptadorCategoria: AdaptadorCategoria

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ListarCategorias()

        binding.BtnAgregarCategoria.setOnClickListener{
            startActivity(Intent(mContext, Agregar_Categoria::class.java))
        }

        binding.AgregarPdf.setOnClickListener{

        }
    }

    private fun ListarCategorias() {
        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriaArrayList.add(modelo!!)
                }
                adaptadorCategoria = AdaptadorCategoria(mContext, categoriaArrayList)
                binding.categoriasRv.adapter = adaptadorCategoria
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}