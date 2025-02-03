package org.programmers.signalbuddy.domain.bookmark.controller

import org.programmers.signalbuddy.domain.bookmark.service.BookmarkService
import org.programmers.signalbuddy.global.annotation.CurrentUser
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("bookmarks")
class BookmarkWebController(
    private val bookmarkService: BookmarkService,
) {
    @ModelAttribute("user")
    fun currentUser(@CurrentUser user: CustomUser2Member) = user

    @GetMapping("add")
    fun addBookmark(mv: ModelAndView): ModelAndView {
        mv.viewName = "/member/bookmark/add"
        return mv
    }

    @GetMapping("{id}/edit")
    fun editBookmark(mv: ModelAndView, @PathVariable id: Long): ModelAndView {
        mv.viewName = "/member/bookmark/edit"
        val bookmark = bookmarkService.getBookmark(id)
        mv.addObject("bookmark", bookmark)
        return mv
    }
}