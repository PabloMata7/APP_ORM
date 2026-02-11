package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview

class AboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        // 1. Configuración de la lista de datos
        val lista = ArrayList<ImagenModel>()
        lista.add(ImagenModel(R.drawable.rodri, "The Vault 1"))
        lista.add(ImagenModel(R.drawable.diego, "The Vault 2"))
        lista.add(ImagenModel(R.drawable.pablo, "The Vault 3"))
        lista.add(ImagenModel(R.drawable.jorge, "The Vault 4"))

        // 2. Configuración del Adaptador
        val adaptador = ImagenAdapter(lista)

        // 3. Configuración del Carrusel
        val carouselRecyclerview = findViewById<CarouselRecyclerview>(R.id.recycler)

        carouselRecyclerview.apply {
            this.adapter = adaptador
            set3DItem(false)
            setAlpha(false)
            setFlat(true)
            setInfinite(true)
        }

        // 4. Configurar el botón Volver
        val buttonVolver: Button = findViewById(R.id.button_volver)
        buttonVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 5. Configurar el botón Salir
        val buttonSalir: Button = findViewById(R.id.button_salir)
        buttonSalir.setOnClickListener {
            finishAffinity()
        }
    }
}