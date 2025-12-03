package com.example.supermarketlistapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton (Object) para configurar y proporcionar la instancia de Retrofit.
 * Se encarga de:
 * 1. Definir la URL base de la API de Node.js.
 * 2. Usar Gson para la conversión de JSON a objetos Kotlin (Item) y viceversa.
 */
object RetrofitClient {

    // NOTA IMPORTANTE:
    // '10.0.2.2' es la IP especial que usa el emulador de Android
    // Si usas un dispositivo Android físico, debes reemplazar esto con
    // la IP local de tu PC (ej: 172.28.84.39) o un dominio.
    private const val BASE_URL = "http://10.195.35.131:3000/"

    // Inicialización perezosa (lazy) de la instancia de Retrofit.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Agregamos el conversor de Gson que se encargará de mapear JSON.
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Inicialización perezosa de la interfaz ShoppingApi.
    // Esta es la instancia que la aplicación usará para hacer llamadas HTTP.
    val api: ShoppingApi by lazy {
        retrofit.create(ShoppingApi::class.java)
    }
}
