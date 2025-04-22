package dding.msa_api_gateway.dto.My.reponse;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class BandRoomDetailResponseDto {
    private String id;
    private String name;
    private String shortDescription;
    private String detailedDescription;

    private String phone;
    private Boolean parkingAvailable;


    private List<String> keywords;
    private List<String> homepageUrls;

    private String notes;

    private String thumbnailUrl;
    private List<String> images;
    private List<ProductResponseDto> productResponseDtos;
    private List<StudioResponseDto> studioResponseDtos;
    private String city;
    private String district;
    private String category;
    private String neighborhood;
    private String roadAddress;
    private String legalDongCode;
    private Double latitude;
    private Double longitude;
}
