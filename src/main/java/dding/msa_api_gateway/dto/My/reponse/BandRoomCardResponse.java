package dding.msa_api_gateway.dto.My.reponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BandRoomCardResponse {
    private String id;
    private String name;
    private String shortDescription;
    private String roadAddress;
    private String city;
    private String district;
    private String thumbnailUrl;
    private boolean isOpen;
}


