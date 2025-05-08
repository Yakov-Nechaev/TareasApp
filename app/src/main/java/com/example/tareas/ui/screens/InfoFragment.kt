package com.example.tareas.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.tareas.R
import com.example.tareas.databinding.FragmentInfoBinding


class InfoFragment : DialogFragment(R.layout.fragment_info) {

    private lateinit var binding: FragmentInfoBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoBinding.bind(view)
        binding.tvContenido.text = getString(R.string.texto_info)
        binding.btnCerrar.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.9).toInt()
        dialog?.window?.setLayout(width, height)
    }
}
