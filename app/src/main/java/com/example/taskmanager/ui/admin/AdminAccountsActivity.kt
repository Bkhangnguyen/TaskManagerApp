package com.example.taskmanager.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.TaskManagerApp
import com.example.taskmanager.data.local.entity.UserAccountEntity
import com.example.taskmanager.databinding.ActivityAdminAccountsBinding
import com.example.taskmanager.databinding.ItemUserAccountBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminAccountsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAccountsBinding
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private var accountList: List<UserAccountEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdmin.setNavigationOnClickListener {
            finish()
        }

        binding.rvUserAccounts.layoutManager = LinearLayoutManager(this)

        val userAccountDao = (application as TaskManagerApp).userAccountDao

        lifecycleScope.launch {
            userAccountDao.getAllAccounts().collect { list ->
                accountList = list
                binding.tvUserCountStats.text = "Tổng cộng: ${list.size} tài khoản"
                binding.rvUserAccounts.adapter = AccountAdapter(list, dateFormat)
            }
        }

        // Xử lý Xuất file CSV
        binding.btnExportCsv.setOnClickListener {
            exportAccountsToCsv()
        }
    }

    private fun exportAccountsToCsv() {
        if (accountList.isEmpty()) {
            Toast.makeText(this, "Không có tài khoản nào để xuất", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val fileName = "danh_sach_tai_khoan_${System.currentTimeMillis()}.csv"
            val file = File(cacheDir, fileName)
            val writer = FileWriter(file)

            // UTF-8 BOM để mở tiếng Việt trên Microsoft Excel không bị lỗi font
            writer.write("\uFEFF")
            // Dòng tiêu đề CSV
            writer.write("STT,Họ và Tên,Email,Vai Trò,Ngày Đăng Ký,Lần Đăng Nhập Cuối,Dự Án Đang Tham Gia\n")

            accountList.forEachIndexed { index, user ->
                val regDate = dateFormat.format(Date(user.registeredAt))
                val loginDate = dateFormat.format(Date(user.lastLoginAt))
                val row = "${index + 1},\"${user.fullName}\",\"${user.email}\",\"${user.role}\",\"$regDate\",\"$loginDate\",\"${user.currentProjectName}\"\n"
                writer.write(row)
            }
            writer.flush()
            writer.close()

            // Chia sẻ hoặc mở file CSV
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Danh sách tài khoản TaskManager")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ hoặc lưu file CSV qua:"))
            Toast.makeText(this, "Đã xuất file CSV thành công!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi khi xuất CSV: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private class AccountAdapter(
        private val items: List<UserAccountEntity>,
        private val dateFormat: SimpleDateFormat
    ) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

        class ViewHolder(val binding: ItemUserAccountBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemUserAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.binding.tvUserFullName.text = item.fullName
            holder.binding.tvUserEmail.text = item.email
            holder.binding.tvUserRoleTag.text = item.role
            holder.binding.tvUserProject.text = "Dự án: ${item.currentProjectName}"
            holder.binding.tvRegisteredDate.text = "Đăng ký: ${dateFormat.format(Date(item.registeredAt))}"
            holder.binding.tvLastLoginDate.text = "Đăng nhập: ${dateFormat.format(Date(item.lastLoginAt))}"
        }

        override fun getItemCount(): Int = items.size
    }
}
