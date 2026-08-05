package com.abonos.control.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abonos.control.data.ClienteEntity
import com.abonos.control.ui.theme.AzulPrimario
import com.abonos.control.ui.theme.DoradoPrimario
import com.abonos.control.ui.theme.BlancoPuro

/** Obtiene las iniciales a partir del seudónimo, ej: "Cerrajería el Mago" -> "CE" */
fun obtenerIniciales(texto: String): String {
    val palabras = texto.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        palabras.isEmpty() -> "?"
        palabras.size == 1 -> palabras[0].take(2).uppercase()
        else -> (palabras[0].take(1) + palabras[1].take(1)).uppercase()
    }
}

@Composable
fun TarjetaCliente(cliente: ClienteEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(AzulPrimario, CircleShape)
                    .background(DoradoPrimario.copy(alpha = 0.0f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    obtenerIniciales(cliente.seudonimo),
                    color = BlancoPuro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                cliente.nombre,
                style = MaterialTheme.typography.labelSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
