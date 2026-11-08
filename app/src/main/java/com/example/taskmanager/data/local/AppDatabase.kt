package com.example.taskmanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmanager.data.local.dao.CategoryDao
import com.example.taskmanager.data.local.dao.MemberDao
import com.example.taskmanager.data.local.dao.ProjectDao
import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.dao.TaskResultDao
import com.example.taskmanager.data.local.dao.UserAccountDao
import com.example.taskmanager.data.local.entity.CategoryEntity
import com.example.taskmanager.data.local.entity.MemberEntity
import com.example.taskmanager.data.local.entity.ProjectEntity
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.local.entity.TaskResultEntity
import com.example.taskmanager.data.local.entity.UserAccountEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        CategoryEntity::class,
        MemberEntity::class,
        TaskResultEntity::class,
        ProjectEntity::class,
        UserAccountEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun memberDao(): MemberDao
    abstract fun taskResultDao(): TaskResultDao
    abstract fun projectDao(): ProjectDao
    abstract fun userAccountDao(): UserAccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope? = null): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_manager_database"
                )
                .addCallback(DatabaseCallback(scope ?: CoroutineScope(Dispatchers.IO)))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                val taskDao = database.taskDao()
                val categoryDao = database.categoryDao()
                val memberDao = database.memberDao()
                val projectDao = database.projectDao()
                val userAccountDao = database.userAccountDao()

                // 1. Nạp dự án ban đầu với mã mời
                projectDao.insertAll(
                    listOf(
                        ProjectEntity(
                            id = 1,
                            name = "TMDT PM: Bán Digital Art",
                            description = "Dự án phát triển website và ứng dụng bán tranh nghệ thuật Digital Art",
                            inviteCode = "TMDT2026",
                            ownerEmail = "admin@taskmanager.com",
                            isPersonal = false
                        ),
                        ProjectEntity(
                            id = 2,
                            name = "Dự án Cá nhân Mẫu",
                            description = "Không gian quản lý công việc riêng của chính bạn",
                            inviteCode = "MYTASK",
                            ownerEmail = "khang@taskmanager.com",
                            isPersonal = true
                        )
                    )
                )

                // 2. Nạp danh sách tài khoản đã đăng ký ban đầu cho Admin theo dõi
                val now = System.currentTimeMillis()
                userAccountDao.insertAll(
                    listOf(
                        UserAccountEntity(
                            email = "admin@taskmanager.com",
                            fullName = "Quản trị viên Hệ thống",
                            role = "ADMIN",
                            registeredAt = now - 86400000L * 7,
                            lastLoginAt = now,
                            currentProjectName = "TMDT PM: Bán Digital Art"
                        ),
                        UserAccountEntity(
                            email = "duong@taskmanager.com",
                            fullName = "Nguyễn Văn Dương",
                            role = "MEMBER",
                            registeredAt = now - 86400000L * 5,
                            lastLoginAt = now - 3600000L * 2,
                            currentProjectName = "TMDT PM: Bán Digital Art"
                        ),
                        UserAccountEntity(
                            email = "khang@taskmanager.com",
                            fullName = "Bùi Khang Nguyên",
                            role = "MEMBER",
                            registeredAt = now - 86400000L * 4,
                            lastLoginAt = now - 1800000L,
                            currentProjectName = "TMDT PM: Bán Digital Art"
                        ),
                        UserAccountEntity(
                            email = "duy@taskmanager.com",
                            fullName = "Trần Hoàng Duy",
                            role = "MEMBER",
                            registeredAt = now - 86400000L * 3,
                            lastLoginAt = now - 7200000L,
                            currentProjectName = "TMDT PM: Bán Digital Art"
                        )
                    )
                )

                // 3. Nạp danh mục (Categories)
                categoryDao.insertAll(
                    listOf(
                        CategoryEntity(name = "Báo cáo", colorHex = "#3B82F6"),
                        CategoryEntity(name = "Nghiên cứu", colorHex = "#10B981"),
                        CategoryEntity(name = "Nội dung", colorHex = "#F59E0B"),
                        CategoryEntity(name = "Kiểm thử", colorHex = "#8B5CF6"),
                        CategoryEntity(name = "Giao diện", colorHex = "#EC4899"),
                        CategoryEntity(name = "UI/UX", colorHex = "#06B6D4"),
                        CategoryEntity(name = "Quản lý / Git", colorHex = "#64748B"),
                        CategoryEntity(name = "WordPress", colorHex = "#38BDF8")
                    )
                )

                // 4. Nạp thành viên (3 thành viên: Dương, Khang, Duy)
                memberDao.insertAll(
                    listOf(
                        MemberEntity(name = "Dương", role = "Trưởng nhóm / Task Core & DB", tagColorHex = "#0F766E"),
                        MemberEntity(name = "Khang", role = "Category & Results Module", tagColorHex = "#B45309"),
                        MemberEntity(name = "Duy", role = "Member & Profile Module", tagColorHex = "#4338CA")
                    )
                )

                // 5. Nạp 9 công việc mẫu chuẩn theo bảng Airtable
                taskDao.insertAll(
                    listOf(
                        TaskEntity(
                            title = "Soạn phần tổng quan báo cáo",
                            description = "Draft các nội dung: Đặt vấn đề, mục tiêu đề tài, khảo sát hiện trạng...",
                            assignee = "Dương",
                            category = "Báo cáo",
                            status = "Cần chỉnh sửa",
                            resultUrl = "https://docs.google.com"
                        ),
                        TaskEntity(
                            title = "Nghiên cứu website Digital Art",
                            description = "Tìm 5 website đang bán Digital Art, Digital Download hoặc artwork online...",
                            assignee = "Khang",
                            category = "Nghiên cứu",
                            status = "Hoàn thành",
                            resultUrl = "https://byvn.net/hs5v"
                        ),
                        TaskEntity(
                            title = "Chuẩn bị dữ liệu 15-20 Digital Art",
                            description = "Chuẩn bị dữ liệu mẫu để nhập WooCommerce. Mỗi sản phẩm bắt buộc có hình ảnh và mô tả...",
                            assignee = "Khang",
                            category = "Nội dung",
                            status = "Hoàn thành",
                            resultUrl = "https://byvn.net/cDkq"
                        ),
                        TaskEntity(
                            title = "Tạo checklist test WooCommerce",
                            description = "Chuẩn bị checklist dùng để test từ tuần 2. Các nhóm cần test: đặt hàng, giỏ hàng, thanh toán...",
                            assignee = "Duy",
                            category = "Kiểm thử",
                            status = "Hoàn thành",
                            resultUrl = "https://docs.google.com"
                        ),
                        TaskEntity(
                            title = "Nghiên cứu UI Digital Art",
                            description = "Tìm 5 website có giao diện phù hợp với concept Digital Art...",
                            assignee = "Duy",
                            category = "Giao diện",
                            status = "Hoàn thành",
                            resultUrl = "https://docs.google.com"
                        ),
                        TaskEntity(
                            title = "Tạo Moodboard / Style Guide ban đầu",
                            description = "Đề xuất style tổng thể của website: Bảng màu, typography, phong cách thiết kế...",
                            assignee = "Duy",
                            category = "Giao diện",
                            status = "Hoàn thành",
                            resultUrl = "https://www.figma.com"
                        ),
                        TaskEntity(
                            title = "Wireframe 4 màn hình chính",
                            description = "Thiết kế wireframe cơ bản: Trang chủ, Danh mục, Chi tiết sản phẩm, Giỏ hàng...",
                            assignee = "Duy",
                            category = "UI/UX",
                            status = "Hoàn thành",
                            resultUrl = "https://www.figma.com"
                        ),
                        TaskEntity(
                            title = "Khởi tạo GitHub Repository",
                            description = "Tạo repository công khai cho đồ án trên GitHub cá nhân, mời các thành viên vào nhóm...",
                            assignee = "Dương",
                            category = "Quản lý / Git",
                            status = "Hoàn thành",
                            resultUrl = "https://github.com/Bkhangnguyen/TaskManagerApp"
                        ),
                        TaskEntity(
                            title = "Thiết lập WordPress Local",
                            description = "Thiết lập môi trường WordPress dùng để phát triển đồ án...",
                            assignee = "Dương",
                            category = "WordPress",
                            status = "Hoàn thành",
                            resultUrl = "http://localhost:8080"
                        )
                    )
                )
            }
        }
    }
}
