package com.example.supermarketlistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background // <-- ADDED THIS IMPORT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.supermarketlistapp.data.Item
import com.example.supermarketlistapp.ui.theme.SupermarketListAppTheme
import com.example.supermarketlistapp.viewmodel.ShoppingViewModel // Asumiendo que esta es la ruta correcta

/**
 * Actividad principal de la aplicación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SupermarketListAppTheme {
                ShoppingListScreen()
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 1. Pantalla Principal: ShoppingListScreen
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingViewModel = viewModel()) {
    // Observamos los estados del ViewModel
    val items = viewModel.items
    val isLoading = viewModel.isLoading
    val totalCost = viewModel.totalCost
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 Lista de Supermercado", style = MaterialTheme.typography.headlineSmall) },
                actions = {
                    // Botón para recargar la lista o simular la recarga de datos
                    IconButton(onClick = { viewModel.fetchItems() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar Lista")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Ítem")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Mostrar estado de carga si es necesario
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Lista de Artículos
            if (items.isEmpty() && !isLoading) {
                // Mensaje si la lista está vacía
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("¡Tu lista está vacía!\nPresiona '+' para añadir un artículo.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        ItemCard(item = item, viewModel = viewModel)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            }

            // Barra de Total al final
            TotalBar(totalCost = totalCost, modifier = Modifier.fillMaxWidth())
        }
    }

    // Diálogo para añadir nuevo ítem
    if (showAddDialog) {
        AddItemDialog(
            onDismiss = { showAddDialog = false },
            onSave = { nombre, cantidad, precio ->
                viewModel.addItem(nombre, cantidad, precio)
                showAddDialog = false
            }
        )
    }
}

// -----------------------------------------------------------------------------
// 2. Componente de Tarjeta de Ítem (ItemCard)
// -----------------------------------------------------------------------------

@Composable
fun ItemCard(item: Item, viewModel: ShoppingViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            // Cambia el color si está comprado para dar feedback visual
            containerColor = if (item.comprado)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true } // Hacemos toda la tarjeta cliclable para editar
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox para palomear (marcar como comprado)
            Checkbox(
                checked = item.comprado,
                onCheckedChange = { viewModel.toggleCheck(item) }
            )

            Spacer(Modifier.width(16.dp))

            // Nombre y detalles
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (item.comprado) TextDecoration.LineThrough else null
                )
                Text(
                    text = "${item.cantidad} x $${"%.2f".format(item.precio)} c/u",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Precio total del ítem
            Text(
                text = "$${"%.2f".format(item.precioTotal)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Botón de eliminar
            IconButton(onClick = { viewModel.deleteItem(item) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }

    // Diálogo para editar cantidad/precio
    if (showDialog) {
        EditItemDialog(
            item = item,
            onDismiss = { showDialog = false },
            onSave = { newQuantity, newPrice ->
                viewModel.updateQuantityAndPrice(item, newQuantity, newPrice)
                showDialog = false
            }
        )
    }
}

// -----------------------------------------------------------------------------
// 3. Componente de Diálogo de Edición (EditItemDialog)
// -----------------------------------------------------------------------------

@Composable
fun EditItemDialog(item: Item, onDismiss: () -> Unit, onSave: (Int, Double) -> Unit) {
    var quantityInput by rememberSaveable { mutableStateOf(item.cantidad.toString()) }
    var priceInput by rememberSaveable { mutableStateOf(item.precio.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar: ${item.nombre}") },
        text = {
            Column {
                // Campo para Cantidad
                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = { quantityInput = it.filter { c -> c.isDigit() } }, // Solo permite dígitos
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo para Precio Unitario
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it.filter { c -> c.isDigit() || c == '.' } }, // Permite dígitos y punto decimal
                    label = { Text("Precio Unitario") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val newQuantity = quantityInput.toIntOrNull() ?: item.cantidad
                val newPrice = priceInput.toDoubleOrNull() ?: item.precio
                // Validamos que la cantidad no sea negativa
                if (newQuantity > 0) {
                    onSave(newQuantity, newPrice)
                } else {
                    onSave(item.cantidad, newPrice) // Si es 0 o inválida, mantenemos la anterior
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// -----------------------------------------------------------------------------
// 4. Componente de Diálogo de Agregar (AddItemDialog)
// -----------------------------------------------------------------------------

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onSave: (String, Int, Double) -> Unit) {
    var nameInput by rememberSaveable { mutableStateOf("") }
    var quantityInput by rememberSaveable { mutableStateOf("1") }
    var priceInput by rememberSaveable { mutableStateOf("0.00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Nuevo Artículo") },
        text = {
            Column {
                // Campo para Nombre
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nombre del Artículo") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo para Cantidad
                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = { quantityInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo para Precio Unitario
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Precio Unitario") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val nombre = nameInput.trim()
                // Aseguramos que la cantidad sea al menos 1
                val cantidad = quantityInput.toIntOrNull()?.coerceAtLeast(1) ?: 1
                val precio = priceInput.toDoubleOrNull() ?: 0.00
                if (nombre.isNotEmpty()) {
                    onSave(nombre, cantidad, precio)
                }
            }, enabled = nameInput.trim().isNotEmpty()) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// -----------------------------------------------------------------------------
// 5. Componente de Barra de Total (TotalBar)
// -----------------------------------------------------------------------------

@Composable
fun TotalBar(totalCost: Double, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Costo Total Estimado:",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "$${"%.2f".format(totalCost)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Preview
// -----------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun ShoppingListPreview() {
    SupermarketListAppTheme {
        ShoppingListScreen()
    }
}