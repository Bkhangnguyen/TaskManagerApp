package com.example.taskmanager.repository

import com.example.taskmanager.data.local.dao.CategoryDao
import com.example.taskmanager.data.local.entity.CategoryEntity
import com.example.taskmanager.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    val categoriesFlow: Flow<List<Category>> = categoryDao.getAllCategories().map { list ->
        list.map { entity ->
            Category(
                id = entity.id,
                name = entity.name,
                colorHex = entity.colorHex,
                taskCount = 1
            )
        }
    }

    suspend fun addCategory(name: String, colorHex: String) {
        categoryDao.insertCategory(CategoryEntity(name = name, colorHex = colorHex))
    }
}
