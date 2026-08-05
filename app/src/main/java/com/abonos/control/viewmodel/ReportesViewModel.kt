package com.abonos.control.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abonos.control.data.AbonoEntity
import com.abonos.control.data.PedidoEntity
import com.abonos.control.data.Repositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class FilaReporteGeneral(
    val fechaMillis: Long,
    val descripcion: String,
    val montoCop: Long,
    val esAbono: Boolean
)

class ReportesViewModel(private val repo: Repositorio) : ViewModel() {

    val deudaTotal: StateFlow<Long> = repo.observarDeudaTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val historial: StateFlow<List<FilaReporteGeneral>> = combine(
        repo.observarTodosLosPedidos(),
        repo.observarTodosLosAbonos()
    ) { pedidos: List<PedidoEntity>, abonos: List<AbonoEntity> ->
        val filasPedidos = pedidos.map {
            FilaReporteGeneral(it.fechaMillis, "Pedido #${it.id}", it.totalCop, esAbono = false)
        }
        val filasAbonos = abonos.map {
            FilaReporteGeneral(it.fechaMillis, "Abono cliente #${it.clienteId}", it.montoCop, esAbono = true)
        }
        (filasPedidos + filasAbonos).sortedByDescending { it.fechaMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
