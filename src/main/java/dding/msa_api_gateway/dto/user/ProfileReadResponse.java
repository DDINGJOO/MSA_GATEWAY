package dding.msa_api_gateway.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileReadResponse {
    private String nickname;
    private String city;

    private String preferred1;
    private String profileImageUrl;
    private String preferred2;
    private String introduction;
}

