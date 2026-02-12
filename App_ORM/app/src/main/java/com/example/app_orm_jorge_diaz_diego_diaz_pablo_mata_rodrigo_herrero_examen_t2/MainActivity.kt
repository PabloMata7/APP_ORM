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
import androidx.room.Room
import androidx.room.util.copy
import com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: CartaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicialización de Room
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "the_vault_db"
        ).fallbackToDestructiveMigration(true).build()
        adapter = CartaAdapter(
            // Acción al pulsar la fila (Editar)
            onCartaClick = { carta -> mostrarDialogoEditar(carta) },

            // Acción al pulsar la papelera (Borrar)
            onDeleteClick = { carta -> borrarCarta(carta) }
        )

        binding.rvCartas.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.rvCartas.adapter = adapter
        // ejemplo de cómo llamar a MongoDB al pulsar un botón
        binding.btnGuardar.setOnClickListener {
            val nombre = binding.Nombre.text.toString()
            val franquicia = binding.Franquicia.text.toString()
            val coleccion = binding.Coleccion.text.toString()
            val rareza = binding.Edicion.text.toString()
            val estado = binding.Estado.text.toString()
            val valorMercado = binding.Valor.text.toString().toDoubleOrNull() ?: 0.0
            val precioVenta = binding.Precio.text.toString().toDoubleOrNull() ?: 0.0

            if (nombre.isNotEmpty()) {
                // Creamos la carta inicial (Sin IDs todavía)
                val nuevaCarta = Carta(
                    nombre = nombre,
                    franquicia = franquicia,
                    coleccion = coleccion,
                    edicion = rareza,     // Corresponde a 'edicion_rareza'
                    estado = estado,      // Corresponde a 'estado_conservacion'
                    valorMercado = valorMercado,
                    precioVenta = precioVenta,
                    // idLocal = 0 (Room lo generará)
                    // idMongo = null (Mongo lo generará)
                )

                // Llamamos a la función híbrida
                guardadoRoomMongo(nuevaCarta)
                limpiarCampos()
            } else {
                Toast.makeText(this, "Rellena el nombre", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            //asdf
        }
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
    }

    private fun guardadoRoomMongo(cartaInicial: Carta) {
        lifecycleScope.launch(Dispatchers.IO) {

            // 1. GUARDAR EN ROOM PRIMERO (Para obtener el idLocal)
            val idGenerado = db.cartaDao().insertarCarta(cartaInicial)

            // Creamos una copia de la carta con el ID real que nos ha dado Room
            // (ej: idLocal pasa de 0 a 1)
            var cartaCompleta = cartaInicial.copy(idLocal = idGenerado.toInt())

            var mensaje = "Guardado en Local (ID: $idGenerado)"

            // 2. ENVIAR A MONGO (Ahora lleva el idLocal correcto)
            try {
                val response = RetrofitClient.instance.insertarCarta(cartaCompleta)

                if (response.isSuccessful && response.body() != null) {
                    // Mongo nos devuelve el _id de la nube
                    val cartaDeMongo = response.body()!!

                    // 3. ACTUALIZAR ROOM CON EL ID DE MONGO
                    // Ya tenemos idLocal (1) y idMongo (5f4a...), lo actualizamos en el móvil
                    cartaCompleta = cartaCompleta.copy(idMongo = cartaDeMongo.idMongo)
                    db.cartaDao().actualizarCarta(cartaCompleta) // Necesitas tener @Update en tu DAO

                    mensaje = "Sincronizado: Room (ID $idGenerado) + Mongo"
                }
                // En MainActivity.kt
            } catch (e: Exception) {
                // CAMBIA ESTO: Haz que el Toast muestre el error técnico
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "ERROR RED: ${e.message}", Toast.LENGTH_LONG).show()
                    android.util.Log.e("ERROR_MONGO", "Fallo detallado: ", e) // Míralo en Logcat también
                }
            }

            // 4. ACTUALIZAR UI
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, mensaje, Toast.LENGTH_LONG).show()
                binding.Nombre.text.clear()
                binding.Valor.text.clear()
                cargarDeRoom()
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
