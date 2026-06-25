package com.example.levisappadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levisappadmin.model.ActualizarUsuarioRequest
import com.example.levisappadmin.model.CrearUsuarioRequest
import com.example.levisappadmin.model.Usuario
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel : ViewModel() {

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    fun cargarUsuarios(token: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = RetrofitClient.api.getUsuarios("Bearer $token")
                if (response.isSuccessful) {
                    _usuarios.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar usuarios"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun crearUsuario(token: String, request: CrearUsuarioRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.crearUsuario("Bearer $token", request)
                if (response.isSuccessful) {
                    cargarUsuarios(token)
                    onSuccess()
                } else {
                    _error.value = "Error al crear usuario"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión: ${e.message}"
            }
        }
    }

    fun actualizarUsuario(token: String, id: Int, request: ActualizarUsuarioRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.actualizarUsuario("Bearer $token", id, request)
                if (response.isSuccessful) {
                    cargarUsuarios(token)
                    onSuccess()
                } else {
                    _error.value = "Error al actualizar usuario"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión: ${e.message}"
            }
        }
    }

    fun eliminarUsuario(token: String, id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.eliminarUsuario("Bearer $token", id)
                if (response.isSuccessful) {
                    cargarUsuarios(token)
                } else {
                    _error.value = "Error al eliminar usuario"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión: ${e.message}"
            }
        }
    }
}