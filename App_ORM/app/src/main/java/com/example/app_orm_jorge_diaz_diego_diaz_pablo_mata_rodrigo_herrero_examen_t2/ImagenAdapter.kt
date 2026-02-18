package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

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
        // Forzamos a Glide a no usar la caché para evitar este tipo de problemas.
        Glide.with(holder.image.context)
            .load(lista[position].img)
            .diskCacheStrategy(DiskCacheStrategy.NONE) // No usar la caché de disco.
            .skipMemoryCache(true) // No usar la caché de memoria.
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}