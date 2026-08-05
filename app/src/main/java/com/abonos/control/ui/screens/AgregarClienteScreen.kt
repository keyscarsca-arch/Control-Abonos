package com.abonos.control.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abonos.control.ui.theme.AzulPrimario
import com.abonos.control.ui.theme.DoradoPrimario
import com.abonos.control.util.aLongCop
import com.abonos.control.viewmodel.ClientesViewModel
import com.abonos.control.viewmodel.fabricaViewModel

@Composable
fun AgregarClienteScreen(onVolver: () -> Unit) {
    val vm: ClientesViewModel = viewModel(factory = fabricaViewModel { ClientesViewModel(it) })

    var nombre by remember { mutableStateOf("") }
    var seudonimo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var deudaInicial by remember { mutableStateOf("0") }
    var errorNombre by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo cliente") },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, contentDescription = null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrimario,
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    navigationIconContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.widthIn(max = 520.dp).padding(24.dp)) {
                Column(Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Datos del cliente", style = MaterialTheme.typography.titleLarge)

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it; errorNombre = false },
                        label = { Text("Nombre *") },
                        isError = errorNombre,
                        supportingText = { if (errorNombre) Text("El nombre es obligatorio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = seudonimo,
                        onValueChange = { seudonimo = it },
                        label = { Text("Seudónimo (para iniciales)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono (opcional)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = deudaInicial,
                        onValueChange = { deudaInicial = it },
                        label = { Text("Deuda inicial (COP)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (nombre.isBlank()) { errorNombre = true; return@Button }
                            vm.crearCliente(nombre, seudonimo, telefono, direccion, deudaInicial.aLongCop(), onVolver)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DoradoPrimario, contentColor = AzulPrimario),
                        modifier = Modifier.align(Alignment.End)
                    ) { Text("Guardar cliente") }
                }
            }
        }
    }
}
