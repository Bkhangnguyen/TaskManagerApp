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
import com.example.taskmanager.data.remote.Resource
import com.example.taskmanager.data.security.SecurityPrefManager
import com.example.taskmanager.databinding.FragmentTaskListBinding
import com.example.taskmanager.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels {
        val app = requireActivity().application as com.example.taskmanager.TaskManagerApp
        TaskViewModel.Factory(app.taskRepository)
    }
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

        // Kích hoạt đồng bộ REST API qua Coroutines Dispatchers.IO
        viewModel.fetchTasksFromApi()
    }

    private fun setupRecyclerView() {
        val securityPref = SecurityPrefManager(requireContext())
        val isAdmin = securityPref.isAdmin()

        adapter = TaskAdapter(
            onStatusClick = { task ->
                viewModel.toggleTaskStatus(task.id, task.status)
            },
            onTaskClick = { task ->
                if (isAdmin) {
                    // Admin có thể xóa task hoặc xem chi tiết
                    android.app.AlertDialog.Builder(requireContext())
                        .setTitle(task.title)
                        .setMessage("${task.description}\n\nNgười nhận: ${task.assignee}\nTrạng thái: ${task.status}")
                        .setPositiveButton("Đóng", null)
                        .setNegativeButton("Xóa task này") { _, _ ->
                            viewModel.deleteTask(task.id)
                            Toast.makeText(requireContext(), "Đã xóa công việc", Toast.LENGTH_SHORT).show()
                        }
                        .show()
                } else {
                    Toast.makeText(requireContext(), "${task.title}: ${task.description}", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.adapter = adapter
    }

    private fun setupListeners() {
        val securityPref = SecurityPrefManager(requireContext())
        val isAdmin = securityPref.isAdmin()

        // Hiển thị tên dự án hiện tại trên banner
        binding.tvActiveProjectName.text = "📁 ${securityPref.getCurrentProject()}"

        // Bấm đổi dự án / nhập mã mời / tạo nhóm
        binding.btnSwitchProject.setOnClickListener {
            val dialog = com.example.taskmanager.ui.project.ProjectManageDialog { newProjectName ->
                binding.tvActiveProjectName.text = "📁 $newProjectName"
                (activity as? com.example.taskmanager.MainActivity)?.updateProjectSubtitle(newProjectName)
            }
            dialog.show(childFragmentManager, "ProjectManageDialog")
        }

        // Phân quyền: Chỉ Admin mới thấy nút tạo công việc (+)
        binding.fabAddTask.visibility = if (isAdmin) View.VISIBLE else View.GONE
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

        // Nút thử lại khi gặp lỗi kết nối API
        binding.btnRetry.setOnClickListener {
            viewModel.fetchTasksFromApi()
        }
    }

    private fun observeViewModel() {
        val securityPref = SecurityPrefManager(requireContext())

        // Quan sát danh sách công việc tự động cập nhật
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredTasks.collect { tasks ->
                    val curProject = securityPref.getCurrentProject()
                    if (curProject.contains("Chưa tham gia", ignoreCase = true)) {
                        adapter.submitList(emptyList())
                        binding.tvEmpty.text = "Bạn chưa tham gia dự án nào.\nVui lòng bấm 'Mã mời / Đổi' ở trên để nhập mã (Ví dụ: TMDT2026) hoặc tự tạo dự án cá nhân!"
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        adapter.submitList(tasks)
                        binding.tvEmpty.text = "Chưa có công việc nào trong dự án này"
                        binding.tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }
        }

        // Milestone 4: Quản lý 3 trạng thái Resource State (Loading, Success, Error)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resourceState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.layoutError.visibility = View.GONE
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.layoutError.visibility = View.GONE
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.layoutError.visibility = View.VISIBLE
                            binding.tvErrorMessage.text = state.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
