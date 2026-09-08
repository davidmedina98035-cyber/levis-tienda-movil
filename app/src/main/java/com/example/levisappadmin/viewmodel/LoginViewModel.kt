package com.example.levisappadmin.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levisappadmin.model.LoginRequest
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    sealed class Estado {
        object Idle : Estado()
        object Cargando : Estado()
        object Exitoso : Estado()
        data class Error(val mensaje: String) : Estado()
    }

    private val _estado = MutableStateFlow<Estado>(Estado.Idle)
    val estado: StateFlow<Estado> = _estado

    private var _idUsuario = 0
    val idUsuario get() = _idUsuario

    fun login(onSuccess: (String, String, Int) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _estado.value = Estado.Error("Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _estado.value = Estado.Cargando
            try {
                val response = RetrofitClient.api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    _estado.value = Estado.Exitoso
                    val body = response.body()

                    val token = body?.Token ?: ""
                    val id = body?.id_usuario ?: 0

                    _idUsuario = id
                    onSuccess(token, email, id)
                } else {
                    _estado.value = Estado.Error("Error ${response.code()}: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _estado.value = Estado.Error("Sin conexión: ${e.message}")
            }
        }
    }
}