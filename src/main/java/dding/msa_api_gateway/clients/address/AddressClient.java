package dding.msa_api_gateway.clients.address;

import dding.msa_api_gateway.dto.My.request.AddressCreateRequestDto;
import dding.msa_api_gateway.dto.address.response.AddressResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AddressClient {
    private final WebClient wc;
    public AddressClient(@Qualifier("addressWebClient") WebClient wc) {
        this.wc = wc;
    }
//    public Mono<AddressDto> getById(String id) {
//        return wc.get()
//                .uri("/api/address/{id}", id)
//                .retrieve()
//                .bodyToMono(AddressDto.class);
//    }


    public Mono<String> createAddress(AddressCreateRequestDto req)
    {
        return wc.post()
                .uri("/api/address")
                .bodyValue(req)
                .retrieve()

                .bodyToMono(String.class);
    }


    public Mono<AddressResponse> getAddress(String id)
    {
        return wc.get()
                .uri("/api/address/BAND_ROOM/"+id)
                .retrieve()
                .bodyToMono(AddressResponse.class);
    }
}
