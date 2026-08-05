package com.abonos.control.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abonos.control.data.ClienteEntity
import com.abonos.control.ui.components.TarjetaCliente
import com.abonos.control.ui.theme.AzulPrimario
import com.abonos.control.ui.theme.DoradoPrimario
import com.abonos.control.viewmodel.ClientesViewModel
import com.abonos.control.viewmodel.fabricaViewModel

// Grid optimizado para tablet: número de columnas adaptable al ancho de pantalla
@Composable
fun ClientesScreen(
    onClienteClick: (ClienteEntity) -> Unit,
    onAgregarCliente: () -> Unit,
    onVerReportes: () -> Unit,
    onGestionarUsuarios: () -> Unit
) {
    val vm: ClientesViewModel = viewModel(factory = fabricaViewModel { ClientesViewModel(it) })
    val clientes by vm.clientes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes") },
                actions = {
                    IconButton(onClick = onVerReportes) {
                        Icon(Icons.Default.Assessment, contentDescription = "Reportes")
                    }
                    TextButton(onClick = onGestionarUsuarios) { Text("Usuarios") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrimario,
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    actionIconContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAgregarCliente,
                containerColor = DoradoPrimario,
                contentColor = AzulPrimario,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Agregar cliente") }
            )
        }
    ) { padding ->
        if (clientes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Aún no hay clientes registrados. Usa el botón + para agregar el primero.")
            }
        } else {
            LazyVerticalGrid(
                // GridCells.Adaptive aprovecha el ancho de la tablet mostrando más columnas
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(clientes, key = { it.id }) { cliente ->
                    TarjetaCliente(cliente = cliente, onClick = { onClienteClick(cliente) })
                }
            }
        }
    }
}
