package com.abonos.control.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.abonos.control.data.AbonoEntity
import com.abonos.control.data.ClienteEntity
import com.abonos.control.data.PedidoEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Genera un PDF simple de reporte por cliente usando la API nativa de Android
 * (android.graphics.pdf), sin depender de internet ni librerías externas pesadas.
 */
object ExportadorPdf {

    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO"))

    fun generarReporteCliente(
        context: Context,
        cliente: ClienteEntity,
        pedidos: List<Pair<PedidoEntity, List<Pair<String, Long>>>>,
        abonos: List<AbonoEntity>
    ): File {
        val documento = PdfDocument()
        val anchoPagina = 595   // tamaño carta aprox. en puntos
        val altoPagina = 842
        var pagina = documento.startPage(PdfDocument.PageInfo.Builder(anchoPagina, altoPagina, 1).create())
        var canvas = pagina.canvas
        var y = 40f

        val paintTitulo = Paint().apply { textSize = 18f; isFakeBoldText = true }
        val paintTexto = Paint().apply { textSize = 12f }
        val paintEncabezado = Paint().apply { textSize = 13f; isFakeBoldText = true }

        fun saltoDePaginaSiNecesario() {
            if (y > altoPagina - 60) {
                documento.finishPage(pagina)
                pagina = documento.startPage(PdfDocument.PageInfo.Builder(anchoPagina, altoPagina, documento.pages.size + 1).create())
                canvas = pagina.canvas
                y = 40f
            }
        }

        canvas.drawText("Reporte de cliente - ${cliente.nombre}", 40f, y, paintTitulo); y += 25f
        canvas.drawText("Saldo pendiente actual: ${cliente.saldoPendiente.aFormatoCop()}", 40f, y, paintTexto); y += 30f

        canvas.drawText("Pedidos", 40f, y, paintEncabezado); y += 20f
        pedidos.forEach { (pedido, items) ->
            saltoDePaginaSiNecesario()
            canvas.drawText("Fecha: ${formatoFecha.format(Date(pedido.fechaMillis))}  -  Total: ${pedido.totalCop.aFormatoCop()}", 40f, y, paintTexto)
            y += 16f
            items.forEach { (desc, costo) ->
                saltoDePaginaSiNecesario()
                canvas.drawText("   • $desc: ${costo.aFormatoCop()}", 50f, y, paintTexto)
                y += 14f
            }
            y += 8f
        }

        y += 10f
        saltoDePaginaSiNecesario()
        canvas.drawText("Abonos", 40f, y, paintEncabezado); y += 20f
        abonos.forEach { abono ->
            saltoDePaginaSiNecesario()
            canvas.drawText("Fecha: ${formatoFecha.format(Date(abono.fechaMillis))}  -  Abono: ${abono.montoCop.aFormatoCop()}", 40f, y, paintTexto)
            y += 16f
        }

        documento.finishPage(pagina)

        val carpeta = File(context.externalCacheDir, "reportes").apply { mkdirs() }
        val archivo = File(carpeta, "reporte_${cliente.nombre.replace(" ", "_")}.pdf")
        FileOutputStream(archivo).use { documento.writeTo(it) }
        documento.close()
        return archivo
    }

    fun uriParaCompartir(context: Context, archivo: File) =
        FileProvider.getUriForFile(context, "com.abonos.control.fileprovider", archivo)
}
