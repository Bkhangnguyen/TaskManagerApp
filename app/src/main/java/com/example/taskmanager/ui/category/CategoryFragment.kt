package com.example.taskmanager.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.adapter.CategoryAdapter
import com.example.taskmanager.databinding.FragmentCategoryBinding
import com.example.taskmanager.viewmodel.CategoryViewModel
import com.example.taskmanager.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val categoryViewModel: CategoryViewModel by viewModels {
        val app = requireActivity().application as com.example.taskmanager.TaskManagerApp
        CategoryViewModel.Factory(app.categoryRepository)
    }

    private val taskViewModel: TaskViewModel by activityViewModels {
        val app = requireActivity().application as com.example.taskmanager.TaskManagerApp
        TaskViewModel.Factory(app.taskRepository)
    }

    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo Adapter với sự kiện click chuyển sang lọc công việc
        adapter = CategoryAdapter { selectedCategory ->
            taskViewModel.filterByCategory(selectedCategory.name)
            Toast.makeText(
                requireContext(),
                "Lọc công việc theo hạng mục: ${selectedCategory.name}",
                Toast.LENGTH_SHORT
            ).show()
            // Nhảy trực tiếp sang Tab Danh Sách Công Việc
            findNavController().navigate(R.id.navigation_tasks)
        }

        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = adapter

        // Kết hợp luồng Categories và Tasks để tính toán CHÍNH XÁC số lượng công việc thực tế của từng hạng mục
        val app = requireActivity().application as com.example.taskmanager.TaskManagerApp
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    categoryViewModel.categories,
                    app.taskRepository.tasksFlow
                ) { categories, tasks ->
                    categories.map { category ->
                        val realCount = tasks.count { task ->
                            task.category.equals(category.name, ignoreCase = true)
                        }
                        category.copy(taskCount = realCount)
                    }
                }.collect { updatedCategories ->
                    adapter.submitList(updatedCategories)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
