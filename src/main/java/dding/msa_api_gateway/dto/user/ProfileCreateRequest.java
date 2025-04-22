package dding.msa_api_gateway.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@NoArgsConstructor
@AllArgsConstructor
public class ProfileCreateRequest {
    private String userId;
    private String nickname;
    private String email;
    private String phone;
    private String city;
    private String preferred1;
    private String preferred2;
    private String introduction;
    private Boolean SNS_agree;

}
