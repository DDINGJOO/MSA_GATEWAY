package dding.msa_api_gateway.dto.bandRoom.response;


import lombok.*;

import java.util.List;

@Getter
@Builder
public class BandRoomResponse {

    private String id;
    private String name;
    private String shortDescription;
    private String detailedDescription;
    private String phone;
    private Boolean parkingAvailable;
    private List<String> keywords;
    private List<String> homepageUrls;
    private String notes;

}


