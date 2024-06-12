package com.alexdevs.librosappkotlin.Administrador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.alexdevs.librosappkotlin.databinding.ItemLibroAdminBinding

class AdaptadorPdfAdmin : RecyclerView.Adapter<AdaptadorPdfAdmin.HolderPdfAdmin>, Filterable {

    private lateinit var binding: ItemLibroAdminBinding

    private var m_context : Context
    public var pdfArrayList : ArrayList<Modelopdf>
    private var filtroLibro : ArrayList<Modelopdf>
    private var filtro : FiltroPdfAdmin?=null

    constructor(m_context: Context, pdfArrayList: ArrayList<Modelopdf>) : super() {
        this.m_context = m_context
        this.pdfArrayList = pdfArrayList
        this.filtroLibro = pdfArrayList
    }


    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val VisualizadorPDF = binding.VisualizadorPDF
        val progressBar = binding.progressBar
        val Txt_titulo_libro_item = binding.TxtTituloLibroItem
        val Txt_descripcion_libro_item = binding.TxtDescripcionLibroItem
        val Txt_categoria_libro_admin = binding.TxtCategoriaLibroAdmin
        val Txt_tamaño_libro_admin = binding.TxtTamaOLibroAdmin
        val Txt_fecha_libro_admin = binding.TxtFechaLibroAdmin
        val Ib_mas_opciones = binding.IbMasOpciones


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        binding = ItemLibroAdminBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderPdfAdmin(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        val modelo = pdfArrayList[position]
        val pdfId = modelo.id
        val categoriaID = modelo.categoria
        val titulo = modelo.titulo
        val descripcion = modelo.descripcion
        val pdfUrl = modelo.url
        val tiempo = modelo.tiempo

        val formatoTiempo = MisFunciones.formatoTiempo(tiempo)

        holder.Txt_titulo_libro_item.text = titulo
        holder.Txt_descripcion_libro_item.text = descripcion
        holder.Txt_fecha_libro_admin.text = formatoTiempo

        MisFunciones.CargarCategoria(categoriaID, holder.Txt_categoria_libro_admin)
        MisFunciones.CargarPdfUrl(pdfUrl, titulo, holder.VisualizadorPDF, holder.progressBar, null)
        MisFunciones.CargarSizePDF(pdfUrl, titulo, holder.Txt_tamaño_libro_admin)

    }

    override fun getFilter(): Filter {
        if (filtro == null) {
            filtro = FiltroPdfAdmin(filtroLibro, this)
        }
        return filtro as FiltroPdfAdmin
    }
}