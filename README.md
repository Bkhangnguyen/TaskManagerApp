# TaskManagerApp — Ứng Dụng Quản Lý & Phân Công Tác Vụ Dự Án (TMDT PM)

Dự án Đồ án Môn học: **Lập trình Ứng dụng Di động (Mobile Application Development)**  
Nền tảng: **Android Studio (Kotlin)**  
Kiến trúc: **Clean Architecture / MVVM + View Binding + Room Database (Offline-First) + Retrofit 2 + Firebase Auth**  
Lưu trữ GitHub: [https://github.com/Bkhangnguyen/TaskManagerApp](https://github.com/Bkhangnguyen/TaskManagerApp)

---

## 📌 1. Phân Công Kỹ Thuật Theo Chiều Dọc (Vertical Slicing — Nhóm 3 Thành Viên)

Dự án được phân chia theo nguyên tắc lát cắt dọc (Vertical Slicing), mỗi thành viên làm chủ trọn vẹn một phân hệ từ giao diện, ViewModel, CSDL cục bộ đến kết nối API mạng:

| Thành viên | Phân hệ (Module) | Toàn bộ thành phần kỹ thuật phụ trách (Milestones 1 – 5) |
| :--- | :--- | :--- |
| **Dương (Lead)** | **Task Core, Database & API** | • Thiết kế `AppDatabase` Singleton (`@Volatile` + `synchronized`)<br>• Phân hệ Task: `TaskEntity`, `TaskDao`, `TaskRepository`<br>• `TaskApiService` (Retrofit 2 @GET, @POST, @PUT, @DELETE)<br>• Quản lý 3 trạng thái giao diện qua Sealed Class `Resource<T>` (Loading, Success, Error)<br>• `TaskListFragment`, `TaskAdapter` (100% ViewBinding) |
| **Khang** | **Category, Results & Firebase** | • Phân hệ Hạng mục: `CategoryEntity`, `CategoryDao`, `CategoryRepository`<br>• Phân hệ Kết quả / Link nộp: `TaskResultEntity`, `TaskResultDao`<br>• `CategoryApiService` (@GET, @POST categories)<br>• Tích hợp **Firebase Authentication** thật (`LoginActivity`, `AuthViewModel`)<br>• `CategoryViewModel`, `CategoryFragment`, `CategoryAdapter`<br>• Hộp thoại thêm công việc `AddTaskDialogFragment` |
| **Duy** | **Member, Security & Image Loading** | • Phân hệ Thành viên: `MemberEntity`, `MemberDao`, `MemberRepository`<br>• `MemberApiService` (@GET, @POST members)<br>• Tích hợp thư viện **Glide** tải ảnh bất đồng bộ tối ưu RAM<br>• Bảo mật lưu trữ bằng **`EncryptedSharedPreferences`** (chuẩn AES-256)<br>• `MemberViewModel`, `MemberFragment`, `MemberAdapter`, `ProfileFragment`<br>• Cấu hình Proguard/R8 xuất bản APK Release |

---

## 👑 2. Hệ Thống Phân Quyền Người Dùng (Role-Based Access Control — RBAC)

Ứng dụng hỗ trợ phân quyền người dùng thông minh, đáp ứng kịch bản chấm điểm thực tế:

| Tính năng | 👑 Quản trị viên (Trưởng nhóm / Admin) | 👤 Thành viên dự án (Member) |
| :--- | :---: | :---: |
| **Xem danh sách công việc & bộ lọc** | ✅ Có | ✅ Có |
| **Tìm kiếm công việc theo từ khóa** | ✅ Có | ✅ Có |
| **Mở đường dẫn kết quả (Figma, GitHub, Docs)** | ✅ Có | ✅ Có |
| **Cập nhật tiến độ task (Đang làm $\rightarrow$ Hoàn thành)** | ✅ Có | ✅ Có |
| **Tạo mới công việc (Nút nổi FAB +)** | ✅ **Có quyền** | ❌ Bị ẩn (Chỉ xem) |
| **Xóa công việc khỏi CSDL** | ✅ **Có quyền** (Bấm vào task để xóa) | ❌ Không có quyền |
| **Huy hiệu hiển thị trên Profile** | `👑 Quản trị viên (Trưởng nhóm / PM)` | `👤 Thành viên dự án (Member)` |

### Tài khoản Demo có sẵn trên màn hình Login:
* **Đăng nhập nhanh quyền Admin:** Bấm nút *"👑 Đăng nhập quyền Admin (Trưởng nhóm)"* (hoặc đăng nhập bằng `admin@taskmanager.com`).
* **Đăng nhập nhanh quyền Member:** Bấm nút *"👤 Đăng nhập quyền Thành viên (Member)"* (hoặc đăng nhập bằng email bất kỳ).
* **Đăng ký tài khoản thật:** Nhập Email & Mật khẩu $\rightarrow$ Bấm *"Tạo tài khoản mới"* (dữ liệu lưu trực tiếp lên Firebase Authentication).

---

## 🚀 3. Tổng Quan 5 Cột Mốc Kỹ Thuật Đã Hoàn Thành (Milestones 1 – 5)

### 📌 Milestone 1: Sơ đồ Điều hướng Màn hình (Sitemap / User Flow)
* **Single-Activity Architecture:** Sử dụng `MainActivity` làm container kết hợp `NavHostFragment` và **Bottom Navigation** 4 tabs: *Công việc*, *Hạng mục*, *Thành viên*, *Cá nhân*.
* Sơ đồ điều hướng User Flow bài bản từ `LoginActivity` $\rightarrow$ `MainActivity` $\rightarrow$ `TaskDetail` / `AddTaskBottomSheet`.
* Đánh giá chuyên sâu: Lựa chọn Bottom Navigation tối ưu thao tác 1 tay và phù hợp quy mô 4 phân hệ cốt lõi.

### 📌 Milestone 2: Kiến trúc MVVM & View Binding
* **100% View Binding:** Bật `viewBinding = true` trong `build.gradle.kts`, loại bỏ 100% các hàm `findViewById()` trong toàn bộ Activity, Fragment và RecyclerView ViewHolder.
* Tách biệt dữ liệu và logic sang tầng `ViewModel` và `Repository`, bọc trong `StateFlow` đảm bảo bảo toàn dữ liệu khi xoay màn hình (Configuration Changes).

### 📌 Milestone 3: CSDL Cục bộ Room Database (Offline-First Architecture)
* Thiết kế 4 Entity và 4 DAO độc lập theo chiều dọc cho từng thành viên: `TaskEntity/Dao`, `CategoryEntity/Dao`, `MemberEntity/Dao`, `TaskResultEntity/Dao`.
* **Database Singleton:** Khởi tạo 01 lớp `AppDatabase` kế thừa `RoomDatabase` áp dụng mẫu Singleton chuẩn (`@Volatile` + `synchronized`).
* **Coroutines & Flow:** Thao tác ghi dữ liệu dùng `suspend fun`, thao tác đọc dùng `Flow<List<T>>` tự động phát tín hiệu cập nhật UI tức thì khi CSDL thay đổi.
* **Kiểm thử Offline Mode:** Tự động nạp trước 9 công việc mẫu từ Airtable qua `DatabaseCallback`, ứng dụng hoạt động trơn tru khi bật **Chế độ máy bay (Airplane Mode)**.

### 📌 Milestone 4: Tích hợp Web Service RESTful API (Retrofit 2 & Coroutines)
* Khai báo quyền `<uses-permission android:name="android.permission.INTERNET" />` và `ACCESS_NETWORK_STATE`.
* Định nghĩa Retrofit Interface với Kotlin Coroutines (`suspend fun`) chạy trên luồng `Dispatchers.IO`.
* Sealed Class **`Resource<T>`** xử lý trọn vẹn 3 trạng thái giao diện:
  * `Loading`: Hiển thị `ProgressBar` quay tròn.
  * `Success`: Đổ dữ liệu vào `RecyclerView` và cache vào Room DB.
  * `Error`: Bắt lỗi khi ngắt mạng hoặc gặp mã lỗi HTTP 4xx, 5xx, hiển thị banner lỗi kèm nút **"Thử lại" (Retry)** và tự động fallback về dữ liệu Offline trong Room DB.

### 📌 Milestone 5: Firebase Authentication, Glide, Tối ưu & Xuất bản APK
* **Firebase Authentication:** Tích hợp file cấu hình thật `google-services.json`, hỗ trợ đăng ký và đăng nhập tài khoản an toàn qua đám mây.
* **Thư viện Glide:** Tải ảnh đại diện và ảnh đính kèm bất đồng bộ, có hiệu ứng `circleCrop()` và placeholder chống giật lag.
* **Bảo mật AES-256:** Áp dụng `EncryptedSharedPreferences` bảo vệ thông tin đăng nhập và phiên làm việc.
* **Cấu hình Proguard/R8:** Cấu hình file `proguard-rules.pro` tối ưu dung lượng và bảo vệ mã nguồn khi xuất bản APK Release.

---

## 🛠 4. Hướng Dẫn Mở Dự Án & Chạy Demo

1. **Mở dự án:**
   * Mở phần mềm **Android Studio** $\rightarrow$ Chọn **Open** $\rightarrow$ Trỏ tới thư mục `AppMobie`.
   * Chờ Gradle đồng bộ trong 1–2 phút (Sync Project with Gradle Files).
2. **Chạy ứng dụng:**
   * Chọn thiết bị máy ảo Android (AVD) hoặc cắm điện thoại thật $\rightarrow$ Bấm nút xanh **Run (Shift + F10)**.
3. **Kịch bản kiểm thử Offline Mode (Chấm điểm Cột mốc 3):**
   * Trên máy ảo AVD, vuốt thanh thông báo từ trên xuống $\rightarrow$ Bật **Airplane Mode (Chế độ máy bay)**.
   * Mở app $\rightarrow$ Bấm nút *"✈️ Duyệt nhanh Offline"*.
   * Thao tác: Lọc công việc, đổi trạng thái task sang *Hoàn thành*, tắt app hoàn toàn rồi mở lại $\rightarrow$ Toàn bộ dữ liệu vẫn được lưu bền vững trong Room SQLite.
4. **Kịch bản xuất file APK Release (Chấm điểm Cột mốc 5):**
   * Vào menu **Build** $\rightarrow$ **Generate Signed Bundle / APK...** $\rightarrow$ Chọn **APK** $\rightarrow$ **Next**.
   * Tạo Keystore chữ ký số mới $\rightarrow$ Chọn variant **release** $\rightarrow$ Bấm **Create**.
   * File cài đặt hoàn chỉnh sẽ nằm tại `app/release/app-release.apk`.
