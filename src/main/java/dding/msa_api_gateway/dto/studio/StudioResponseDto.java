package dding.msa_api_gateway.dto.studio;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudioResponseDto {
    private String id;
    private String name;
    private String description;
    private Integer defaultPrice;
    private List<String> imageUrls;
    private String pricePoliciesDescription;
    private boolean isAvailable;
}
