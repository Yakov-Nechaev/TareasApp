package com.example.tareas.models

data class Tarea(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)