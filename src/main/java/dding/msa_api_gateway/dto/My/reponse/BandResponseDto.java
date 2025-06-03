package dding.msa_api_gateway.dto.My.reponse;

import dding.msa_api_gateway.config.BandCategory;
import dding.msa_api_gateway.dto.Band.response.BandMemberResponse;
import dding.msa_api_gateway.dto.Band.response.BandPositionSlotResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BandResponseDto {
    private String bandId;
    private String name;
    private String leaderId;
    private BandCategory category;
    private Integer count;
    private String description;
    private List<String> imageUrls;

    private List<BandPositionSlotResponse> positions; // 포지션별 인원 수
    private List<MemberWithProfileDto> members; // 가입된 멤버 프로핃
    private String preferredRegion; // 선호 지역 (ex: 서울시, 부산시)
    private Integer currentMemberCount;// 현재 멤버 수




}




