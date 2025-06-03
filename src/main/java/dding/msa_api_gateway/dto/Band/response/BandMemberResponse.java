package dding.msa_api_gateway.dto.Band.response;


import dding.msa_api_gateway.config.BandPosition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BandMemberResponse {

    private String userId;
    private BandPosition position;
    private String description;
    private LocalDateTime confirmedAt;
}



