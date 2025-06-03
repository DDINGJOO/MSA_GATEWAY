package dding.msa_api_gateway.dto.Band.request;


import dding.msa_api_gateway.config.BandPosition;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class BandMemberJoinRequest {


    private String userId;

    private String bandId;
    private BandPosition position;

    private String description;//짧은 소개

}
