# Milestone 5: Cấu hình ProGuard / R8 tối ưu hiệu năng và bảo mật khi xuất bản APK Release

# Giữ lại các Model & Entity cho Room Database và Gson JSON Serialization
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-keep class com.example.taskmanager.data.local.entity.** { *; }
-keep class com.example.taskmanager.data.remote.dto.** { *; }
-keep class com.example.taskmanager.model.** { *; }

# Giữ lại Retrofit Services
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}

# Firebase & Coroutines
-keepattributes *Annotation*
-dontwarn sun.misc.Unsafe
-dontwarn okio.**
-dontwarn javax.annotation.**
