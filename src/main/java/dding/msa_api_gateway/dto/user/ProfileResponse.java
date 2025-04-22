package dding.msa_api_gateway.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private String userId;
    private String nickname;
    private String email;
    private String phone;
    private String city;
    private String profileImageUrl;
    private String preferred1;
    private String preferred2;
    private String introduction;
    private Integer totalPoint;
    private Integer userLevel;
}
