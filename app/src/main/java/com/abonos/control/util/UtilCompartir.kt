package com.abonos.control.util

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Funciones nativas de Android para compartir el reporte generado
 * por correo electrónico o directamente por WhatsApp.
 */
object UtilCompartir {

    /** Abre el selector estándar de Android (incluye correo, WhatsApp, Drive, etc.) */
    fun compartirArchivo(context: Context, uri: Uri, tipoMime: String, tituloChooser: String = "Compartir reporte") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = tipoMime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, tituloChooser))
    }

    /** Intenta abrir directamente WhatsApp (o WhatsApp Business) con el archivo adjunto. */
    fun compartirPorWhatsapp(context: Context, uri: Uri, tipoMime: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = tipoMime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.whatsapp")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Si no está instalado WhatsApp normal, intenta con WhatsApp Business
            intent.setPackage("com.whatsapp.w4b")
            try {
                context.startActivity(intent)
            } catch (e2: Exception) {
                // Si tampoco existe, cae al selector genérico
                compartirArchivo(context, uri, tipoMime)
            }
        }
    }

    /** Abre un correo con el archivo adjunto usando apps de correo instaladas. */
    fun compartirPorCorreo(context: Context, uri: Uri, tipoMime: String, asunto: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = tipoMime
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Enviar por correo"))
    }
}
