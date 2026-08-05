package com.abonos.control.data

import kotlinx.coroutines.flow.Flow

/**
 * Capa única de acceso a datos. Las pantallas/ViewModels nunca hablan directo con los DAOs.
 */
class Repositorio(private val db: AppDatabase) {

    // ---------- Usuarios ----------
    fun observarUsuarios(): Flow<List<UsuarioEntity>> = db.usuarioDao().observarTodos()

    suspend fun autenticar(usuario: String, password: String): UsuarioEntity? =
        db.usuarioDao().autenticar(usuario.trim(), password)

    suspend fun guardarUsuario(usuario: UsuarioEntity): Result<Unit> {
        val duplicado = db.usuarioDao().contarPorNombre(usuario.nombreUsuario, usuario.id)
        if (duplicado > 0) return Result.failure(IllegalStateException("Ya existe un usuario con ese nombre"))
        if (usuario.id == 0L) db.usuarioDao().insertar(usuario) else db.usuarioDao().actualizar(usuario)
        return Result.success(Unit)
    }

    /** Regla estricta: nunca puede quedar la base de datos sin al menos un administrador. */
    suspend fun eliminarUsuario(usuario: UsuarioEntity): Result<Unit> {
        val totalAdmins = db.usuarioDao().contarAdministradores()
        if (usuario.esAdministrador && totalAdmins <= 1) {
            return Result.failure(IllegalStateException("Debe existir al menos un usuario administrador"))
        }
        db.usuarioDao().eliminar(usuario)
        return Result.success(Unit)
    }

    // ---------- Clientes ----------
    fun observarClientes(): Flow<List<ClienteEntity>> = db.clienteDao().observarTodos()
    fun observarCliente(id: Long): Flow<ClienteEntity?> = db.clienteDao().observarPorId(id)
    fun observarDeudaTotal(): Flow<Long> = db.clienteDao().observarDeudaTotal()

    suspend fun crearCliente(cliente: ClienteEntity): Long = db.clienteDao().insertar(cliente)
    suspend fun actualizarCliente(cliente: ClienteEntity) = db.clienteDao().actualizar(cliente)
    suspend fun eliminarCliente(cliente: ClienteEntity) = db.clienteDao().eliminar(cliente)

    // ---------- Pedidos ----------
    fun observarPedidosDeCliente(clienteId: Long): Flow<List<PedidoEntity>> =
        db.pedidoDao().observarPorCliente(clienteId)

    fun observarTodosLosPedidos(): Flow<List<PedidoEntity>> = db.pedidoDao().observarTodos()

    suspend fun itemsDePedido(pedidoId: Long): List<ItemPedidoEntity> = db.pedidoDao().itemsDePedido(pedidoId)

    /** Guarda el pedido con sus items y suma el total a la deuda del cliente. */
    suspend fun guardarPedido(clienteId: Long, items: List<Pair<String, Long>>): Long {
        val total = items.sumOf { it.second }
        val pedidoId = db.pedidoDao().insertarPedido(
            PedidoEntity(clienteId = clienteId, fechaMillis = System.currentTimeMillis(), totalCop = total)
        )
        db.pedidoDao().insertarItems(items.map { (desc, costo) ->
            ItemPedidoEntity(pedidoId = pedidoId, descripcion = desc, costoCop = costo)
        })
        db.clienteDao().ajustarSaldo(clienteId, total)
        return pedidoId
    }

    // ---------- Abonos ----------
    fun observarAbonosDeCliente(clienteId: Long): Flow<List<AbonoEntity>> =
        db.abonoDao().observarPorCliente(clienteId)

    fun observarTodosLosAbonos(): Flow<List<AbonoEntity>> = db.abonoDao().observarTodos()

    /** Registra el abono y resta el monto del saldo pendiente del cliente. */
    suspend fun registrarAbono(clienteId: Long, montoCop: Long) {
        db.abonoDao().insertar(AbonoEntity(clienteId = clienteId, montoCop = montoCop, fechaMillis = System.currentTimeMillis()))
        db.clienteDao().ajustarSaldo(clienteId, -montoCop)
    }
}
