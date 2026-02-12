package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImagenAdapter(
    var lista: ArrayList<ImagenModel>
) : RecyclerView.Adapter<ImagenAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Este ID debe ser el mismo que el del archivo item_rv_imagen.xml
        val image: ImageView = itemView.findViewById(R.id.image) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_imagen, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Aquí usamos .image y .img porque es como lo definimos en el modelo
        Glide.with(holder.image.context)
            .load(lista[position].img)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}