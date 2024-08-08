package com.alexdevs.librosappkotlin.Fragmentos_Cliente

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexdevs.librosappkotlin.Modelos.Modelopdf
import com.alexdevs.librosappkotlin.Cliente.AdaptadorPdfFavorito
import com.alexdevs.librosappkotlin.databinding.FragmentClienteFavoritosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Fragment_cliente_favoritos : Fragment() {

    private lateinit var binding: FragmentClienteFavoritosBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var librosArrayList: ArrayList<Modelopdf>
    private lateinit var adaptadorPdfFav: AdaptadorPdfFavorito
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentClienteFavoritosBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        librosArrayList = ArrayList()
        val refFavoritos = FirebaseDatabase.getInstance().getReference("Usuarios")
        refFavoritos.child(firebaseAuth.uid!!).child("Favoritos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    librosArrayList.clear()
                    for (ds in snapshot.children) {
                        val idLibro = "${ds.child("id").value}"

                        val refLibros = FirebaseDatabase.getInstance().getReference("Libros")
                        refLibros.child(idLibro).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshotLibro: DataSnapshot) {
                                if (snapshotLibro.exists()) {
                                    val modelopdf = Modelopdf()
                                    modelopdf.id = idLibro
                                    // Puedes agregar más detalles del libro aquí si es necesario
                                    librosArrayList.add(modelopdf)
                                } else {
                                    // Si el libro ya no existe, remuévelo de los favoritos
                                    refFavoritos.child(firebaseAuth.uid!!).child("Favoritos").child(ds.key!!).removeValue()
                                }
                                adaptadorPdfFav = AdaptadorPdfFavorito(mContext, librosArrayList)
                                binding.RvLibrosFavoritos.adapter = adaptadorPdfFav
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Manejo de errores si es necesario
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejo de errores si es necesario
                }
            })
    }

}