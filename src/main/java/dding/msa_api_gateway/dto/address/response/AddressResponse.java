package dding.msa_api_gateway.dto.address.response;


import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private Long id;
    private String addressType;
    private String referenceId;

    private String city;
    private String district;
    private String category;
    private String neighborhood;
    private String roadAddress;
    private String legalDongCode;

    private Double latitude;
    private Double longitude;
}
