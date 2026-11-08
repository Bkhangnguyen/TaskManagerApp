package com.example.taskmanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmanager.data.local.dao.CategoryDao
import com.example.taskmanager.data.local.dao.MemberDao
import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.dao.TaskResultDao
import com.example.taskmanager.data.local.entity.CategoryEntity
import com.example.taskmanager.data.local.entity.MemberEntity
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.local.entity.TaskResultEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        CategoryEntity::class,
        MemberEntity::class,
        TaskResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun memberDao(): MemberDao
    abstract fun taskResultDao(): TaskResultDao

    companion object {
        // @Volatile đảm bảo giá trị của INSTANCE luôn được cập nhật đồng bộ giữa các luồng (Thread)
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton Pattern với khối synchronized đảm bảo chỉ có duy nhất 1 phiên kết nối Database
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

        // Tự động nạp trước dữ liệu mẫu ban đầu từ bảng Airtable khi khởi tạo DB lần đầu
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

                // 1. Nạp danh mục (Categories)
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

                // 2. Nạp thành viên (3 thành viên: Dương, Khang, Duy)
                memberDao.insertAll(
                    listOf(
                        MemberEntity(name = "Dương", role = "Trưởng nhóm / Task Core & DB", tagColorHex = "#0F766E"),
                        MemberEntity(name = "Khang", role = "Category & Results Module", tagColorHex = "#B45309"),
                        MemberEntity(name = "Duy", role = "Member & Profile Module", tagColorHex = "#4338CA")
                    )
                )

                // 3. Nạp 9 công việc mẫu chuẩn theo bảng Airtable
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
