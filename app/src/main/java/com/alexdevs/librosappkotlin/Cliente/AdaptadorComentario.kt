package com.alexdevs.librosappkotlin.Cliente

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.alexdevs.librosappkotlin.Administrador.MisFunciones
import com.alexdevs.librosappkotlin.Modelos.ModeloComentario
import com.alexdevs.librosappkotlin.R
import com.alexdevs.librosappkotlin.databinding.ItemComentarioBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorComentario : RecyclerView.Adapter<AdaptadorComentario.HolderComentario> {

    val context: Context
    val comentarioArrayList: ArrayList<ModeloComentario>

    private lateinit var binding: ItemComentarioBinding

    constructor(context: Context, comentarioArrayList: ArrayList<ModeloComentario>) {
        this.context = context
        this.comentarioArrayList = comentarioArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComentario {
        binding = ItemComentarioBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderComentario(binding.root)
    }

    override fun getItemCount(): Int {
        return comentarioArrayList.size
    }

    override fun onBindViewHolder(holder: HolderComentario, position: Int) {
        val modelo = comentarioArrayList[position]

        val id = modelo.id
        val idLibro = modelo.idLibro
        val comentario = modelo.comentario
        val uid = modelo.uid
        val tiempo = modelo.tiempo

        val fecha = MisFunciones.formatoTiempo(tiempo.toLong())

        holder.Tv_fecha.text = fecha
        holder.Tv_comentario.text = comentario

        cargarInformacion(modelo, holder)

        holder.itemView.setOnClickListener { 
            dialogEliminarComentario(modelo, holder)
        }

    }

    private fun dialogEliminarComentario(modelo: ModeloComentario, holder: AdaptadorComentario.HolderComentario) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Eliminar Comentario")
            .setMessage("¿Desea eliminar el comentario?")
            .setPositiveButton("Eliminar"){d,e->
                val idLibro = modelo.idLibro
                val idCom = modelo.id

                val ref = FirebaseDatabase.getInstance().getReference("Libros")
                ref.child(idLibro).child("Comentarios").child(idCom)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Comentario eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e->
                        Toast.makeText(context, "Error al eliminar el comentario ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar"){ d,e->
                d.dismiss()
            }
            .show()
    }

    private fun cargarInformacion(
        modelo: ModeloComentario,
        holder: AdaptadorComentario.HolderComentario
    ) {
        val uid = modelo.uid

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = "${snapshot.child("nombre").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    holder.Tv_nombre.text = nombre

                    try {
                        Glide.with(context)
                            .load(imagen)
                            .placeholder(R.drawable.ic_perfil)
                            .into(holder.Iv_imagen)
                    } catch (e: Exception) {
                        holder.Iv_imagen.setImageResource(R.drawable.ic_perfil)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    inner class HolderComentario(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Iv_imagen = binding.IvImgPerfil
        val Tv_nombre = binding.TvNombreC
        val Tv_fecha = binding.TvFechaC
        val Tv_comentario = binding.TvComentarioC
    }


}