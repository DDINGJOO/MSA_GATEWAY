package dding.msa_api_gateway.dto.bandRoom.request.server;

import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Service
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class BandRoomCreateRequestDto {
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

    private String thumbnailUrl;

    private String roadAddress;
    private String detailAddress;

}
