package com.example.taskmanager.ui.task

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.taskmanager.databinding.DialogAddTaskBinding
import com.example.taskmanager.viewmodel.TaskViewModel

class AddTaskDialogFragment : DialogFragment() {

    private var _binding: DialogAddTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels {
        val app = requireActivity().application as com.example.taskmanager.TaskManagerApp
        TaskViewModel.Factory(app.taskRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSaveTask.setOnClickListener {
            val title = binding.etTaskTitle.text?.toString()?.trim().orEmpty()
            val description = binding.etTaskDescription.text?.toString()?.trim().orEmpty()
            val assignee = binding.etAssignee.text?.toString()?.trim().orEmpty()
            val category = binding.etCategory.text?.toString()?.trim().orEmpty()
            val url = binding.etResultUrl.text?.toString()?.trim()

            if (title.isBlank()) {
                binding.etTaskTitle.error = "Vui lòng nhập tên công việc"
                return@setOnClickListener
            }

            viewModel.addTask(title, description, assignee, category, url)
            Toast.makeText(requireContext(), "Đã thêm công việc thành công!", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
