package dding.msa_api_gateway.dto.My.request;

import dding.msa_api_gateway.config.BandCategory;


import dding.msa_api_gateway.dto.Band.request.BandPositionCountRequest;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BandCreateRequestDto {
    private String bandId;
    private String name;
    private String leaderId;
    private String description;
    private String preferredRegion;     // 선호 지역 (ex: 서울시, 부산시)
    private BandCategory category;      // 카테고리 (팝, 락...)


    private List<BandPositionCountRequest> positions;

}
