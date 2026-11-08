package com.example.taskmanager.repository

import com.example.taskmanager.model.Member
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MemberRepository {

    private val members = listOf(
        Member(1, "Dương", "Trưởng nhóm / Core System", 3, "#0F766E"),
        Member(2, "Khôi", "Product & Content", 2, "#B45309"),
        Member(3, "Kiệt", "Kiểm thử & UI/UX", 2, "#4338CA"),
        Member(4, "Khoa", "Giao diện & Thiết kế", 2, "#BE185D")
    )

    private val _membersFlow = MutableStateFlow(members)
    val membersFlow: Flow<List<Member>> = _membersFlow.asStateFlow()
}
