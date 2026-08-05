package com.abonos.control.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Usuario del sistema (login). Regla: siempre debe existir al menos un administrador,
 * validado en UsuarioDao / UsuariosViewModel antes de permitir eliminar.
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombreUsuario: String,
    val password: String,       // en producción real: guardar hash, no texto plano
    val esAdministrador: Boolean = true
)

/**
 * Cliente / negocio al que se le hacen pedidos y se le controla la deuda.
 */
@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,             // nombre completo del negocio, ej: "Cerrajería el Mago"
    val seudonimo: String,          // usado para generar iniciales de la tarjeta
    val telefono: String? = null,
    val direccion: String? = null,
    val saldoPendiente: Long = 0    // en COP, sin decimales
)

/**
 * Pedido/factura registrado a un cliente en una fecha determinada.
 */
@Entity(
    tableName = "pedidos",
    foreignKeys = [ForeignKey(
        entity = ClienteEntity::class,
        parentColumns = ["id"],
        childColumns = ["clienteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val fechaMillis: Long,      // fecha del pedido (System.currentTimeMillis())
    val totalCop: Long          // suma de los items del pedido, en COP
)

/**
 * Cada línea (producto/servicio) dentro de un pedido.
 */
@Entity(
    tableName = "items_pedido",
    foreignKeys = [ForeignKey(
        entity = PedidoEntity::class,
        parentColumns = ["id"],
        childColumns = ["pedidoId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ItemPedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoId: Long,
    val descripcion: String,
    val costoCop: Long
)

/**
 * Abono (pago parcial o total) registrado contra el saldo pendiente de un cliente.
 */
@Entity(
    tableName = "abonos",
    foreignKeys = [ForeignKey(
        entity = ClienteEntity::class,
        parentColumns = ["id"],
        childColumns = ["clienteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AbonoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val montoCop: Long,
    val fechaMillis: Long
)
