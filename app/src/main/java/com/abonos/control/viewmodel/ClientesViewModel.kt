package com.abonos.control.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abonos.control.data.ClienteEntity
import com.abonos.control.data.Repositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClientesViewModel(private val repo: Repositorio) : ViewModel() {

    val clientes: StateFlow<List<ClienteEntity>> = repo.observarClientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun crearCliente(
        nombre: String,
        seudonimo: String,
        telefono: String?,
        direccion: String?,
        deudaInicial: Long,
        alTerminar: () -> Unit
    ) {
        viewModelScope.launch {
            repo.crearCliente(
                ClienteEntity(
                    nombre = nombre,
                    seudonimo = seudonimo.ifBlank { nombre },
                    telefono = telefono?.ifBlank { null },
                    direccion = direccion?.ifBlank { null },
                    saldoPendiente = deudaInicial
                )
            )
            alTerminar()
        }
    }
}
