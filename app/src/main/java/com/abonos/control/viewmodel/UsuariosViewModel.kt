package com.abonos.control.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abonos.control.data.Repositorio
import com.abonos.control.data.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuariosViewModel(private val repo: Repositorio) : ViewModel() {

    val usuarios: StateFlow<List<UsuarioEntity>> = repo.observarUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    fun guardar(usuario: UsuarioEntity, alTerminar: () -> Unit) {
        viewModelScope.launch {
            val resultado = repo.guardarUsuario(usuario)
            resultado.onSuccess { alTerminar() }
                .onFailure { _mensajeError.value = it.message }
        }
    }

    fun eliminar(usuario: UsuarioEntity) {
        viewModelScope.launch {
            val resultado = repo.eliminarUsuario(usuario)
            resultado.onFailure { _mensajeError.value = it.message }
        }
    }

    fun limpiarError() { _mensajeError.value = null }
}
