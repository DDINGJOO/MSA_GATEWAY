package dding.msa_api_gateway.dto.Band.response;
import dding.msa_api_gateway.config.BandPosition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BandPositionSlotResponse {

    private BandPosition position;
    private int requiredCount;


}

