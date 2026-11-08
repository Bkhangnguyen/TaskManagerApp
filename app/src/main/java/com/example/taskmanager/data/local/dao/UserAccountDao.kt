package com.example.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskmanager.data.local.entity.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {

    @Query("SELECT * FROM user_accounts ORDER BY lastLoginAt DESC")
    fun getAllAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getAccountByEmail(email: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(account: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<UserAccountEntity>)

    @Query("UPDATE user_accounts SET lastLoginAt = :loginTime WHERE email = :email")
    suspend fun updateLastLogin(email: String, loginTime: Long)

    @Query("UPDATE user_accounts SET currentProjectName = :projectName WHERE email = :email")
    suspend fun updateProject(email: String, projectName: String)
}
