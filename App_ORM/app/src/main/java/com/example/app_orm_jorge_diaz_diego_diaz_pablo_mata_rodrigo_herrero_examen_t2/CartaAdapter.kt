package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartaAdapter(
    private val onCartaClick: (Carta) -> Unit,
    private val onDeleteClick: (Carta) -> Unit
) : RecyclerView.Adapter<CartaAdapter.CartaViewHolder>() {

    private var listaCartas = listOf<Carta>()

    fun actualizarLista(nuevaLista: List<Carta>) {
        listaCartas = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carta, parent, false)
        return CartaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartaViewHolder, position: Int) {
        val carta = listaCartas[position]
        holder.bind(carta)

        holder.itemView.findViewById<View>(R.id.btnEliminarItem).setOnClickListener {
            onDeleteClick(carta)
        }

        holder.itemView.setOnClickListener {
            onCartaClick(carta)
        }
    }

    override fun getItemCount() = listaCartas.size

    class CartaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre = itemView.findViewById<TextView>(R.id.tvNombreCarta)
        val tvPrecio = itemView.findViewById<TextView>(R.id.tvPrecioCarta)

        fun bind(carta: Carta) {
            tvNombre.text = carta.nombre
            tvPrecio.text = "Estado: ${carta.estado} | ${carta.valorMercado}€"
        }
    }
}