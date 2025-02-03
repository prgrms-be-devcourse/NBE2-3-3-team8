package org.programmers.signalbuddy.domain.crossroad.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.*


@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
class CrossroadStateApiResponse {
    @JsonProperty("itstId")
    private var crossroadApiId: String? = null

    // e,w,s,n,ne,nw,se,sw 8방위
    // p : 사람, rs: remain second (남은 시간), sn: state name (상태 이름)
    @JsonProperty("ntPdsgRmdrCs")
    private var nprs = 0

    @JsonProperty("etPdsgRmdrCs")
    private var eprs = 0

    @JsonProperty("stPdsgRmdrCs")
    private var sprs = 0

    @JsonProperty("wtPdsgRmdrCs")
    private var wprs = 0

    @JsonProperty("nePdsgRmdrCs")
    private var neprs = 0

    @JsonProperty("nwPdsgRmdrCs")
    private var nwprs = 0

    @JsonProperty("swPdsgRmdrCs")
    private var swprs = 0

    @JsonProperty("sePdsgRmdrCs")
    private var seprs = 0

    @JsonProperty("ntPdsgStatNm")
    private var npsn: SignalState? = null

    @JsonProperty("etPdsgStatNm")
    private var epsn: SignalState? = null

    @JsonProperty("wtPdsgStatNm")
    private var wpsn: SignalState? = null

    @JsonProperty("stPdsgStatNm")
    private var spsn: SignalState? = null

    @JsonProperty("nePdsgStatNm")
    private var nepsn: SignalState? = null

    @JsonProperty("nwPdsgStatNm")
    private var nwpsn: SignalState? = null

    @JsonProperty("sePdsgStatNm")
    private var sepsn: SignalState? = null

    @JsonProperty("swPdsgStatNm")
    private var swpsn: SignalState? = null
}
