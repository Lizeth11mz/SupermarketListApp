# ✔️ CheckList Súper: Gestión de Compras Móvil

## 💡 1. Introducción y Objetivo del Proyecto

**CheckList Súper** es una aplicación móvil nativa para Android, desarrollada en Kotlin y Jetpack Compose. Fue creada como parte del curso de Desarrollo de Aplicaciones Móviles.

El objetivo principal es ofrecer una herramienta **sencilla y eficiente** para que los usuarios organicen y gestionen sus listas de compras de supermercado. La aplicación permite añadir artículos, indicar la cantidad, registrar el precio estimado, y **marcar los productos como comprados**, manteniendo un control visual y económico de la lista.

### Requisitos del Curso Cumplidos

| Requisito | Tecnología |
| :--- | :--- |
| **Desarrollo Nativo** | **Kotlin** y **Jetpack Compose**. |
| **Comunicación** | Uso de servicios **REST** para interactuar con una API. |
| **Persistencia** | Manejo de datos externos con **MariaDB / MySQL**. |

---

## 🏗️ 2. Arquitectura de Tres Capas (MVVM)

La aplicación utiliza una arquitectura de **tres capas** (Cliente Móvil, Servidor/API y Base de Datos) para garantizar la separación de responsabilidades y la comunicación eficiente a través de servicios REST. 

### 2.1 Componentes y Tecnologías

| Componente | Tecnología | Patrón/Modelo | Propósito |
| :--- | :--- | :--- | :--- |
| **Cliente Móvil** | Kotlin + **Jetpack Compose** | **MVVM** | Interfaz de usuario, gestión de estado y presentación de datos. |
| **Comunicación** | **Retrofit** + **Coroutines** | Asincronía | Manejo seguro de peticiones HTTP en segundo plano. |
| **Servidor/API** | **Node.js + Express.js** | **API REST** | Lógica de negocio y gestión de *endpoints*. |
| **Base de Datos** | **MariaDB / MySQL** | SQL | **Almacenamiento persistente de datos (implementado en MariaDB).** |

### 2.2 Implementación del Cliente (Android/Compose)

* **Patrón MVVM:** Las pantallas de **Compose** actúan como la **View**, las cuales consumen los datos reactivos expuestos por un **ViewModel**.
* **Conectividad:** Todas las llamadas a la API se realizan con **Retrofit** para serializar objetos Kotlin a JSON y viceversa.
* **Rendimiento:** Las operaciones de red se ejecutan con **Coroutines**, asegurando que el hilo principal de la interfaz de usuario no se bloquee.

### 2.3 Implementación de Servicios REST (CRUD)

La comunicación se gestiona mediante los siguientes *endpoints*:

| Operación | Método HTTP | Ruta de API | Función Cliente (Kotlin) |
| :--- | :--- | :--- | :--- |
| **Leer** | `GET` | `/items` | `getItems()` |
| **Crear** | `POST` | `/items` | `addItem(item)` |
| **Actualizar** | `PUT`/`PATCH` | `/items/:id` | `updateItem(id, item)` |
| **Eliminar** | `DELETE` | `/items/:id` | `deleteItem(id)` |

---

## 💻 3. Notas de Desarrollo y Configuración

### 3.1 Manejo de Conectividad Local

Para las pruebas con el servidor local, fueron necesarias configuraciones específicas:

| Entorno de Prueba | Dirección de Acceso |
| :--- | :--- |
| **Emulador de Android Studio** | `http://10.0.2.2:3000` |
| **Dispositivo Físico (Wi-Fi)** | IP local de la PC (Ej: `http://192.168.1.82:3000`) |

> **NOTA:** La aplicación se conecta actualmente a un servidor local. Se habilitó `android:usesCleartextTraffic="true"` en el `AndroidManifest.xml` para permitir peticiones HTTP para el desarrollo. **Esta bandera debe ser removida en producción.**

---

## ⚙️ 4. Configuración (Para Desarrolladores)

### 1. Clonar el Repositorio

```bash
git clone [https://github.com/Lizeth11mz/SupermarketListApp.git](https://github.com/Lizeth11mz/SupermarketListApp.git)
cd SupermarketListApp

```bash
git clone [https://github.com/Lizeth11mz/SupermarketListApp.git](https://github.com/Lizeth11mz/SupermarketListApp.git)
cd SupermarketListApp
