package dding.msa_api_gateway.dto.My.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductResponseDto {
    String productId;
    String name;
    Long price;
    String description;
    String thumbnailUrl;
}
