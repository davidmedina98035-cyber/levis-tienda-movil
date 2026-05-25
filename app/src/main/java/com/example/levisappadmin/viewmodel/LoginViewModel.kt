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

    fun login(onSuccess: (String) -> Unit) {
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
                    val Token = response.body()?.Token ?: ""
                    onSuccess(Token)
                } else {
                    _estado.value = Estado.Error("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                _estado.value = Estado.Error("Sin conexión: ${e.message}")
            }
        }
    }
}