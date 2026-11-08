# TaskManagerApp — Ứng Dụng Quản Lý & Phân Công Tác Vụ (TMDT PM)

Dự án Đồ án Môn học Lập trình Ứng dụng Di động (Android Studio - Kotlin).  
Kiến trúc: **MVVM (Model - View - ViewModel) + View Binding + Room Database (Offline-First)**

---

## 📌 Phân Công Kỹ Thuật Theo Chiều Dọc (Vertical Slicing — Nhóm 3 Thành Viên)

| Thành viên | Phân hệ (Module) | Thành phần kỹ thuật phụ trách |
| :--- | :--- | :--- |
| **Dương (Lead)** | **Task Core & Database Singleton** | • Xây dựng `AppDatabase` Singleton (@Volatile + synchronized)<br>• Phân hệ Task: `TaskEntity`, `TaskDao`, `TaskRepository`<br>• `TaskViewModel` (StateFlow & Coroutines)<br>• `TaskListFragment`, `TaskAdapter` (100% ViewBinding) |
| **Khang** | **Category & Results Module** | • Phân hệ Hạng mục: `CategoryEntity`, `CategoryDao`, `CategoryRepository`<br>• Phân hệ Kết quả / Link nộp: `TaskResultEntity`, `TaskResultDao`<br>• `CategoryViewModel`, `CategoryFragment`, `CategoryAdapter`<br>• Hộp thoại thêm công việc `AddTaskDialogFragment` |
| **Duy** | **Member & Profile Module** | • Phân hệ Thành viên: `MemberEntity`, `MemberDao`, `MemberRepository`<br>• `MemberViewModel`, `MemberFragment`, `MemberAdapter`<br>• `ProfileFragment` (Giao diện thông tin cá nhân & cài đặt)<br>• Hệ thống màu sắc & Theme Material 3 chuẩn Airtable |

---

## 🚀 Các Tính Năng Hiện Có (Milestone 1, 2 & 3 Hoàn Thành)
* **100% View Binding:** Loại bỏ hoàn toàn `findViewById()` trong toàn bộ Activity, Fragment và RecyclerView ViewHolder.
* **Kiến trúc MVVM chuẩn mực:** Tách biệt rõ ràng các tầng `ui`, `viewmodel`, `model`, `repository`, `data.local`.
* **Cơ sở dữ liệu Room Database (Offline-First):**
  * Tích hợp 4 Entity & 4 DAO cho các phân hệ tính năng.
  * Các thao tác ghi (Insert/Update/Delete) dùng `suspend fun`.
  * Các thao tác đọc dữ liệu dùng `Flow<List<T>>` tự động phản ứng và cập nhật giao diện khi dữ liệu thay đổi.
  * Kiểm thử mượt mà trên Chế độ máy bay (Airplane Mode).
* **Tìm kiếm & Lọc thời gian thực:** Lọc theo trạng thái (*Tất cả, Cần làm, Đang làm, Cần chỉnh sửa, Hoàn thành*) và tìm kiếm theo từ khóa.
* **Giao diện chuẩn Material 3 & Airtable Style:** Thẻ công việc trực quan, tag màu thành viên và chip trạng thái giống bảng Airtable.

---

## 🛠 Hướng Dẫn Mở Dự Án Trên Android Studio
1. Mở **Android Studio** $\rightarrow$ Chọn **Open**.
2. Chọn thư mục dự án `AppMobie` (hoặc `TaskManagerApp`).
3. Chờ Gradle đồng bộ (Sync Project with Gradle Files).
4. Chọn máy ảo Android (AVD) hoặc cắm thiết bị thật $\rightarrow$ Bấm nút **Run (Shift + F10)**.
