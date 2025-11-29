package com.example.supermarketlistapp.data

import com.google.gson.annotations.SerializedName

/**
 * Clase de datos que representa un artículo en la lista de supermercado.
 * Las anotaciones @SerializedName mapean los nombres JSON que envía Node.js
 * a los nombres en español que usamos en Kotlin.
 */
data class Item(
    // Columna DB: articulo_id | JSON API: "id" | Variable Kotlin: id
    @SerializedName("id")
    val id: Int = 0,

    // Columna DB: nombre | JSON API: "name" | Variable Kotlin: nombre
    @SerializedName("name")
    val nombre: String,

    // Columna DB: cantidad | JSON API: "quantity" | Variable Kotlin: cantidad
    @SerializedName("quantity")
    val cantidad: Int,

    // Columna DB: precio | JSON API: "price" | Variable Kotlin: precio
    @SerializedName("price")
    val precio: Double,

    // Columna DB: comprado | JSON API: "is_checked" | Variable Kotlin: comprado
    @SerializedName("is_checked")
    val comprado: Boolean = false,

    // Columna DB: fecha_creacion | JSON API: "created_at" | Variable Kotlin: fechaCreacion
    @SerializedName("created_at")
    val fechaCreacion: String? = null
) {
    // Getter personalizado para calcular el precio total del ítem
    val precioTotal: Double
        get() = cantidad * precio
}