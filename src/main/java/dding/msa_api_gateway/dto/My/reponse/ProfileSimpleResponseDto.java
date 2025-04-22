package dding.msa_api_gateway.dto.My.reponse;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileSimpleResponseDto {
    private String userId;
    private String nickname;
    private String profileImageUrl;
    private String preferred1;
    private String preferred2;
    private String city;
}
