package com.abonos.control.util

import android.content.Context
import com.abonos.control.data.AbonoEntity
import com.abonos.control.data.ClienteEntity
import com.abonos.control.data.PedidoEntity
import org.dhatim.fastexcel.Workbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Genera el reporte por cliente en formato .xlsx usando FastExcel
 * (librería liviana, sin las dependencias de escritorio de Apache POI).
 */
object ExportadorExcel {

    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO"))

    fun generarReporteCliente(
        context: Context,
        cliente: ClienteEntity,
        pedidos: List<Pair<PedidoEntity, List<Pair<String, Long>>>>,
        abonos: List<AbonoEntity>
    ): File {
        val carpeta = File(context.externalCacheDir, "reportes").apply { mkdirs() }
        val archivo = File(carpeta, "reporte_${cliente.nombre.replace(" ", "_")}.xlsx")

        FileOutputStream(archivo).use { salida ->
            val workbook = Workbook(salida, "ControlAbonos", "1.0")

            val hojaPedidos = workbook.newWorksheet("Pedidos")
            hojaPedidos.value(0, 0, "Fecha")
            hojaPedidos.value(0, 1, "Descripción")
            hojaPedidos.value(0, 2, "Costo (COP)")
            var fila = 1
            pedidos.forEach { (pedido, items) ->
                items.forEach { (desc, costo) ->
                    hojaPedidos.value(fila, 0, formatoFecha.format(Date(pedido.fechaMillis)))
                    hojaPedidos.value(fila, 1, desc)
                    hojaPedidos.value(fila, 2, costo.toDouble())
                    fila++
                }
            }

            val hojaAbonos = workbook.newWorksheet("Abonos")
            hojaAbonos.value(0, 0, "Fecha")
            hojaAbonos.value(0, 1, "Monto (COP)")
            abonos.forEachIndexed { i, abono ->
                hojaAbonos.value(i + 1, 0, formatoFecha.format(Date(abono.fechaMillis)))
                hojaAbonos.value(i + 1, 1, abono.montoCop.toDouble())
            }

            val hojaResumen = workbook.newWorksheet("Resumen")
            hojaResumen.value(0, 0, "Cliente")
            hojaResumen.value(0, 1, cliente.nombre)
            hojaResumen.value(1, 0, "Saldo pendiente (COP)")
            hojaResumen.value(1, 1, cliente.saldoPendiente.toDouble())

            workbook.finish()
        }
        return archivo
    }
}
