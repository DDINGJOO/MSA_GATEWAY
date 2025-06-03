package dding.msa_api_gateway.dto.My.reponse;

import lombok.*;

import java.util.List;




import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BandRoomSearchRequestDto {
    private String name;
    private String keyword;
    private Boolean isOpen;
    private String roadAddress;
}
