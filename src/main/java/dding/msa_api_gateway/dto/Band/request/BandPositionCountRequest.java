package dding.msa_api_gateway.dto.Band.request;

import dding.msa_api_gateway.config.BandPosition;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BandPositionCountRequest {

    private BandPosition position;
    private int count;
}
