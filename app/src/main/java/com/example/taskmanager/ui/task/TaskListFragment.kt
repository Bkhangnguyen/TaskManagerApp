package com.example.taskmanager.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.adapter.TaskAdapter
import com.example.taskmanager.databinding.FragmentTaskListBinding
import com.example.taskmanager.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onStatusClick = { task ->
                viewModel.toggleTaskStatus(task.id, task.status)
            },
            onTaskClick = { task ->
                Toast.makeText(requireContext(), "${task.title}: ${task.description}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.adapter = adapter
    }

    private fun setupListeners() {
        // Lắng nghe tìm kiếm theo thời gian thực
        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchTasks(text?.toString().orEmpty())
        }

        // Lắng nghe chọn Filter Chip
        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            val status = when (checkedIds.firstOrNull()) {
                R.id.chipTodo -> "Cần làm"
                R.id.chipDoing -> "Đang làm"
                R.id.chipReview -> "Cần chỉnh sửa"
                R.id.chipDone -> "Hoàn thành"
                else -> "Tất cả"
            }
            viewModel.filterByStatus(status)
        }

        // Nút thêm công việc mới (FAB)
        binding.fabAddTask.setOnClickListener {
            val dialog = AddTaskDialogFragment()
            dialog.show(childFragmentManager, "AddTaskDialog")
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredTasks.collect { tasks ->
                    adapter.submitList(tasks)
                    binding.tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
