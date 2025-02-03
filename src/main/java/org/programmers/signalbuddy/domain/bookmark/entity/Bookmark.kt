package org.programmers.signalbuddy.domain.bookmark.entity

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import jakarta.persistence.*
import org.locationtech.jts.geom.Point
import org.programmers.signalbuddy.domain.basetime.BaseTimeEntity
import org.programmers.signalbuddy.domain.member.entity.Member

@Entity(name = "bookmarks")
class Bookmark(
    @Column(nullable = false) var coordinate: Point,

    @Column(nullable = false) var address: String,

    var name: String,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(
        name = "member_id",
        nullable = false
    )
    val member: Member? = null,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val bookmarkId: Long? = null

    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?): Boolean =
        kotlinEquals(other = other, properties = equalsAndHashCodeProperties)

    override fun hashCode(): Int = kotlinHashCode(properties = equalsAndHashCodeProperties)

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(Bookmark::bookmarkId)
        private val toStringProperties = arrayOf(
            Bookmark::coordinate,
            Bookmark::address,
            Bookmark::name
        )
    }

    fun update(coordinate: Point, address: String, name: String) {
        this.coordinate = coordinate
        this.address = address
        this.name = name
    }
}