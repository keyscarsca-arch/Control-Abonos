package com.abonos.control.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios ORDER BY nombreUsuario ASC")
    fun observarTodos(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :usuario AND password = :password LIMIT 1")
    suspend fun autenticar(usuario: String, password: String): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios WHERE esAdministrador = 1")
    suspend fun contarAdministradores(): Int

    @Query("SELECT COUNT(*) FROM usuarios WHERE nombreUsuario = :usuario AND id != :idExcluir")
    suspend fun contarPorNombre(usuario: String, idExcluir: Long = -1): Int

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Delete
    suspend fun eliminar(usuario: UsuarioEntity)
}

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE id = :id")
    fun observarPorId(id: Long): Flow<ClienteEntity?>

    @Insert
    suspend fun insertar(cliente: ClienteEntity): Long

    @Update
    suspend fun actualizar(cliente: ClienteEntity)

    @Delete
    suspend fun eliminar(cliente: ClienteEntity)

    @Query("UPDATE clientes SET saldoPendiente = saldoPendiente + :delta WHERE id = :clienteId")
    suspend fun ajustarSaldo(clienteId: Long, delta: Long)

    @Query("SELECT COALESCE(SUM(saldoPendiente), 0) FROM clientes")
    fun observarDeudaTotal(): Flow<Long>
}

@Dao
interface PedidoDao {
    @Insert
    suspend fun insertarPedido(pedido: PedidoEntity): Long

    @Insert
    suspend fun insertarItems(items: List<ItemPedidoEntity>)

    @Query("SELECT * FROM pedidos WHERE clienteId = :clienteId ORDER BY fechaMillis DESC")
    fun observarPorCliente(clienteId: Long): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM items_pedido WHERE pedidoId = :pedidoId")
    suspend fun itemsDePedido(pedidoId: Long): List<ItemPedidoEntity>

    @Query("SELECT * FROM pedidos ORDER BY fechaMillis DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>
}

@Dao
interface AbonoDao {
    @Insert
    suspend fun insertar(abono: AbonoEntity): Long

    @Query("SELECT * FROM abonos WHERE clienteId = :clienteId ORDER BY fechaMillis DESC")
    fun observarPorCliente(clienteId: Long): Flow<List<AbonoEntity>>

    @Query("SELECT * FROM abonos ORDER BY fechaMillis DESC")
    fun observarTodos(): Flow<List<AbonoEntity>>
}
