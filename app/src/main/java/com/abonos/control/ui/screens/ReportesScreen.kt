package com.abonos.control.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abonos.control.data.ClienteEntity
import com.abonos.control.ui.theme.AzulPrimario
import com.abonos.control.ui.theme.RojoDeuda
import com.abonos.control.ui.theme.VerdeAbono
import com.abonos.control.util.ExportadorExcel
import com.abonos.control.util.ExportadorPdf
import com.abonos.control.util.UtilCompartir
import com.abonos.control.util.aFormatoCop
import com.abonos.control.viewmodel.ClienteDetalleViewModel
import com.abonos.control.viewmodel.ClientesViewModel
import com.abonos.control.viewmodel.ReportesViewModel
import com.abonos.control.viewmodel.fabricaViewModel
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportesScreen(onVolver: () -> Unit) {
    val vmReportes: ReportesViewModel = viewModel(factory = fabricaViewModel { ReportesViewModel(it) })
    val vmClientes: ClientesViewModel = viewModel(factory = fabricaViewModel { ClientesViewModel(it) })

    val deudaTotal by vmReportes.deudaTotal.collectAsState()
    val historial by vmReportes.historial.collectAsState()
    val clientes by vmClientes.clientes.collectAsState()

    var clienteSeleccionado by remember { mutableStateOf<ClienteEntity?>(null) }
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes") },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, contentDescription = null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrimario,
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    navigationIconContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- Reporte general ---
            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Reporte general", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(6.dp))
                        Text("Deuda general total:", style = MaterialTheme.typography.bodyMedium)
                        Text(deudaTotal.aFormatoCop(), style = MaterialTheme.typography.headlineMedium, color = RojoDeuda, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item { Text("Historial por fechas", style = MaterialTheme.typography.titleMedium) }
            items(historial) { fila ->
                Card(shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(fila.descripcion, fontWeight = FontWeight.Medium)
                            Text(formatoFecha.format(Date(fila.fechaMillis)), style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            fila.montoCop.aFormatoCop(),
                            color = if (fila.esAbono) VerdeAbono else RojoDeuda,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- Reporte por cliente ---
            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Reporte por cliente", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(10.dp))
                        ExposedSelectorClientes(clientes, clienteSeleccionado) { clienteSeleccionado = it }
                        Spacer(Modifier.height(14.dp))
                        if (clienteSeleccionado != null) {
                            BotonesExportar(cliente = clienteSeleccionado!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExposedSelectorClientes(
    clientes: List<ClienteEntity>,
    seleccionado: ClienteEntity?,
    onSeleccionar: (ClienteEntity) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = seleccionado?.nombre ?: "Selecciona un cliente",
            onValueChange = {},
            readOnly = true,
            label = { Text("Cliente") },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            clientes.forEach { cliente ->
                DropdownMenuItem(text = { Text(cliente.nombre) }, onClick = { onSeleccionar(cliente); expandido = false })
            }
        }
    }
}

/**
 * Genera y ofrece compartir el reporte del cliente seleccionado en PDF y Excel.
 * Nota: en una app de producción, la carga de pedidos/abonos + items debe hacerse con
 * corrutinas desde un ViewModel; aquí se usa runBlocking por simplicidad del ejemplo,
 * ya que la base de datos es local y la consulta es rápida.
 */
@Composable
private fun BotonesExportar(cliente: ClienteEntity) {
    val context = LocalContext.current
    val vmDetalle: ClienteDetalleViewModel = viewModel(
        key = "reporte_${cliente.id}",
        factory = fabricaViewModel { ClienteDetalleViewModel(it, cliente.id) }
    )
    val pedidos by vmDetalle.pedidos.collectAsState()
    val abonos by vmDetalle.abonos.collectAsState()

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = {
            val pedidosConItems = pedidos.map { pedido ->
                val items = runBlocking { vmDetalle.itemsComoParesSync(pedido.id) }
                pedido to items
            }
            val archivo = ExportadorPdf.generarReporteCliente(context, cliente, pedidosConItems, abonos)
            val uri = ExportadorPdf.uriParaCompartir(context, archivo)
            UtilCompartir.compartirArchivo(context, uri, "application/pdf")
        }) { Text("Exportar PDF") }

        OutlinedButton(onClick = {
            val pedidosConItems = pedidos.map { pedido ->
                val items = runBlocking { vmDetalle.itemsComoParesSync(pedido.id) }
                pedido to items
            }
            val archivo = ExportadorExcel.generarReporteCliente(context, cliente, pedidosConItems, abonos)
            val uri = ExportadorPdf.uriParaCompartir(context, archivo)
            UtilCompartir.compartirArchivo(
                context, uri,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
        }) { Text("Exportar Excel") }

        OutlinedButton(onClick = {
            val pedidosConItems = pedidos.map { pedido ->
                val items = runBlocking { vmDetalle.itemsComoParesSync(pedido.id) }
                pedido to items
            }
            val archivo = ExportadorPdf.generarReporteCliente(context, cliente, pedidosConItems, abonos)
            val uri = ExportadorPdf.uriParaCompartir(context, archivo)
            UtilCompartir.compartirPorWhatsapp(context, uri, "application/pdf")
        }) { Text("Enviar por WhatsApp") }
    }
}
