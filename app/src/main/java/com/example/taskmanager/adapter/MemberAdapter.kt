package com.example.taskmanager.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.databinding.ItemMemberBinding
import com.example.taskmanager.model.Member

class MemberAdapter : ListAdapter<Member, MemberAdapter.MemberViewHolder>(DiffCallback) {

    class MemberViewHolder(
        val binding: ItemMemberBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val item = getItem(position)
        val binding = holder.binding

        binding.tvMemberName.text = item.name
        binding.tvMemberRole.text = item.role
        binding.tvAssignedTasks.text = "${item.assignedTasksCount} công việc"
        binding.tvAvatar.text = item.name.firstOrNull()?.toString()?.uppercase() ?: "U"

        val avatarCircle = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(item.tagColorHex))
        }
        binding.tvAvatar.background = avatarCircle
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Member>() {
        override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean = oldItem == newItem
    }
}
