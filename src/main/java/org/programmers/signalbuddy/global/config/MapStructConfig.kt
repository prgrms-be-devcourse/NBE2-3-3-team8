package org.programmers.signalbuddy.global.config

import org.mapstruct.*
import org.mapstruct.MappingConstants.ComponentModel

@MapperConfig(
    componentModel = ComponentModel.SPRING, // Spring Bean으로 등록
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, // null 값 무시
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, // null이면 기본값 반환
    unmappedTargetPolicy = ReportingPolicy.IGNORE, // 매핑되지 않은 대상 필드 무시
    unmappedSourcePolicy = ReportingPolicy.IGNORE // 매핑되지 않은 소스 필드 무시
)
interface MapStructConfig
