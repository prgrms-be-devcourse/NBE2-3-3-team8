package org.programmers.signalbuddy.domain.crossroad.controller

import jakarta.validation.constraints.Min
import lombok.RequiredArgsConstructor
import org.programmers.signalbuddy.domain.crossroad.dto.CrossroadApiResponse
import org.programmers.signalbuddy.domain.crossroad.dto.CrossroadStateApiResponse
import org.programmers.signalbuddy.domain.crossroad.repository.CrossroadRepository
import org.programmers.signalbuddy.domain.crossroad.service.CrossroadService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/crossroads")
@RequiredArgsConstructor
class CrossroadController (
    private var crossroadService: CrossroadService,
    private var crossroadRepository: CrossroadRepository
) {

    @PostMapping("/save")
    fun saveCrossroadDates(
        @RequestParam("page") page: @Min(1) Int,
        @RequestParam("size") pageSize: @Min(10) Int
    ): ResponseEntity<Void> {
        crossroadService.saveCrossroadDates(page, pageSize)
        return ResponseEntity.ok().build<Void>()
    }


    @GetMapping("/marker") // 저장된 DB 데이터를 기반으로 map에 찍을 marker의 데이터를 point로 가져오기
    fun pointToMarker(): ResponseEntity<List<CrossroadApiResponse>> {
        val markers: List<CrossroadApiResponse> = crossroadService.allMarkers()
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(markers)
    }


    @GetMapping("/state/{id}") // id를 기반으로 신호등 데이터 상태 검색
    fun markerToState(@PathVariable("id") id: Long): ResponseEntity<List<CrossroadStateApiResponse>> {

        val headers = HttpHeaders()
        headers.cacheControl = CacheControl.noCache().headerValue

        val stateRes: List<CrossroadStateApiResponse> = crossroadService.checkSignalState(id)
        println(stateRes)

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_JSON)
            .body(stateRes)

    }

    @GetMapping("/around") // 좌표 값을 기반으로 50m이내 신호등 반환
    fun aroundCrossroad(
        @RequestParam lat: Double,
        @RequestParam lng: Double
    ): ResponseEntity<List<CrossroadApiResponse>> {
        val aroundSign: List<CrossroadApiResponse> = crossroadRepository.findNearByCrossroads(lat, lng)

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(aroundSign)
    }
}
