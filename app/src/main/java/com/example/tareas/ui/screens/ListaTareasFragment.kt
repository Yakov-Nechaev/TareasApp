package com.example.tareas.ui.screens

import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.tareas.R
import com.example.tareas.databinding.FragmentListaTareasBinding
import com.example.tareas.models.Tarea
import com.example.tareas.ui.mvvm.MainViewModel
import com.example.tareas.ui.mvvm.MainViewModelFactory
import com.example.tareas.ui.recycler.TareaAdapter
import com.google.android.material.snackbar.Snackbar

class ListaTareasFragment : Fragment(R.layout.fragment_lista_tareas) {

    lateinit var binding: FragmentListaTareasBinding
    private val viewModel by viewModels<MainViewModel> { MainViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListaTareasBinding.bind(view)

        if (savedInstanceState == null) {
            viewModel.cargarTareas()
        }

        //  TOOLBAR
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.info -> InfoFragment().show(childFragmentManager, INFO)
                R.id.renovar -> viewModel.cargarTareas()
            }
            true
        }

        //  ADAPTER
        val adapter = TareaAdapter(
            onDelete = { tarea -> onEliminarTarea(tarea) },
            onToggleCompleted = { tarea, isChecked -> onActualizarCompletada(tarea, isChecked) },
            onEdit = { tarea -> onEditarTarea(tarea) }
        )
        binding.taskRecyclerView.adapter = adapter

        //  OBSERVERS
        viewModel.tareaList.observe(viewLifecycleOwner) { lista ->
            val isAdded = adapter.update(lista)
            if (isAdded) {
                binding.taskRecyclerView.scrollToPosition(0)
                binding.taskRecyclerView.post {
                    binding.taskRecyclerView.scrollBy(0, 2)
                    binding.taskRecyclerView.scrollBy(0, -2)
                }
            }
        }

        viewModel.cargando.observe(viewLifecycleOwner) { cargando ->
            binding.progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).apply {
                    setBackgroundTint(resources.getColor(android.R.color.background_dark, null))
                    setTextColor(resources.getColor(android.R.color.white, null))
                    setActionTextColor(resources.getColor(android.R.color.white, null))
                    setAction(getString(R.string.snackbar_close)) { }
                }.show()
                viewModel.mensaje.value = null
            }
        }


        //  SCROLL LISTENER
        binding.taskRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager =
                    recyclerView.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager
                val firstVisiblePosition = layoutManager?.findFirstVisibleItemPosition() ?: return

                val item = adapter.listTarea.getOrNull(firstVisiblePosition)
                item?.let {
                    binding.toolbar.title = getString(R.string.toolbar_usuario, it.userId)
                    binding.toolbar.subtitle = getString(
                        R.string.toolbar_tarea,
                        it.id,
                        it.title.replaceFirstChar { c -> c.uppercase() }
                    )
                }
            }
        })

        //  BOTONES
        binding.getData.setOnClickListener { viewModel.cargarTareas() }
        binding.fab.setOnClickListener { showCrearTareaDialog() }

        //  SPINNER
        val listEstado = listOf(getString(R.string.spinner_todos)) + List(10) { (it + 1).toString() }
        binding.spinner.adapter =
            ArrayAdapter(requireContext(), R.layout.spinner_item, listEstado)

        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    viewModel.seleccionarUsuario(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    //  EVENTOS / ACCIONES

    private fun onActualizarCompletada(tarea: Tarea, isChecked: Boolean) {
        viewModel.actualizarCompletada(tarea, isChecked)
    }

    private fun onEliminarTarea(tarea: Tarea) {
        val dialogContext = ContextThemeWrapper(requireContext(), R.style.App_LightAlertDialogTheme)

        AlertDialog.Builder(dialogContext)
            .setTitle(getString(R.string.title_eliminar))
            .setPositiveButton(getString(R.string.boton_eliminar)) { _, _ ->
                viewModel.eliminarTarea(tarea)
            }
            .setNegativeButton(getString(R.string.boton_cancelar)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun onEditarTarea(tarea: Tarea) {
        val editText = EditText(requireContext()).apply {
            setText(tarea.title)
            setTextColor(resources.getColor(android.R.color.black, null))
            setHintTextColor(resources.getColor(android.R.color.darker_gray, null))
        }

        val dialogContext = ContextThemeWrapper(requireContext(), R.style.App_LightAlertDialogTheme)

        AlertDialog.Builder(dialogContext)
            .setTitle(getString(R.string.title_editar))
            .setView(editText)
            .setPositiveButton(getString(R.string.boton_guardar)) { _, _ ->
                val nuevoTitulo = editText.text.toString()
                if (nuevoTitulo.isNotBlank()) {
                    viewModel.actualizarTitulo(tarea, nuevoTitulo)
                }
            }
            .setNegativeButton(getString(R.string.boton_cancelar)) { dialog, _ -> dialog.dismiss() }
            .show()
    }


    private fun showCrearTareaDialog() {
        val editText = EditText(requireContext()).apply {
            hint = getString(R.string.hint_titulo)
            setTextColor(resources.getColor(android.R.color.black, null))
            setHintTextColor(resources.getColor(android.R.color.darker_gray, null))
        }

        val dialogContext = ContextThemeWrapper(requireContext(), R.style.App_LightAlertDialogTheme)

        AlertDialog.Builder(dialogContext)
            .setTitle(getString(R.string.title_crear))
            .setView(editText)
            .setPositiveButton(getString(R.string.boton_crear)) { _, _ ->
                val title = editText.text.toString()
                if (title.isNotBlank()) {
                    viewModel.crearTarea(title)
                }
            }
            .setNegativeButton(getString(R.string.boton_cancelar)) { dialog, _ -> dialog.dismiss() }
            .show()
    }


    companion object {
        const val INFO = "info_fragment"
    }
}