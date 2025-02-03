package org.programmers.signalbuddy.domain.bookmark.mapper

import org.locationtech.jts.geom.Point
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import org.programmers.signalbuddy.domain.bookmark.dto.BookmarkRequest
import org.programmers.signalbuddy.domain.bookmark.dto.BookmarkResponse
import org.programmers.signalbuddy.domain.bookmark.entity.Bookmark
import org.programmers.signalbuddy.domain.member.entity.Member

@Mapper
interface BookmarkMapper {

    companion object {
        val INSTANCE: BookmarkMapper = Mappers.getMapper(BookmarkMapper::class.java)
    }

    @Mapping(source = "point", target = "coordinate")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    fun toEntity(bookmarkRequest: BookmarkRequest, point: Point, member: Member): Bookmark

    @Mapping(target = "lng", expression = "java(bookmark.getCoordinate().getX())")
    @Mapping(target = "lat", expression = "java(bookmark.getCoordinate().getY())")
    fun toDto(bookmark: Bookmark): BookmarkResponse
}