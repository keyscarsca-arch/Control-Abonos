package com.abonos.control

import android.app.Application
import com.abonos.control.data.AppDatabase
import com.abonos.control.data.Repositorio

class ControlAbonosApp : Application() {
    lateinit var repositorio: Repositorio
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.obtener(this)
        repositorio = Repositorio(db)
    }
}
