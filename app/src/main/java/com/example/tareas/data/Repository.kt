package com.example.tareas.data

import android.content.Context
import android.util.Log
import com.example.tareas.commons.isInternetAvailable
import com.example.tareas.models.Tarea

class Repository(private val context: Context, private val network: ApiService) {

    suspend fun crearTarea(title: String, userId: Int): Resultado<Unit> {
        if (!context.isInternetAvailable()) {
            Log.e("Repository", "No hay conexión a Internet")
            return Resultado.Error("Sin conexión a Internet")
        }
        return try {
            val nuevaTarea = Tarea(userId = userId, title = title, id = 0, completed = false)
            network.createTodo(nuevaTarea)
            Resultado.Success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Error al crear tarea", e)
            Resultado.Error("Error al crear la tarea")
        }
    }


    suspend fun obtenerTodasLasTareas(): Resultado<List<Tarea>> {
        if (!context.isInternetAvailable()) {
            Log.e("Repository", "No hay conexión a Internet")
            return Resultado.Error("Sin conexión a Internet")
        }
        return try {
            val lista = network.getTodos()
            Resultado.Success(lista)
        } catch (e: Exception) {
            Log.e("Repository", "Error al obtener tareas", e)
            Resultado.Error("Error al obtener las tareas")
        }
    }

    suspend fun obtenerTareasPorUsuario(id: Int): Resultado<List<Tarea>> {
        if (!context.isInternetAvailable()) {
            Log.e("Repository", "No hay conexión a Internet")
            return Resultado.Error("Sin conexión a Internet")
        }
        return try {
            val lista = network.getById(userId = id)
            Resultado.Success(lista)
        } catch (e: Exception) {
            Log.e("Repository", "Error al obtener tareas por usuario", e)
            Resultado.Error("Error al obtener las tareas del usuario")
        }
    }

    suspend fun eliminarTarea(id: Int): Resultado<Unit> {
        if (!context.isInternetAvailable()) {
            Log.e("Repository", "No hay conexión a Internet")
            return Resultado.Error("Sin conexión a Internet")
        }
        return try {
            network.deleteTodo(id)
            Resultado.Success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Error al eliminar tarea", e)
            Resultado.Error("Error al eliminar la tarea")
        }
    }

    suspend fun actualizarTarea(tarea: Tarea): Resultado<Unit> {
        if (!context.isInternetAvailable()) {
            Log.e("Repository", "No hay conexión a Internet")
            return Resultado.Error("Sin conexión a Internet")
        }
        return try {
            network.updateTodo(tarea.id, tarea)
            Resultado.Success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Error al actualizar tarea", e)
            Resultado.Error("Error al actualizar la tarea")
        }
    }
}