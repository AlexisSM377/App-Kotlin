package com.alexdevs.librosappkotlin.Administrador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexdevs.librosappkotlin.databinding.ItemCategoriaAdminBinding

class AdaptadorCategoria : RecyclerView.Adapter<AdaptadorCategoria.HolderCategoria> {

    private lateinit var binding: ItemCategoriaAdminBinding

    private  val m_context: Context
    private  val categoriaArrayList : ArrayList<ModeloCategoria>

    constructor(m_context: Context, categoriaArrayList: ArrayList<ModeloCategoria>) {
        this.m_context = m_context
        this.categoriaArrayList = categoriaArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaAdminBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modelo = categoriaArrayList[position]
        val id = modelo.id
        val categoria = modelo.categoria
        val tiempo = modelo.tiempo
        val uid = modelo.uid

        holder.categoriaTv.text = categoria
        holder.eliminarCarIb.setOnClickListener {

        }
    }

    inner class HolderCategoria(itemView: View) : RecyclerView.ViewHolder(itemView){
        var categoriaTv : TextView = binding.ItemNombreCatA
        var eliminarCarIb : ImageButton = binding.EliminarCat
    }


}