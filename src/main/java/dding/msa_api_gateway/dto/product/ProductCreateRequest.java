package dding.msa_api_gateway.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductCreateRequest {
    String productId;
    String name;
    Long price;
    String description;
}
