package com.abonos.control.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abonos.control.data.Repositorio
import com.abonos.control.data.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface EstadoLogin {
    object Inicial : EstadoLogin
    object Cargando : EstadoLogin
    data class Exito(val usuario: UsuarioEntity) : EstadoLogin
    data class Error(val mensaje: String) : EstadoLogin
}

class LoginViewModel(private val repo: Repositorio) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoLogin>(EstadoLogin.Inicial)
    val estado: StateFlow<EstadoLogin> = _estado

    fun ingresar(usuario: String, password: String) {
        if (usuario.isBlank() || password.isBlank()) {
            _estado.value = EstadoLogin.Error("Ingresa usuario y contraseña")
            return
        }
        viewModelScope.launch {
            _estado.value = EstadoLogin.Cargando
            val encontrado = repo.autenticar(usuario, password)
            _estado.value = if (encontrado != null) {
                EstadoLogin.Exito(encontrado)
            } else {
                EstadoLogin.Error("Usuario o contraseña incorrectos")
            }
        }
    }
}
