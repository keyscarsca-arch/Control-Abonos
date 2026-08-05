package com.abonos.control.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abonos.control.data.AbonoEntity
import com.abonos.control.data.ClienteEntity
import com.abonos.control.data.PedidoEntity
import com.abonos.control.data.Repositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClienteDetalleViewModel(
    private val repo: Repositorio,
    private val clienteId: Long
) : ViewModel() {

    val cliente: StateFlow<ClienteEntity?> = repo.observarCliente(clienteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val pedidos: StateFlow<List<PedidoEntity>> = repo.observarPedidosDeCliente(clienteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val abonos: StateFlow<List<AbonoEntity>> = repo.observarAbonosDeCliente(clienteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** items: lista de (descripcion, costoCop) ya validados desde la pantalla */
    fun guardarPedido(items: List<Pair<String, Long>>, alTerminar: () -> Unit) {
        val validos = items.filter { it.first.isNotBlank() && it.second > 0 }
        if (validos.isEmpty()) return
        viewModelScope.launch {
            repo.guardarPedido(clienteId, validos)
            alTerminar()
        }
    }

    fun registrarAbono(monto: Long, alTerminar: () -> Unit) {
        if (monto <= 0) return
        viewModelScope.launch {
            repo.registrarAbono(clienteId, monto)
            alTerminar()
        }
    }

    /** Helper usado por la pantalla de reportes para armar el PDF/Excel de este cliente. */
    suspend fun itemsComoParesSync(pedidoId: Long): List<Pair<String, Long>> =
        repo.itemsDePedido(pedidoId).map { it.descripcion to it.costoCop }
}
