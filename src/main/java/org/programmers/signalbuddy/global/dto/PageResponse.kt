package org.programmers.signalbuddy.global.dto

import org.springframework.data.domain.Page

data class PageResponse<T>(val page: Page<T>) {
    val totalElements = page.totalElements
    val totalPages = page.totalPages.toLong()
    val currentPageNumber = page.number.toLong()
    val pageSize = page.size.toLong()
    val hasNext = page.hasNext()
    val hasPrevious = page.hasPrevious()
    val searchResults: List<T> = page.content
}
