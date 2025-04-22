package dding.msa_api_gateway.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProfileSimpleResponse {
    private String userId;
    private String nickname;
    private String preferred1;
    private String preferred2;
    private String profileImageUrl;
    private String city;
}
