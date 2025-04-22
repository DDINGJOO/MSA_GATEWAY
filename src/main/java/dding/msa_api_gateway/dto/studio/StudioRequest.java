package dding.msa_api_gateway.dto.studio;

import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudioRequest {
    private String id;
    private String name;
    private String userId;
    private String description;
    private Integer defaultPrice;
    private String pricePoliciesDescription;
    private boolean isAvailable;
    private String bandRoomId;
    private String studioId;
}
