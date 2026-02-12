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
            val nombre = binding.etNombre.text.toString()
            val valor = binding.etValor.text.toString().toDoubleOrNull() ?: 0.0

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
<<<<<<< Updated upstream
=======
        // Botón Listar (CRUD: Read)
        binding.btnCargarCartas.setOnClickListener {
            sincronizarYMostrar()
        }

        // Botón "Sobre Nosotros"
        findViewById<Button>(R.id.btnAboutUs).setOnClickListener {
            startActivity(Intent(this, AboutUs::class.java))
        }

        // Botón "Salir"
        findViewById<Button>(R.id.btnSalir).setOnClickListener {
            finishAffinity()
        }
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
}
=======
    private fun cargarDeRoom() {
        lifecycleScope.launch(Dispatchers.IO) {
            // Obtenemos la lista de la base de datos
            val lista = db.cartaDao().obtenerTodas()

            // Volvemos al hilo principal para tocar la interfaz (UI)
            withContext(Dispatchers.Main) {
                if (::adapter.isInitialized) { // Verificamos que el adapter existe
                    adapter.actualizarLista(lista)
                }
            }
        }
    }

    // 2. Función para borrar una carta
    private fun borrarCarta(carta: Carta) {
        lifecycleScope.launch(Dispatchers.IO) {
            var mensaje = ""
            try {
                // 1. Borrar de MongoDB
                if (carta.idMongo != null) {
                    val response = RetrofitClient.instance.eliminarCarta(carta.idMongo)
                    if (response.isSuccessful) {
                        mensaje = "Carta eliminada de la nube. "
                    } else {
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@MainActivity, "Error al eliminar de la nube", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // 2. Borrar de Room
                db.cartaDao().borrarCarta(carta)
                mensaje += "Carta eliminada de la base de datos local."

                // 3. Actualizar UI
                withContext(Dispatchers.Main) {
                    cargarDeRoom()
                    Toast.makeText(this@MainActivity, mensaje, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun sincronizarYMostrar() {
        // Mostramos un aviso de carga
        Toast.makeText(this, "Sincronizando con la Nube...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. DESCARGAR DE MONGO
                val response = RetrofitClient.instance.obtenerTodasLasCartas() //

                if (response.isSuccessful && response.body() != null) {
                    val listaNube = response.body()!!

                    // 2. LIMPIEZA (Opcional: Para no duplicar datos antiguos)
                    // Si quieres mezclar lo local y lo nube, quita esta línea.
                    // Si quieres que el móvil sea un espejo exacto de la nube, déjala.
                    db.cartaDao().borrarTodas()

                    // 3. GUARDAR EN ROOM
                    listaNube.forEach { cartaNube ->
                        // TRUCO IMPORTANTE:
                        // La carta viene de Mongo con idLocal=0 o null.
                        // Al guardarla, Room le asignará un nuevo ID autoincremental (1, 2, 3...)
                        // Asegúrate de que idLocal sea 0 para que Room sepa que es nueva.
                        val cartaParaRoom = cartaNube.copy(idLocal = 0)

                        db.cartaDao().insertarCarta(cartaParaRoom) //
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Sin conexión: Mostrando datos locales", Toast.LENGTH_SHORT).show()
                }
            }

            // 4. MOSTRAR LOS DATOS (Siempre desde Room)
            // Tanto si hubo internet (datos nuevos) como si no (datos viejos),
            // leemos de la base de datos local.
            val listaFinal = db.cartaDao().obtenerTodas()

            withContext(Dispatchers.Main) {
                if (::adapter.isInitialized) {
                    adapter.actualizarLista(listaFinal) //
                    if (listaFinal.isEmpty()) {
                        Toast.makeText(this@MainActivity, "No hay cartas ni en Local ni en Nube", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    private fun mostrarDialogoEditar(carta: Carta) {
        val context = this
        val layout = android.widget.LinearLayout(context)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etEstado = android.widget.EditText(context)
        etEstado.hint = "Estado (ej: NM)"
        etEstado.setText(carta.estado)

        val etValor = android.widget.EditText(context)
        etValor.hint = "Valor Mercado"
        etValor.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        etValor.setText(carta.valorMercado.toString())

        val etPrecio = android.widget.EditText(context)
        etPrecio.hint = "Precio Venta"
        etPrecio.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        etPrecio.setText(carta.precioVenta.toString())

        layout.addView(etEstado)
        layout.addView(etValor)
        layout.addView(etPrecio)

        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("Editar ${carta.nombre}")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoEstado = etEstado.text.toString()
                val nuevoValor = etValor.text.toString().toDoubleOrNull() ?: 0.0
                val nuevoPrecio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0

                // Creamos la carta modificada
                val cartaActualizada = carta.copy(
                    estado = nuevoEstado,
                    valorMercado = nuevoValor,
                    precioVenta = nuevoPrecio
                )
                actualizarCarta(cartaActualizada)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // 2. Guardar los cambios en Room y Mongo
    private fun actualizarCarta(carta: Carta) {
        lifecycleScope.launch(Dispatchers.IO) {
            // A. Actualizar en Mongo (Si tiene ID)
            if (!carta.idMongo.isNullOrEmpty()) {
                try {
                    RetrofitClient.instance.actualizarCarta(carta.idMongo, carta)
                } catch (e: Exception) {
                    // Si falla internet, seguimos para guardar en local
                }
            }

            // B. Actualizar en Room (Siempre)
            db.cartaDao().actualizarCarta(carta)

            // C. Refrescar pantalla
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Carta actualizada", Toast.LENGTH_SHORT).show()
                cargarDeRoom()
            }
        }
    }
    private fun limpiarCampos() {
        binding.Nombre.text.clear()
        binding.Franquicia.text.clear()
        binding.Coleccion.text.clear()
        binding.Edicion.text.clear()
        binding.Estado.text.clear()
        binding.Valor.text.clear()
        binding.Precio.text.clear()
    }
}
>>>>>>> Stashed changes
