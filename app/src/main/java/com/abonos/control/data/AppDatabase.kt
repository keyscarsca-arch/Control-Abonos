package com.abonos.control.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UsuarioEntity::class,
        ClienteEntity::class,
        PedidoEntity::class,
        ItemPedidoEntity::class,
        AbonoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun clienteDao(): ClienteDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun abonoDao(): AbonoDao

    companion object {
        @Volatile private var INSTANCIA: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "control_abonos.db"
                )
                    // Base de datos 100% local: no requiere internet ni sincronización
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Al crear la BD por primera vez, se asegura un admin por defecto
                            // (regla: nunca puede quedar sin al menos un administrador)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCIA?.usuarioDao()?.insertar(
                                    UsuarioEntity(
                                        nombreUsuario = "admin",
                                        password = "admin123",
                                        esAdministrador = true
                                    )
                                )
                            }
                        }
                    })
                    .build()
                    .also { INSTANCIA = it }
            }
    }
}
