package com.example.taskmanager.repository

import com.example.taskmanager.data.local.dao.MemberDao
import com.example.taskmanager.data.local.entity.MemberEntity
import com.example.taskmanager.model.Member
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MemberRepository(
    private val memberDao: MemberDao
) {

    val membersFlow: Flow<List<Member>> = memberDao.getAllMembers().map { list ->
        list.map { entity ->
            Member(
                id = entity.id,
                name = entity.name,
                role = entity.role,
                assignedTasksCount = 2,
                tagColorHex = entity.tagColorHex
            )
        }
    }

    suspend fun addMember(name: String, role: String, colorHex: String) {
        memberDao.insertMember(MemberEntity(name = name, role = role, tagColorHex = colorHex))
    }
}
