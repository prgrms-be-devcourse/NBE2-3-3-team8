package org.programmers.signalbuddy.domain.admin.controller

import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.programmers.signalbuddy.domain.admin.dto.AdminMemberResponse
import org.programmers.signalbuddy.domain.member.service.AdminService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admins")
class AdminWebController(private val adminService: AdminService) {

    @GetMapping
    fun adminsMain(): ModelAndView {
        return ModelAndView("admin/main")
    }

    @GetMapping("members/list")
    fun getAllMembers(
        @PageableDefault(
            page = 0,
            size = 10,
            sort = ["email"]
        ) pageable: Pageable
    ): ResponseEntity<List<AdminMemberResponse>>? {
        val members = adminService.getAllMembers(pageable)
        return ResponseEntity.ok(members)
    }

    @GetMapping("members-detail/{id}")
    fun getAllMembers(@PathVariable id : Long): ResponseEntity<AdminMemberResponse>? {
        val member = adminService.getMember(id)
        return ResponseEntity.ok(member)
    }
}