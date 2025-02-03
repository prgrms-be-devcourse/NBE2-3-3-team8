package org.programmers.signalbuddy.domain.bookmark.dto

data class BookmarkResponse(
    var bookmarkId: Long = 0L,
    var address: String = "",
    var name: String? = "",
    var lng: Double = 0.0,
    var lat: Double = 0.0,
)
