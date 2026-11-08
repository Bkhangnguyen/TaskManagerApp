package com.example.taskmanager.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ItemTaskBinding
import com.example.taskmanager.model.Task

class TaskAdapter(
    private val onStatusClick: (Task) -> Unit,
    private val onTaskClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback) {

    class TaskViewHolder(
        val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        val binding = holder.binding
        val context = binding.root.context

        binding.tvTaskTitle.text = task.title
        binding.tvTaskDescription.text = task.description
        binding.tvCategory.text = task.category
        binding.tvAssignee.text = task.assignee
        binding.tvTaskStatus.text = task.status

        // Áp dụng màu cho Tag Assignee (Người được giao)
        val assigneeBg = GradientDrawable().apply {
            cornerRadius = 16f
            when (task.assignee) {
                "Dương" -> {
                    setColor(ContextCompat.getColor(context, R.color.tag_duong))
                    binding.tvAssignee.setTextColor(ContextCompat.getColor(context, R.color.tag_duong_text))
                }
                "Khôi" -> {
                    setColor(ContextCompat.getColor(context, R.color.tag_khoi))
                    binding.tvAssignee.setTextColor(ContextCompat.getColor(context, R.color.tag_khoi_text))
                }
                "Kiệt" -> {
                    setColor(ContextCompat.getColor(context, R.color.tag_kiet))
                    binding.tvAssignee.setTextColor(ContextCompat.getColor(context, R.color.tag_kiet_text))
                }
                "Khoa" -> {
                    setColor(ContextCompat.getColor(context, R.color.tag_khoa))
                    binding.tvAssignee.setTextColor(ContextCompat.getColor(context, R.color.tag_khoa_text))
                }
                else -> {
                    setColor(Color.parseColor("#E2E8F0"))
                    binding.tvAssignee.setTextColor(Color.parseColor("#475569"))
                }
            }
        }
        binding.tvAssignee.background = assigneeBg

        // Áp dụng màu cho Chip Trạng thái (Status)
        val statusBg = GradientDrawable().apply {
            cornerRadius = 20f
            when (task.status) {
                "Hoàn thành" -> {
                    setColor(ContextCompat.getColor(context, R.color.status_done_bg))
                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.status_done_text))
                }
                "Cần chỉnh sửa" -> {
                    setColor(ContextCompat.getColor(context, R.color.status_review_bg))
                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.status_review_text))
                }
                "Đang làm" -> {
                    setColor(ContextCompat.getColor(context, R.color.status_doing_bg))
                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.status_doing_text))
                }
                else -> {
                    setColor(ContextCompat.getColor(context, R.color.status_todo_bg))
                    binding.tvTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.status_todo_text))
                }
            }
        }
        binding.tvTaskStatus.background = statusBg

        // Hiển thị và xử lý click vào link kết quả
        if (!task.resultUrl.isNullOrBlank()) {
            binding.ivResultLink.visibility = View.VISIBLE
            binding.ivResultLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(task.resultUrl))
                context.startActivity(intent)
            }
        } else {
            binding.ivResultLink.visibility = View.GONE
        }

        // Tương tác đổi trạng thái nhanh khi bấm vào status chip
        binding.tvTaskStatus.setOnClickListener {
            onStatusClick(task)
        }

        binding.root.setOnClickListener {
            onTaskClick(task)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }
}
