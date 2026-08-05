package com.abonos.control.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abonos.control.data.UsuarioEntity
import com.abonos.control.ui.theme.AzulPrimario
import com.abonos.control.ui.theme.DoradoPrimario
import com.abonos.control.ui.theme.RojoDeuda
import com.abonos.control.viewmodel.UsuariosViewModel
import com.abonos.control.viewmodel.fabricaViewModel

// CRUD de usuarios. Regla estricta: nunca puede quedar la base sin al menos un administrador
// (la validación real ocurre en Repositorio.eliminarUsuario / guardarUsuario).
@Composable
fun UsuariosScreen(onVolver: () -> Unit) {
    val vm: UsuariosViewModel = viewModel(factory = fabricaViewModel { UsuariosViewModel(it) })
    val usuarios by vm.usuarios.collectAsState()
    val error by vm.mensajeError.collectAsState()

    var mostrarFormulario by remember { mutableStateOf(false) }
    var usuarioEnEdicion by remember { mutableStateOf<UsuarioEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de usuarios") },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, contentDescription = null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrimario,
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    navigationIconContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { usuarioEnEdicion = null; mostrarFormulario = true },
                containerColor = DoradoPrimario,
                contentColor = AzulPrimario,
                text = { Text("Nuevo usuario") },
                icon = {}
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(usuarios, key = { it.id }) { usuario ->
                Card(shape = RoundedCornerShape(12.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(usuario.nombreUsuario, style = MaterialTheme.typography.titleMedium)
                            Text(if (usuario.esAdministrador) "Administrador" else "Usuario", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row {
                            IconButton(onClick = { usuarioEnEdicion = usuario; mostrarFormulario = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { vm.eliminar(usuario) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = RojoDeuda)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioUsuario(
            usuario = usuarioEnEdicion,
            onCerrar = { mostrarFormulario = false },
            onGuardar = { u -> vm.guardar(u) { mostrarFormulario = false } }
        )
    }

    if (error != null) {
        AlertDialog(
            onDismissRequest = { vm.limpiarError() },
            confirmButton = { TextButton(onClick = { vm.limpiarError() }) { Text("Entendido") } },
            title = { Text("No se pudo completar la acción") },
            text = { Text(error ?: "") }
        )
    }
}

@Composable
private fun FormularioUsuario(
    usuario: UsuarioEntity?,
    onCerrar: () -> Unit,
    onGuardar: (UsuarioEntity) -> Unit
) {
    var nombreUsuario by remember { mutableStateOf(usuario?.nombreUsuario ?: "") }
    var password by remember { mutableStateOf(usuario?.password ?: "") }
    var esAdmin by remember { mutableStateOf(usuario?.esAdministrador ?: true) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (usuario == null) "Nuevo usuario" else "Editar usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = nombreUsuario, onValueChange = { nombreUsuario = it }, label = { Text("Nombre de usuario") }, singleLine = true)
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, label = { Text("Contraseña") },
                    singleLine = true, visualTransformation = PasswordVisualTransformation()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = esAdmin, onCheckedChange = { esAdmin = it })
                    Text("Es administrador")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (nombreUsuario.isNotBlank() && password.isNotBlank()) {
                    onGuardar(
                        UsuarioEntity(
                            id = usuario?.id ?: 0,
                            nombreUsuario = nombreUsuario.trim(),
                            password = password,
                            esAdministrador = esAdmin
                        )
                    )
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
