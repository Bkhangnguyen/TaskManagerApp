# TaskManagerApp — Ứng Dụng Quản Lý & Phân Công Tác Vụ (TMDT PM)

Dự án Đồ án Môn học Lập trình Ứng dụng Di động (Android Studio - Kotlin).  
Kiến trúc: **MVVM (Model - View - ViewModel) + View Binding + Room Database (Offline-First)**

---

## 📌 Phân Công Kỹ Thuật Theo Chiều Dọc (Vertical Slicing)

| Thành viên | Phân hệ (Module) | Thành phần phụ trách |
| :--- | :--- | :--- |
| **Dương (Lead)** | **Task Core & System** | `TaskViewModel`, `TaskListFragment`, `TaskAdapter`, Base Navigation, `AppDatabase` Singleton |
| **Khôi** | **Category Module** | `CategoryViewModel`, `CategoryFragment`, `CategoryAdapter`, `CategoryRepository` |
| **Kiệt** | **Member Module** | `MemberViewModel`, `MemberFragment`, `MemberAdapter`, `MemberRepository` |
| **Khoa** | **Detail & Task Results** | `AddTaskDialogFragment`, `ProfileFragment`, Theme & Color System |

---

## 🚀 Các Tính Năng Hiện Có (Milestone 2 Hoàn Thành)
* **100% View Binding:** Loại bỏ hoàn toàn `findViewById()`.
* **Kiến trúc MVVM chuẩn mực:** Tách biệt rõ ràng các tầng `ui`, `viewmodel`, `model`, `repository`, `adapter`.
* **StateFlow & Coroutines:** Dữ liệu phản ứng tự động (Reactive Flow), giữ nguyên trạng thái khi xoay màn hình (Configuration Changes).
* **Tìm kiếm & Lọc thời gian thực:** Lọc theo trạng thái (*Tất cả, Cần làm, Đang làm, Cần chỉnh sửa, Hoàn thành*) và tìm kiếm theo từ khóa.
* **Giao diện chuẩn Material 3 & Airtable Style:** Màu sắc, thẻ công việc và chip trạng thái mô phỏng trực quan theo bảng phân công Airtable.

---

## 🛠 Hướng Dẫn Mở Dự Án Trên Android Studio
1. Mở **Android Studio** $\rightarrow$ Chọn **Open**.
2. Chọn thư mục dự án `AppMobie` (hoặc `TaskManagerApp`).
3. Chờ Gradle đồng bộ (Sync Project with Gradle Files).
4. Chọn máy ảo Android (AVD) hoặc cắm thiết bị thật $\rightarrow$ Bấm nút **Run (Shift + F10)**.
