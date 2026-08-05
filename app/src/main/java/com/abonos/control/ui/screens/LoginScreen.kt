package com.abonos.control.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abonos.control.ControlAbonosApp
import com.abonos.control.data.UsuarioEntity
import com.abonos.control.ui.theme.AzulPrimario
import com.abonos.control.ui.theme.DoradoPrimario
import com.abonos.control.viewmodel.EstadoLogin
import com.abonos.control.viewmodel.LoginViewModel
import com.abonos.control.viewmodel.fabricaViewModel

@Composable
fun LoginScreen(onLoginExitoso: (UsuarioEntity) -> Unit) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as ControlAbonosApp
    val vm: LoginViewModel = viewModel(factory = fabricaViewModel { LoginViewModel(it) })
    val estado by vm.estado.collectAsState()

    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(estado) {
        val actual = estado
        if (actual is EstadoLogin.Exito) onLoginExitoso(actual.usuario)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulPrimario),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.widthIn(max = 420.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Control de Abonos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AzulPrimario,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text("Inicia sesión para continuar", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(28.dp))

                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    label = { Text("Usuario") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (estado is EstadoLogin.Error) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        (estado as EstadoLogin.Error).mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { vm.ingresar(usuario, password) },
                    enabled = estado !is EstadoLogin.Cargando,
                    colors = ButtonDefaults.buttonColors(containerColor = DoradoPrimario, contentColor = AzulPrimario),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (estado is EstadoLogin.Cargando) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AzulPrimario, strokeWidth = 2.dp)
                    } else {
                        Text("Ingresar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
