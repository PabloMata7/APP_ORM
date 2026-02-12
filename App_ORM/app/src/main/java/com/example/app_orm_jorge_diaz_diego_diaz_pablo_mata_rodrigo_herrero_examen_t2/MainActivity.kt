package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
            val nombre = binding.Nombre.text.toString()
            val valor = binding.Valor.text.toString().toDoubleOrNull() ?: 0.0

            if (nombre.isNotEmpty()) {
                val nuevaCarta = Carta(nombre = nombre, valorMercado = valor)
                guardarEnMongo(nuevaCarta)
            }
        }

        val buttonAboutUs: Button = findViewById(R.id.button_about_us)
        buttonAboutUs.setOnClickListener {
            val intent = Intent(this, AboutUs::class.java)
            startActivity(intent)
        }

        val buttonSalir: Button = findViewById(R.id.button_salir)
        buttonSalir.setOnClickListener {
            finishAffinity()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            //asdf
        }
    }

    private fun guardarEnMongo(carta: Carta) {
        println("DEBUG: Iniciando guardado...") // Esto saldrá en el Logcat

        lifecycleScope.launch {
            try {
                println("DEBUG: Enviando carta a Retrofit: ${carta.nombre}")
                val response = RetrofitClient.instance.insertarCarta(carta)

                println("DEBUG: Respuesta recibida. Código: ${response.code()}")

                if (response.isSuccessful) {
                    println("DEBUG: ÉXITO")
                    Toast.makeText(this@MainActivity, getString(R.string.guardado_exito), Toast.LENGTH_SHORT).show()
                } else {
                    // Esto leerá el mensaje de error real que envía el servidor Node.js
                    val errorMsg = response.errorBody()?.string()
                    Log.e("THE_VAULT", "Error del servidor: $errorMsg")
                    Toast.makeText(this@MainActivity, getString(R.string.error_servidor, errorMsg), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                println("DEBUG: EXCEPCIÓN: ${e.message}")
                e.printStackTrace() // Esto te dirá exactamente qué falla
                Toast.makeText(this@MainActivity, getString(R.string.error_red), Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@MainActivity, getString(R.string.cartas_recibidas, listaCartas?.size ?: 0), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.error_servidor_general), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Capturamos errores de red (ej: sin internet o servidor apagado)
                Toast.makeText(this@MainActivity, getString(R.string.error_conexion, e.message), Toast.LENGTH_LONG).show()
            }
        }
    }
}