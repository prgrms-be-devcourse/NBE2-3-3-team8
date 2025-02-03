package org.programmers.signalbuddy.domain.crossroad.service

import lombok.RequiredArgsConstructor
import io.github.oshai.kotlinlogging.KotlinLogging
import org.programmers.signalbuddy.domain.crossroad.dto.CrossroadApiResponse
import org.programmers.signalbuddy.domain.crossroad.dto.CrossroadStateApiResponse
import org.programmers.signalbuddy.domain.crossroad.entity.Crossroad
import org.programmers.signalbuddy.domain.crossroad.exception.CrossroadErrorCode
import org.programmers.signalbuddy.domain.crossroad.repository.CrossroadRepository
import org.programmers.signalbuddy.global.exception.BusinessException
import org.programmers.signalbuddy.global.monitoring.HttpRequestManager
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class CrossroadService (
    private var crossroadRepository: CrossroadRepository,
    private var crossroadProvider: CrossroadProvider,
    private var httpRequestManager: HttpRequestManager
){

    // TODO: 시간 남으면 Spring Batch로 동작시키기
    @Transactional
    fun saveCrossroadDates(page: Int, pageSize: Int) {
        val responseList = crossroadProvider.requestCrossroadApi(
            page,
            pageSize
        )

        val entityList: MutableList<Crossroad> = ArrayList()
        for (response in responseList!!) {
            if (response.lng.equals(null) && response.lat.equals(null)) {
                entityList.add(Crossroad(response))
            }
        }

        try {
            crossroadRepository.saveAll(entityList)
        } catch (e: DataIntegrityViolationException) {
            throw BusinessException(CrossroadErrorCode.ALREADY_EXIST_CROSSROAD)
        }
    }

    fun checkSignalState(id: Long): List<CrossroadStateApiResponse>? { // id값으로 신호등의 상태를 검색

        httpRequestManager.increase(id)
        return crossroadProvider.requestCrossroadStateApi(id)
    }

    fun allMarkers(): List<CrossroadApiResponse> {

        val crossroads = crossroadRepository.findAll()
        val responseList: MutableList<CrossroadApiResponse> = ArrayList()

        for (crossroad in crossroads.filterNotNull()) {
            responseList.add(CrossroadApiResponse(crossroad))
        }

        return responseList

    }
}
