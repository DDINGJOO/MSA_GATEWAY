package dding.msa_api_gateway.clients.Bandroom.studio;

import dding.msa_api_gateway.dto.address.response.AddressResponse;
import dding.msa_api_gateway.dto.bandRoom.request.server.BandRoomCreateRequestDto;
import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import dding.msa_api_gateway.dto.studio.StudioRequest;
import dding.msa_api_gateway.dto.studio.StudioResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class  StudioClient{
    private final WebClient wc;
    public StudioClient(@Qualifier("bandRoomWebClient") WebClient wc)
    {
        this.wc = wc;
    }


    public Mono<String> createStudio(StudioRequest req, String bandRoomId)
    {
        return wc.post()
                .uri("/api/band-rooms/"+bandRoomId+"studios")
                .bodyValue(req)
                .retrieve()
//                .onStatus(HttpStatus::isError, resp ->
//                        resp.bodyToMono(String.class)
//                                .flatMap(body -> Mono.error(new RuntimeException("BFF 에러 " + body)))
//                )
                .bodyToMono(String.class);
    }

    public Mono<StudioResponse> getStudio(String studioId)
    {
        return wc.get()
                .uri("/api/band-rooms"+"bandRoomId"+studioId)
                .retrieve()
                .bodyToMono(StudioResponse.class);
    }


}
