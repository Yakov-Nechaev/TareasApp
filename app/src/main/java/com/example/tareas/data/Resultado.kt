package com.example.tareas.data

sealed class Resultado<out T> {
    data class Success<T>(val datos: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
}