package com.abonos.control.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abonos.control.ui.theme.AzulPrimario
import com.abonos.control.ui.theme.DoradoPrimario
import com.abonos.control.ui.theme.RojoDeuda
import com.abonos.control.ui.theme.VerdeAbono
import com.abonos.control.util.aFormatoCop
import com.abonos.control.util.aLongCop
import com.abonos.control.viewmodel.ClienteDetalleViewModel
import com.abonos.control.viewmodel.fabricaViewModel
import java.util.UUID

private data class FilaItemPedido(val id: String = UUID.randomUUID().toString(), var descripcion: String = "", var costo: String = "")

@Composable
fun ClienteDetalleScreen(clienteId: Long, onVolver: () -> Unit) {
    val vm: ClienteDetalleViewModel = viewModel(factory = fabricaViewModel { ClienteDetalleViewModel(it, clienteId) })
    val cliente by vm.cliente.collectAsState()
    val pedidos by vm.pedidos.collectAsState()
    val abonos by vm.abonos.collectAsState()

    var filasItems by remember { mutableStateOf(listOf(FilaItemPedido())) }
    var montoAbono by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cliente?.nombre ?: "Cliente") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrimario,
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    navigationIconContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    ) { padding ->
        val c = cliente ?: return@Scaffold
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- Encabezado con saldo pendiente ---
            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Saldo pendiente actual", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            c.saldoPendiente.aFormatoCop(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (c.saldoPendiente > 0) RojoDeuda else VerdeAbono,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- Agregar a la factura ---
            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Agregar a la Factura", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(12.dp))

                        filasItems.forEachIndexed { index, fila ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                OutlinedTextField(
                                    value = fila.descripcion,
                                    onValueChange = { nuevo ->
                                        filasItems = filasItems.toMutableList().also { it[index] = fila.copy(descripcion = nuevo) }
                                    },
                                    label = { Text("Descripción") },
                                    modifier = Modifier.weight(2f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = fila.costo,
                                    onValueChange = { nuevo ->
                                        filasItems = filasItems.toMutableList().also { it[index] = fila.copy(costo = nuevo) }
                                    },
                                    label = { Text("Costo (COP)") },
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                IconButton(onClick = {
                                    if (filasItems.size > 1) filasItems = filasItems.toMutableList().also { it.removeAt(index) }
                                }) {
                                    Icon(Icons.Default.Remove, contentDescription = "Quitar fila", tint = RojoDeuda)
                                }
                            }
                        }

                        TextButton(onClick = { filasItems = filasItems + FilaItemPedido() }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar producto/servicio")
                        }

                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val items = filasItems.map { it.descripcion to it.costo.aLongCop() }
                                vm.guardarPedido(items) {
                                    filasItems = listOf(FilaItemPedido())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DoradoPrimario, contentColor = AzulPrimario),
                            modifier = Modifier.align(Alignment.End)
                        ) { Text("Guardar Pedido", fontWeight = FontWeight.Bold) }
                    }
                }
            }

            // --- Registrar abono ---
            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Registrar Abono", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = montoAbono,
                                onValueChange = { montoAbono = it },
                                label = { Text("Monto en COP") },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    vm.registrarAbono(montoAbono.aLongCop()) { montoAbono = "" }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeAbono)
                            ) { Text("Guardar Abono") }
                        }
                    }
                }
            }

            // --- Historial de pedidos ---
            item { Text("Historial de pedidos", style = MaterialTheme.typography.titleMedium) }
            items(pedidos) { pedido ->
                Card(shape = RoundedCornerShape(12.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pedido #${pedido.id}")
                        Text(pedido.totalCop.aFormatoCop(), fontWeight = FontWeight.Bold, color = RojoDeuda)
                    }
                }
            }

            // --- Historial de abonos ---
            item { Text("Historial de abonos", style = MaterialTheme.typography.titleMedium) }
            items(abonos) { abono ->
                Card(shape = RoundedCornerShape(12.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Abono #${abono.id}")
                        Text(abono.montoCop.aFormatoCop(), fontWeight = FontWeight.Bold, color = VerdeAbono)
                    }
                }
            }
        }
    }
}
