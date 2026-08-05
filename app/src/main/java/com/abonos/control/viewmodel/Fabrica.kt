package com.abonos.control.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.viewModelFactory
import com.abonos.control.ControlAbonosApp

/** Fábrica genérica para crear ViewModels con el Repositorio de la Application. */
inline fun <reified T : ViewModel> fabricaViewModel(
    crossinline crear: (com.abonos.control.data.Repositorio) -> T
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ControlAbonosApp)
        crear(app.repositorio)
    }
}
