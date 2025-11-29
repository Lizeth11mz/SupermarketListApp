package com.example.supermarketlistapp.network

import com.example.supermarketlistapp.data.Item
import retrofit2.http.*
import retrofit2.Response

/**
 * Interfaz de Retrofit para definir los endpoints REST que se comunican
 * con la API de Node.js/MariaDB.
 * Todas las funciones son 'suspend' porque se llamarán desde corrutinas.
 */
interface ShoppingApi {

    // 1. GET /items: Obtener todos los artículos de la lista
    @GET("items")
    suspend fun getItems(): List<Item>

    // 2. POST /items: Crear un nuevo artículo. Retorna el artículo creado con su ID.
    @POST("items")
    suspend fun addItem(@Body item: Item): Item

    // 3. PUT /items/{id}/check: Actualizar el estado 'comprado' (true/false)
    @PUT("items/{id}/check")
    suspend fun updateCheck(
        @Path("id") id: Int,
        // El body debe enviar: {"comprado": true/false}
        @Body body: Map<String, Boolean>
    ): Response<Unit> // Response<Unit> indica que no esperamos un cuerpo de respuesta (204 No Content/200 OK)

    // 4. PUT /items/{id}: Actualizar detalles (cantidad o precio)
    @PUT("items/{id}")
    suspend fun updateDetails(
        @Path("id") id: Int,
        // El body debe enviar: {"cantidad": X} o {"precio": Y} o ambos
        @Body body: Map<String, Any>
    ): Response<Unit>

    // 5. DELETE /items/{id}: Eliminar un artículo
    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: Int): Response<Unit>
}