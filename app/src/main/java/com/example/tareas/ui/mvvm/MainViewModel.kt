package com.example.tareas.ui.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.tareas.data.NetworkService
import com.example.tareas.data.Repository
import com.example.tareas.data.Resultado
import com.example.tareas.models.Tarea
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class MainViewModel(val repository: Repository) : ViewModel() {

    val tareaList = MutableLiveData<List<Tarea>>()
    val cargando = MutableLiveData<Boolean>()
    val mensaje = MutableLiveData<String?>()

    private var filtroUsuarioId = 0

    fun seleccionarUsuario(id: Int) {
        filtroUsuarioId = id
    }

    fun cargarTareas() {
        viewModelScope.launch {
            cargando.value = true
            val resultado = if (filtroUsuarioId == 0) {
                repository.obtenerTodasLasTareas()
            } else {
                repository.obtenerTareasPorUsuario(filtroUsuarioId)
            }
            cargando.value = false

            when (resultado) {
                is Resultado.Success -> {
                    tareaList.value = resultado.datos
                    mensaje.value = "Tareas cargadas correctamente."
                }
                is Resultado.Error -> {
                    tareaList.value = emptyList()
                    mensaje.value = resultado.mensaje
                }
            }
        }
    }

    fun actualizarTitulo(tarea: Tarea, nuevoTitulo: String) {
        viewModelScope.launch {
            cargando.value = true
            val tareaActualizada = tarea.copy(title = nuevoTitulo)
            val resultado = repository.actualizarTarea(tareaActualizada)
            cargando.value = false

            when (resultado) {
                is Resultado.Success -> {
                    val nuevaLista = tareaList.value.orEmpty().map { tareaExistente ->
                        if (tareaExistente.id == tarea.id) tareaExistente.copy(title = nuevoTitulo)
                        else tareaExistente
                    }
                    tareaList.value = nuevaLista
                    mensaje.value = "Tarea actualizada."
                }
                is Resultado.Error -> {
                    mensaje.value = resultado.mensaje
                }
            }
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            cargando.value = true
            val resultado = repository.eliminarTarea(tarea.id)
            cargando.value = false

            when (resultado) {
                is Resultado.Success -> {
                    val nuevaLista = tareaList.value.orEmpty().filter { it.id != tarea.id }
                    tareaList.value = nuevaLista
                    mensaje.value = "Tarea eliminada."
                }
                is Resultado.Error -> {
                    mensaje.value = resultado.mensaje
                }
            }
        }
    }

    fun crearTarea(title: String) {
        viewModelScope.launch {
            cargando.value = true
            val nuevoUsuarioId = if (filtroUsuarioId == 0) 1 else filtroUsuarioId
            val resultado = repository.crearTarea(title, nuevoUsuarioId)
            cargando.value = false

            when (resultado) {
                is Resultado.Success -> {
                    val newId = (tareaList.value.orEmpty().maxOfOrNull { it.id } ?: 0) + 1

                    val nuevaTarea = Tarea(
                        userId = nuevoUsuarioId,
                        id = newId,
                        title = title,
                        completed = false
                    )

                    val nuevaLista = listOf(nuevaTarea) + tareaList.value.orEmpty()
                    tareaList.value = nuevaLista
                    mensaje.value = "Tarea creada."
                }

                is Resultado.Error -> {
                    mensaje.value = resultado.mensaje
                }
            }
        }
    }


    fun actualizarCompletada(tarea: Tarea, isChecked: Boolean) {
        viewModelScope.launch {
            cargando.value = true
            val tareaActualizada = tarea.copy(completed = isChecked)
            val resultado = repository.actualizarTarea(tareaActualizada)
            cargando.value = false

            when (resultado) {
                is Resultado.Success -> {
                    val nuevaLista = tareaList.value.orEmpty().map { tareaExistente ->
                        if (tareaExistente.id == tarea.id) tareaExistente.copy(completed = isChecked)
                        else tareaExistente
                    }
                    tareaList.value = nuevaLista
                }
                is Resultado.Error -> {
                    mensaje.value = resultado.mensaje
                }
            }
        }
    }
}

class MainViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[APPLICATION_KEY])
        val context = application.applicationContext
        val apiService = NetworkService.apiService
        val repository = Repository(context = context, network = apiService)
        return MainViewModel(repository = repository) as T
    }
}







