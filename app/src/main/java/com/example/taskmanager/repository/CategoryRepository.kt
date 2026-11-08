package com.example.taskmanager.repository

import com.example.taskmanager.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryRepository {

    private val categories = listOf(
        Category(1, "Báo cáo", "#3B82F6", 1),
        Category(2, "Nghiên cứu", "#10B981", 1),
        Category(3, "Nội dung", "#F59E0B", 1),
        Category(4, "Kiểm thử", "#8B5CF6", 1),
        Category(5, "Giao diện", "#EC4899", 2),
        Category(6, "UI/UX", "#06B6D4", 1),
        Category(7, "Quản lý / Git", "#64748B", 1),
        Category(8, "WordPress", "#38BDF8", 1)
    )

    private val _categoriesFlow = MutableStateFlow(categories)
    val categoriesFlow: Flow<List<Category>> = _categoriesFlow.asStateFlow()
}
