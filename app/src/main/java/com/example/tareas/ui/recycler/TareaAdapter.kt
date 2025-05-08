package com.example.tareas.ui.recycler

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tareas.R
import com.example.tareas.databinding.ItemTareaBinding
import com.example.tareas.models.Tarea

class TareaAdapter(
    private val onDelete: (Tarea) -> Unit,
    val onToggleCompleted: (Tarea, Boolean) -> Unit,
    private val onEdit: (Tarea) -> Unit
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    var listTarea = mutableListOf<Tarea>()

    private val userColors = listOf(
        R.color.user_color_1,
        R.color.user_color_2,
        R.color.user_color_3,
        R.color.user_color_4,
        R.color.user_color_5,
        R.color.user_color_6,
        R.color.user_color_7,
        R.color.user_color_8,
        R.color.user_color_9,
        R.color.user_color_10
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val binding = ItemTareaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TareaViewHolder(binding, onDelete, onEdit)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val item = listTarea[position]
        holder.bind(item)

        val params = holder.binding.cardView.layoutParams as ViewGroup.MarginLayoutParams

        if (position == 0 || item.userId != listTarea[position - 1].userId) {
            params.topMargin = holder.binding.root.context.resources.getDimensionPixelSize(R.dimen.margin_large)
        } else {
            params.topMargin = holder.binding.root.context.resources.getDimensionPixelSize(R.dimen.margin_small)
        }

        holder.binding.cardView.layoutParams = params
    }

    private var lastUserId: Int? = null

    fun update(newList: List<Tarea>): Boolean {
        val tareaDiffUtil = TareaDiffUtil(listTarea, newList)
        val check = DiffUtil.calculateDiff(tareaDiffUtil)

        val oldSize = listTarea.size
        val newSize = newList.size
        val newUserId = newList.firstOrNull()?.userId

        val shouldScroll = when {
            newUserId != lastUserId -> true
            newSize > oldSize -> true
            else -> false
        }

        listTarea.clear()
        listTarea.addAll(newList)
        lastUserId = newUserId
        check.dispatchUpdatesTo(this)

        return shouldScroll
    }

    override fun getItemCount(): Int = listTarea.size

    inner class TareaViewHolder(
        val binding: ItemTareaBinding,
        val onDelete: (Tarea) -> Unit,
        val onEdit: (Tarea) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Tarea) {

            val colorRes = userColors.getOrNull(item.userId - 1) ?: R.color.white
            val backgroundColor = ContextCompat.getColor(binding.root.context, colorRes)
            binding.cardView.setCardBackgroundColor(backgroundColor)

            binding.tvUsuario.text = buildString {
                append("Usuario: ")
                append(item.userId)
            }
            binding.tvTarea.text = buildString {
                append("Tarea: ")
                append(item.id)
            }

            binding.tvDescriptionTarea.text = item.title.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }

            binding.checkBoxCompleted.setOnCheckedChangeListener(null)
            binding.checkBoxCompleted.isChecked = item.completed

            applyTextStyle(item.completed)

            binding.checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
                onToggleCompleted(item, isChecked)
                applyTextStyle(isChecked)
            }

            binding.imageViewDelete.setOnClickListener {
                onDelete(item)
            }

            binding.cardView.setOnClickListener {
                onEdit(item)
            }
        }

        private fun applyTextStyle(completed: Boolean) {
            if (completed) {
                val primaryColor = binding.root.context.getColor(R.color.color_primary)
                binding.tvDescriptionTarea.setTextColor(primaryColor)
                binding.tvDescriptionTarea.paintFlags =
                    binding.tvDescriptionTarea.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.tvDescriptionTarea.setTextColor(
                    binding.root.context.getColor(android.R.color.holo_red_dark)
                )
                binding.tvDescriptionTarea.paintFlags =
                    binding.tvDescriptionTarea.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }
}

class TareaDiffUtil(
    private val oldList: List<Tarea>,
    private val newList: List<Tarea>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.title == newItem.title &&
                oldItem.completed == newItem.completed &&
                oldItem.userId == newItem.userId
    }
}
