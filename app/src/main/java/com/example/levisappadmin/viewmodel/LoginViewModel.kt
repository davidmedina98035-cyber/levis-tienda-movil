package com.example.levisappadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import com.example.levisappadmin.model.LoginRequest
import com.example.levisappadmin.model.RegisterRequest
import com.example.levisappadmin.model.User
import com.example.levisappadmin.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val api = RetrofitClient.api

    val error = mutableStateOf<String?>(null)
    val cargando = mutableStateOf(false)
    val user = mutableStateOf<User?>(null)

    fun registrarUsuario(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        direccion: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            cargando.value = true
            try {
                val response = api.register(
                    RegisterRequest(nombre, email, password, telefono, direccion)
                )
                if (response.isSuccessful) {
                    user.value = response.body()?.user
                    error.value = null
                    onSuccess()
                } else {
                    error.value = response.errorBody()?.string() ?: "Error en registro"
                }
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                cargando.value = false
            }
        }
    }

    fun loginUsuario(email: String, password: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            cargando.value = true
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loggedUser = response.body()?.user
                    if (loggedUser != null) {
                        user.value = loggedUser
                        error.value = null
                        // Usamos el id_usuario como "token" de navegación, ya que no hay JWT
                        onSuccess(loggedUser.id_usuario.toString())
                    } else {
                        error.value = "No se recibió usuario del servidor"
                    }
                } else {
                    error.value = response.errorBody()?.string() ?: "Error en login"
                }
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                cargando.value = false
            }
        }
    }
}