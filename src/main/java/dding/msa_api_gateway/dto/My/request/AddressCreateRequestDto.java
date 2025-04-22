package dding.msa_api_gateway.dto.My.request;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class AddressCreateRequestDto {
    String referenceId;
    String addressType; // "USER / BAND_ROOM

    Double latitude;
    Double longitude;
    String roadAddress;
    String displayAddress;
}
