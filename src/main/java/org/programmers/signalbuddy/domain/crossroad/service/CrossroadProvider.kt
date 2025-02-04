package org.programmers.signalbuddy.domain.crossroad.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.programmers.signalbuddy.domain.crossroad.dto.CrossroadApiResponse
import org.programmers.signalbuddy.domain.crossroad.dto.CrossroadStateApiResponse
import org.programmers.signalbuddy.domain.crossroad.exception.CrossroadErrorCode
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.util.function.Function

private val logger = KotlinLogging.logger {}

@Component
class CrossroadProvider (

    @Value("\${t-data-api.api-key}")
    val API_KEY: String,

    @Value("\${t-data-api.crossroad-api}")
    val CROSSROAD_API_URL: String,

    @Value("\${t-data-api.traffic-light-api}")
    val SIGNAL_STATE_URL: String,

    val webClient: WebClient

) {

    fun requestCrossroadApi(page: Int, pageSize: Int): List<CrossroadApiResponse>? {

        val paramTypeRef =  object : ParameterizedTypeReference<List<CrossroadApiResponse>>() {};

        return CROSSROAD_API_URL.let {
            webClient.get()
                ?.uri(
                    it
                ) { uriBuilder: UriBuilder ->
                    uriBuilder
                        .queryParam("apiKey", API_KEY)
                        .queryParam("pageNo", page)
                        .queryParam("numOfRows", pageSize)
                        .build()
                }
                ?.retrieve()
                ?.bodyToMono(paramTypeRef)
                ?.onErrorMap( Function<Throwable, Throwable> { e: Throwable ->
                    logger.error{"${e.message} ->  ${e.cause}"}
                    throw BusinessException(CrossroadErrorCode.CROSSROAD_API_REQUEST_FAILED)
                })
                ?.block()
        }
    }

    fun requestCrossroadStateApi(id: Long): List<CrossroadStateApiResponse> {
        val paramTypeRef = object : ParameterizedTypeReference<List<CrossroadStateApiResponse>>() {}

        return SIGNAL_STATE_URL.let {
            webClient.get()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder
                        .queryParam("apiKey", API_KEY)
                        .queryParam("itstId", id)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1)
                        .build()
                }
                .retrieve()
                .bodyToMono(paramTypeRef)
                .switchIfEmpty(Mono.just(emptyList())) // 응답 본문이 없을 경우 빈 리스트 반환
                .onErrorMap { e: Throwable ->
                    logger.error{"${e.message} -> ${e.cause}"}
                    BusinessException(CrossroadErrorCode.CROSSROAD_API_REQUEST_FAILED)
                }
                .block() ?: emptyList()
        }
    }

    /*fun requestCrossroadStateApi(id: Long): List<CrossroadStateApiResponse> {

        val paramTypeRef =  object : ParameterizedTypeReference<List<CrossroadStateApiResponse>>() {};

        return SIGNAL_STATE_URL.let {
            webClient.get()
                .uri(
                    it
                ) { uriBuilder: UriBuilder ->
                    uriBuilder
                        .queryParam("apiKey", API_KEY)
                        .queryParam("itstId", id)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1)
                        .build()
                }
                .retrieve()
                .bodyToMono(paramTypeRef)
                .onErrorMap(Function<Throwable, Throwable> { e: Throwable ->
                    logger.error{"${e.message} ->  ${e.cause}"}
                    throw BusinessException(CrossroadErrorCode.CROSSROAD_API_REQUEST_FAILED)
                })
                .block() ?: emptyList()
        }
    }*/
}
