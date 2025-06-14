package dding.msa_api_gateway.dto.bandRoom.request.client;


import dding.msa_api_gateway.dto.My.request.AddressCreateRequestDto;
import dding.msa_api_gateway.dto.bandRoom.request.server.BandRoomCreateRequestDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BandRoomCreateRequest {

    private String id;
    private String name;
    private String shortDescription;
    private String userId;
    private String detailedDescription;

    private String phone;
    private Boolean parkingAvailable;
    private String displayAddress;
    private String parkingDescription;

    private List<String> keywords;
    private List<String> homepageUrls;

    private String notes;

    private Double latitude;
    private Double longitude;

    private String roadAddress;
    private String detailAddress;


    public BandRoomCreateRequestDto toBandRoomCreateDto (BandRoomCreateRequest request)
    {
        return BandRoomCreateRequestDto.builder()
                .id(request.getId())
                .name(request.getName())
                .homepageUrls(request.getHomepageUrls())
                .keywords(request.getKeywords())
                .parkingAvailable(request.getParkingAvailable())
                .parkingDescription(request.getParkingDescription())
                .phone(request.getPhone())
                .userId(request.getUserId())
                .notes(request.getNotes())
                .shortDescription(request.getShortDescription())
                .detailedDescription(request.getDetailedDescription())
                .build();
    }

    public AddressCreateRequestDto toAddressCreateDto(BandRoomCreateRequest request)
    {
        return AddressCreateRequestDto.builder()
                .referenceId(request.getId())
                .addressType("BAND_ROOM")
                .roadAddress(request.getRoadAddress())
                .displayAddress(request.getDisplayAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }
}
