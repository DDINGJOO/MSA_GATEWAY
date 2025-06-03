package dding.msa_api_gateway.dto.Band.response;


import dding.msa_api_gateway.config.BandCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BandResponse {

    private String id;
    private String name;
    private String leaderId;
    private BandCategory category;
    private Integer count;
    private String description;


    private List<BandPositionSlotResponse> positions; // 포지션별 인원 수
    private List<BandMemberResponse> members; // 가입된 멤버 목록
    private String preferredRegion; // 선호 지역 (ex: 서울시, 부산시)
    private Integer currentMemberCount;// 현재 멤버 수



}

