# TaskManagerApp — Ứng Dụng Quản Lý & Phân Công Tác Vụ (TMDT PM)

Dự án Đồ án Môn học Lập trình Ứng dụng Di động (Android Studio - Kotlin).  
Kiến trúc: **Clean Architecture / MVVM + View Binding + Room Database (Offline-First) + Retrofit 2 + Firebase Auth**

---

## 📌 Phân Công Kỹ Thuật Theo Chiều Dọc (Vertical Slicing — Nhóm 3 Thành Viên)

| Thành viên | Phân hệ (Module) | Toàn bộ thành phần kỹ thuật phụ trách (Milestones 1 – 5) |
| :--- | :--- | :--- |
| **Dương (Lead)** | **Task Core, Database & API** | • `AppDatabase` Singleton (`@Volatile` + `synchronized`)<br>• Phân hệ Task: `TaskEntity`, `TaskDao`, `TaskRepository`<br>• `TaskApiService` (Retrofit 2 @GET, @POST, @PUT, @DELETE)<br>• Quản lý 3 trạng thái `Resource<T>` (Loading, Success, Error)<br>• `TaskListFragment`, `TaskAdapter` (100% ViewBinding) |
| **Khang** | **Category, Results & Firebase** | • Phân hệ Hạng mục: `CategoryEntity`, `CategoryDao`, `CategoryRepository`<br>• Phân hệ Kết quả / Link nộp: `TaskResultEntity`, `TaskResultDao`<br>• `CategoryApiService` (@GET, @POST categories)<br>• Tích hợp **Firebase Authentication** thật (`LoginActivity`, `AuthViewModel`)<br>• `CategoryViewModel`, `CategoryFragment`, `CategoryAdapter`<br>• Hộp thoại thêm công việc `AddTaskDialogFragment` |
| **Duy** | **Member, Security & Image Loading** | • Phân hệ Thành viên: `MemberEntity`, `MemberDao`, `MemberRepository`<br>• `MemberApiService` (@GET, @POST members)<br>• Tích hợp thư viện **Glide** tải ảnh bất đồng bộ tối ưu RAM<br>• Bảo mật lưu trữ bằng **`EncryptedSharedPreferences`**<br>• `MemberViewModel`, `MemberFragment`, `MemberAdapter`, `ProfileFragment`<br>• Cấu hình Proguard/R8 xuất bản APK Release |

---

## 🚀 Toàn Bộ Tính Năng Đã Hoàn Thành Qua 5 Cột Mốc

### 1. Milestone 1: Điều hướng & Kiến trúc giao diện
* Single-Activity Architecture (`MainActivity`) với `NavHostFragment` và **Bottom Navigation** 4 Tabs trực quan.
* Sơ đồ luồng người dùng (User Flow) hoàn chỉnh từ Auth $\rightarrow$ Danh sách công việc $\rightarrow$ Chi tiết $\rightarrow$ Cài đặt.

### 2. Milestone 2: MVVM & View Binding
* **100% View Binding:** Loại bỏ hoàn toàn `findViewById()` trong toàn bộ Activity, Fragment và RecyclerView ViewHolder.
* Tách biệt logic và dữ liệu vào tầng `ViewModel` và `Repository`, bảo toàn dữ liệu khi xoay màn hình (Configuration Changes).

### 3. Milestone 3: Room Database (Offline-First Architecture)
* 4 Entity và 4 DAO độc lập cho các phân hệ tính năng.
* Khởi tạo **Database Singleton** (`@Volatile` + `synchronized`) với cơ chế tự động nạp dữ liệu mẫu từ bảng Airtable.
* Toàn bộ thao tác ghi dùng `suspend fun`, thao tác đọc dùng `Flow<List<T>>` tự động phát tín hiệu cập nhật UI.
* Kiểm thử trơn tru trên **Chế độ máy bay (Airplane Mode)**.

### 4. Milestone 4: Tích hợp RESTful API với Retrofit 2 & Coroutines
* Đầy đủ quyền `INTERNET` và `ACCESS_NETWORK_STATE`.
* Retrofit 2 + Gson Converter + `Dispatchers.IO` kết nối API online.
* Sealed Class **`Resource<T>`** xử lý trọn vẹn 3 trạng thái:
  * `Loading`: Hiển thị `ProgressBar` quay tròn.
  * `Success`: Đổ dữ liệu vào `RecyclerView` và cache vào Room DB.
  * `Error`: Báo lỗi khi ngắt mạng, có nút **"Thử lại" (Retry)** và tự động fallback về dữ liệu Offline.

### 5. Milestone 5: Firebase Authentication, Glide, Bảo mật & Release
* **Firebase Authentication:** Màn hình Đăng ký / Đăng nhập tài khoản thật bằng Email & Mật khẩu (có nút duyệt nhanh Offline).
* **Thư viện Glide:** Tải ảnh đại diện và hình ảnh mượt mà, chống rò rỉ bộ nhớ (Memory Leak).
* **Bảo mật:** Mã hóa thông tin đăng nhập bằng `EncryptedSharedPreferences` (AES-256).
* **Đóng gói xuất bản:** Cấu hình Proguard/R8 tối ưu dung lượng và bảo vệ mã nguồn.

---

## 🛠 Hướng Dẫn Mở & Build Dự Án Trên Android Studio
1. Mở **Android Studio** $\rightarrow$ Chọn **Open** $\rightarrow$ Trỏ tới thư mục `AppMobie`.
2. Chờ Android Studio tải các thư viện và đồng bộ Gradle (Sync Project).
3. **Chạy ứng dụng:** Chọn máy ảo AVD hoặc thiết bị thật $\rightarrow$ Bấm nút **Run (Shift + F10)**.
4. **Xuất file APK Release (Nộp bài Milestone 5):**
   * Vào menu **Build** $\rightarrow$ **Generate Signed Bundle / APK...**
   * Chọn **APK** $\rightarrow$ Bấm **Next**.
   * Chọn tạo mới **Keystore** (hoặc chọn keystore có sẵn) $\rightarrow$ Điền mật khẩu $\rightarrow$ Bấm **Next**.
   * Chọn build variant **release** $\rightarrow$ Bấm **Create**.
   * File `.apk` release sẽ được sinh ra tại thư mục `app/release/app-release.apk`.
