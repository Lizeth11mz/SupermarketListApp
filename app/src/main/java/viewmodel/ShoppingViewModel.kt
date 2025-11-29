package com.example.supermarketlistapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supermarketlistapp.data.Item
import com.example.supermarketlistapp.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el estado de la lista de compras y la comunicación con el API.
 * Usa corrutinas (viewModelScope) para realizar llamadas de red asíncronas.
 */
class ShoppingViewModel : ViewModel() {

    // Lista de ítems a observar en la UI. mutableStateOf hace que Compose reaccione a los cambios.
    var items by mutableStateOf(emptyList<Item>())
        private set

    // Estado para indicar si se está cargando información del servidor.
    var isLoading by mutableStateOf(false)
        private set

    // Total de la lista, calculado dinámicamente.
    val totalCost: Double
        get() = items.sumOf { it.precioTotal } // Usamos precioTotal del modelo Item

    // El bloque init se ejecuta la primera vez que se crea el ViewModel
    init {
        fetchItems() // Cargar la lista al iniciar la aplicación
    }

    /**
     * Obtiene la lista completa de artículos desde el API (GET /items).
     */
    fun fetchItems() {
        viewModelScope.launch {
            isLoading = true
            try {
                items = RetrofitClient.api.getItems()
            } catch (e: Exception) {
                // En un app real, esto se mostraría al usuario.
                println("Error al obtener ítems: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Agrega un nuevo artículo al API y a la lista local (POST /items).
     */
    fun addItem(nombre: String, cantidad: Int, precio: Double) {
        viewModelScope.launch {
            try {
                // Creamos un Item sin ID, el servidor se lo asignará.
                val newItem = Item(nombre = nombre, cantidad = cantidad, precio = precio)
                val addedItem = RetrofitClient.api.addItem(newItem) // Llama al endpoint POST /items

                // Agrega el ítem devuelto por el servidor (con su ID) al inicio de la lista
                items = listOf(addedItem) + items
            } catch (e: Exception) {
                println("Error al añadir ítem: ${e.message}")
            }
        }
    }

    /**
     * Cambia el estado 'comprado' de un artículo (PUT /items/:id/check).
     */
    fun toggleCheck(item: Item) {
        viewModelScope.launch {
            try {
                // El estado comprado de tu modelo Item es 'comprado'
                val newCheckStatus = !item.comprado

                // Llama al endpoint PUT /items/:id/check enviando {"comprado": true/false}
                RetrofitClient.api.updateCheck(item.id, mapOf("comprado" to newCheckStatus))

                // Actualiza el estado localmente para reflejar el cambio inmediato en la UI
                items = items.map {
                    if (it.id == item.id) it.copy(comprado = newCheckStatus) else it
                }
            } catch (e: Exception) {
                println("Error al marcar ítem: ${e.message}")
            }
        }
    }

    /**
     * Actualiza la cantidad y/o el precio de un artículo (PUT /items/:id).
     */
    fun updateQuantityAndPrice(item: Item, newQuantity: Int, newPrice: Double) {
        viewModelScope.launch {
            try {
                // Construye el cuerpo de la petición solo con los campos que cambiaron
                val updateBody = mutableMapOf<String, Any>()
                if (item.cantidad != newQuantity) updateBody["cantidad"] = newQuantity
                if (item.precio != newPrice) updateBody["precio"] = newPrice

                if (updateBody.isNotEmpty()) {
                    RetrofitClient.api.updateDetails(item.id, updateBody)
                }

                // Actualiza el estado localmente
                items = items.map {
                    if (it.id == item.id) it.copy(cantidad = newQuantity, precio = newPrice) else it
                }
            } catch (e: Exception) {
                println("Error al actualizar detalles: ${e.message}")
            }
        }
    }

    /**
     * Elimina un artículo del API y de la lista local (DELETE /items/:id).
     */
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            try {
                RetrofitClient.api.deleteItem(item.id)

                // Elimina el ítem localmente filtrando el ID
                items = items.filter { it.id != item.id }
            } catch (e: Exception) {
                println("Error al eliminar ítem: ${e.message}")
            }
        }
    }
}