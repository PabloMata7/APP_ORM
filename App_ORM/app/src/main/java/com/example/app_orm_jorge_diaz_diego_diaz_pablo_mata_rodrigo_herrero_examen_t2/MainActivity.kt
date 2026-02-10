package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Ejemplo de cómo llamar a MongoDB al pulsar un botón
        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val valor = binding.etValor.text.toString().toDoubleOrNull() ?: 0.0

            if (nombre.isNotEmpty()) {
                val nuevaCarta = Carta(nombre = nombre, valorMercado = valor)
                guardarEnMongo(nuevaCarta)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            //asdf
        }
    }

    private fun guardarEnMongo(carta: Carta) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.insertarCarta(carta)
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Guardado en The Vault", Toast.LENGTH_SHORT).show()
                    binding.etNombre.text.clear()
                    binding.etValor.text.clear()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Fallo de red: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarDatosDesdeMongo() {
        // Usamos lifecycleScope (Corrutinas) para no bloquear la UI
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.obtenerTodasLasCartas()

                if (response.isSuccessful) {
                    val listaCartas = response.body()
                    // Aquí actualizarías tu RecyclerView o UI con los datos
                    Toast.makeText(this@MainActivity, "Cartas recibidas: ${listaCartas?.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Error en el servidor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Capturamos errores de red (ej: sin internet o servidor apagado)
                Toast.makeText(this@MainActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}