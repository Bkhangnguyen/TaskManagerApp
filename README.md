# TaskManagerApp — Ứng Dụng Quản Lý & Phân Công Tác Vụ Dự Án (TMDT PM)

Dự án Đồ án Môn học: **Lập trình Ứng dụng Di động (Mobile Application Development)**  
Nền tảng: **Android Studio (Kotlin)**  
Kiến trúc: **Clean Architecture / MVVM + View Binding + Room Database (Offline-First) + Retrofit 2 + Firebase Auth**  
Lưu trữ GitHub: [https://github.com/Bkhangnguyen/TaskManagerApp](https://github.com/Bkhangnguyen/TaskManagerApp)

---

## 📌 1. Phân Công Kỹ Thuật Theo Chiều Dọc (Vertical Slicing — Nhóm 3 Thành Viên)

| Thành viên | Phân hệ (Module) | Toàn bộ thành phần kỹ thuật phụ trách (Milestones 1 – 5) |
| :--- | :--- | :--- |
| **Dương (Lead)** | **Task Core, Database & API** | • Thiết kế `AppDatabase` Singleton (`@Volatile` + `synchronized`)<br>• Phân hệ Task: `TaskEntity`, `TaskDao`, `TaskRepository`<br>• `TaskApiService` (Retrofit 2 @GET, @POST, @PUT, @DELETE)<br>• Quản lý 3 trạng thái giao diện qua Sealed Class `Resource<T>` (Loading, Success, Error)<br>• `TaskListFragment`, `TaskAdapter` (100% ViewBinding) |
| **Khang** | **Category, Results & Firebase** | • Phân hệ Hạng mục: `CategoryEntity`, `CategoryDao`, `CategoryRepository`<br>• Phân hệ Kết quả / Link nộp: `TaskResultEntity`, `TaskResultDao`<br>• `CategoryApiService` (@GET, @POST categories)<br>• Tích hợp **Firebase Authentication** thật (`LoginActivity`, `RegisterActivity`, `AuthViewModel`)<br>• `CategoryViewModel`, `CategoryFragment`, `CategoryAdapter`<br>• Hộp thoại thêm công việc `AddTaskDialogFragment` |
| **Duy** | **Member, Security, Project & Dark Mode** | • Phân hệ Thành viên: `MemberEntity`, `MemberDao`, `MemberRepository`<br>• `MemberApiService` (@GET, @POST members)<br>• Quản trị tài khoản & **Xuất file CSV** (`AdminAccountsActivity`)<br>• Quản lý nhóm dự án & mã mời (`ProjectManageDialog`, `ProjectEntity`)<br>• Giao diện tối **Dark Mode** & Thư viện **Glide**<br>• Bảo mật lưu trữ bằng **`EncryptedSharedPreferences`** (chuẩn AES-256) |

---

## 🌟 2. Các Tính Năng Đột Phá Mới Được Bổ Sung

### A. Quy Trình Tham Gia Dự Án Bằng Mã Mời & Tự Tạo Dự Án Cá Nhân
* **Thành viên mới đăng ký:** Mặc định chưa được add vào dự án nào và chưa thấy các thông báo nội bộ.
* **🔑 Nhập mã mời (Invite Code):** Thành viên nhập mã mời do nhóm trưởng cấp (Mã dự án mẫu: `TMDT2026`) để tham gia nhóm và lập tức thấy toàn bộ danh sách công việc.
* **👤 Tạo dự án cho chính mình làm (Personal Project):** Bấm 1 chạm để tạo không gian quản lý công việc riêng cho cá nhân mà không cần ai mời.
* **👥 Tạo nhóm dự án mới:** Cho phép tự đặt tên nhóm, hệ thống tự động sinh **Mã mời ngẫu nhiên (Ví dụ: `GRP-7892`)** để gửi cho đồng đội cùng tham gia.

### B. Tính Năng Quản Trị Viên (Admin Tools) & Xuất File CSV
* **Xem toàn bộ danh sách tài khoản:** Thấy danh sách tất cả các tài khoản đã đăng ký và đăng nhập, ngày đăng ký, lần online cuối và dự án đang tham gia.
* **📊 Xuất danh sách tài khoản ra file CSV:**
  * Xuất file `danh_sach_tai_khoan.csv` chuẩn mã hóa `UTF-8 BOM` (mở trên Microsoft Excel không bao giờ bị lỗi font tiếng Việt).
  * Hỗ trợ chia sẻ hoặc lưu file trực tiếp qua Google Drive, Zalo, Gmail, Excel hoặc File Manager.

### C. Đăng Ký Đám Mây & Đăng Nhập Đa Thiết Bị (Multi-Device Cloud)
* Sử dụng **Firebase Authentication** lưu trữ tài khoản vĩnh viễn trên Cloud của Google.
* **Không sợ mất tài khoản:** Dù người dùng xóa app, cài lại máy hay đăng nhập từ một chiếc điện thoại khác, tài khoản đã đăng ký vẫn được bảo toàn nguyên vẹn và đăng nhập bình thường!

### D. Giao Diện Tối (Dark Mode)
* Tích hợp công tắc chuyển đổi **Chế độ giao diện Tối (Dark Mode)** ngay trong mục Cá nhân (Profile).
* Bảng màu Dark Theme dịu mắt, tiết kiệm pin cho màn hình OLED/AMOLED và lưu trạng thái người dùng.

---

## 👑 3. Hệ Thống Phân Quyền Người Dùng (Role-Based Access Control — RBAC)

| Tính năng | 👑 Quản trị viên (Admin / Lead) | 👤 Thành viên dự án (Member) |
| :--- | :---: | :---: |
| **Xem công việc & tìm kiếm thời gian thực** | ✅ Có | ✅ Có |
| **Cập nhật tiến độ task (Đang làm $\rightarrow$ Hoàn thành)** | ✅ Có | ✅ Có |
| **Tạo task mới (Nút nổi FAB +)** | ✅ Có | ❌ Bị ẩn |
| **Xóa task khỏi CSDL** | ✅ Có | ❌ Không có quyền |
| **Quản trị tài khoản & Xuất file CSV** | ✅ **Toàn quyền** | ❌ Bị ẩn |
| **Tạo nhóm & Cấp mã mời** | ✅ Có | ✅ Có |
| **Chế độ giao diện Tối (Dark Mode)** | ✅ Có | ✅ Có |

---

## 🚀 4. Tổng Quan 5 Cột Mốc Kỹ Thuật (Milestones 1 – 5)

* **Milestone 1:** Sơ đồ điều hướng Sitemap & User Flow, cấu trúc Single-Activity + Fragments + Bottom Navigation.
* **Milestone 2:** Kiến trúc MVVM chuẩn mực, bật `viewBinding = true`, loại bỏ 100% `findViewById()`, bọc dữ liệu trong `StateFlow`.
* **Milestone 3:** Room Database Cục bộ (Offline-First), Singleton `@Volatile` + `synchronized`, 4 Entities & 4 DAOs, `suspend fun` & `Flow`, kiểm thử trơn tru trên **Chế độ máy bay (Airplane Mode)**.
* **Milestone 4:** Tích hợp Web Service RESTful API với **Retrofit 2** trên `Dispatchers.IO`, Sealed Class **`Resource<T>`** xử lý 3 trạng thái `Loading`, `Success`, `Error` kèm cơ chế tự động Fallback Offline.
* **Milestone 5:** Firebase Authentication thật (đăng ký, đăng nhập), thư viện **Glide** tải ảnh bất đồng bộ, mã hóa **`EncryptedSharedPreferences`** (AES-256), và cấu hình Proguard/R8 xuất bản APK Release.

---

## 🛠 5. Hướng Dẫn Chạy & Demo Đồ Án

1. **Mở dự án:** Mở Android Studio $\rightarrow$ Chọn **Open** $\rightarrow$ Trỏ vào thư mục `AppMobie`.
2. **Chạy ứng dụng:** Chọn máy ảo AVD $\rightarrow$ Bấm nút xanh **Run (Shift + F10)**.
3. **Các tài khoản Demo nhanh trên màn hình Login:**
   * Bấm **"👑 Đăng nhập quyền Admin"** để thử nghiệm vai trò Trưởng nhóm và tính năng Xuất CSV.
   * Bấm **"👤 Đăng nhập quyền Thành viên"** để xem giao diện thành viên thường.
   * Bấm **"Tạo tài khoản mới"** để đăng ký tài khoản thật với Firebase Authentication.
4. **Kiểm tra mã mời:** Vào mục *Cá nhân (Profile)* $\rightarrow$ Bấm *Đổi Dự Án / Nhập Mã Mời* $\rightarrow$ Thử nhập mã `TMDT2026` hoặc tự tạo dự án cá nhân!
