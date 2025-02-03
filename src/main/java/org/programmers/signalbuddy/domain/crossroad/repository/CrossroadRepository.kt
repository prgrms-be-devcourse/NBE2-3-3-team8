package org.programmers.signalbuddy.domain.crossroad.repository

import org.programmers.signalbuddy.domain.crossroad.entity.Crossroad
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CrossroadRepository : JpaRepository<Crossroad?, Long?>, QueryCrossroadRepository
