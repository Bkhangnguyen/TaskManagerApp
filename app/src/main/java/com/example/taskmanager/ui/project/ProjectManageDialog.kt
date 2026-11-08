package com.example.taskmanager.ui.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.taskmanager.TaskManagerApp
import com.example.taskmanager.data.local.entity.ProjectEntity
import com.example.taskmanager.data.security.SecurityPrefManager
import com.example.taskmanager.databinding.DialogProjectManageBinding
import kotlinx.coroutines.launch
import java.util.UUID

class ProjectManageDialog(
    private val onProjectChanged: (String) -> Unit = {}
) : DialogFragment() {

    private var _binding: DialogProjectManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogProjectManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val securityPref = SecurityPrefManager(requireContext())
        val app = requireActivity().application as TaskManagerApp
        val projectDao = app.projectDao
        val userAccountDao = app.userAccountDao
        val userEmail = securityPref.getUserEmail() ?: "user@taskmanager.com"

        binding.tvCurrentActiveProject.text = "Dự án hiện tại: ${securityPref.getCurrentProject()}"

        binding.btnCloseDialog.setOnClickListener {
            dismiss()
        }

        // 1. Tham gia dự án bằng Mã mời (Invite Code)
        binding.btnJoinProject.setOnClickListener {
            val code = binding.etInviteCodeInput.text?.toString()?.trim()?.uppercase().orEmpty()
            if (code.isBlank()) {
                binding.etInviteCodeInput.error = "Vui lòng nhập mã mời"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val project = projectDao.getProjectByInviteCode(code)
                if (project != null) {
                    securityPref.setCurrentProject(project.name)
                    userAccountDao.updateProject(userEmail, project.name)
                    Toast.makeText(requireContext(), "Đã tham gia dự án: ${project.name}!", Toast.LENGTH_SHORT).show()
                    onProjectChanged(project.name)
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Mã mời '$code' không hợp lệ hoặc không tồn tại!", Toast.LENGTH_LONG).show()
                }
            }
        }

        // 2. Tạo dự án cá nhân cho chính mình làm
        binding.btnCreatePersonalProject.setOnClickListener {
            val personalName = "Dự án Cá nhân của ${userEmail.substringBefore("@")}"
            val inviteCode = "ME-" + UUID.randomUUID().toString().substring(0, 4).uppercase()

            lifecycleScope.launch {
                val newProject = ProjectEntity(
                    name = personalName,
                    description = "Không gian riêng để tự quản lý công việc của chính bạn",
                    inviteCode = inviteCode,
                    ownerEmail = userEmail,
                    isPersonal = true
                )
                projectDao.insertProject(newProject)
                securityPref.setCurrentProject(personalName)
                userAccountDao.updateProject(userEmail, personalName)
                Toast.makeText(requireContext(), "Đã tạo không gian riêng thành công!", Toast.LENGTH_SHORT).show()
                onProjectChanged(personalName)
                dismiss()
            }
        }

        // 3. Tạo nhóm dự án mới (Sinh mã mời ngẫu nhiên)
        binding.btnCreateNewGroupProject.setOnClickListener {
            val groupName = binding.etNewProjectName.text?.toString()?.trim().orEmpty()
            if (groupName.isBlank()) {
                binding.etNewProjectName.error = "Vui lòng nhập tên nhóm dự án"
                return@setOnClickListener
            }

            val randomCode = "GRP-" + UUID.randomUUID().toString().substring(0, 5).uppercase()

            lifecycleScope.launch {
                val newProject = ProjectEntity(
                    name = groupName,
                    description = "Nhóm dự án do $userEmail tạo",
                    inviteCode = randomCode,
                    ownerEmail = userEmail,
                    isPersonal = false
                )
                projectDao.insertProject(newProject)
                securityPref.setCurrentProject(groupName)
                userAccountDao.updateProject(userEmail, groupName)
                Toast.makeText(requireContext(), "Tạo nhóm thành công! Mã mời của bạn là: $randomCode", Toast.LENGTH_LONG).show()
                onProjectChanged(groupName)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
