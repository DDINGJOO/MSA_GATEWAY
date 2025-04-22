package dding.msa_api_gateway.clients.Bandroom.studio;

import dding.msa_api_gateway.dto.studio.StudioRequest;
import dding.msa_api_gateway.dto.studio.StudioResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
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


    public Mono<String> createStudio(StudioRequest req)
    {
        System.out.println(":ca");
        return wc.post()
                .uri("/api/band-rooms/studios")
                .bodyValue(req)
                .retrieve()

                .bodyToMono(String.class);
    }

    public Mono<StudioResponse> getStudio(String studioId)
    {
        return wc.get()
                .uri("/api/band-rooms/studios/"+studioId)
                .retrieve()
                .bodyToMono(StudioResponse.class);
    }


    public Mono<List<StudioResponse>> getStudios( String bandRoomId)
    {
        return wc.get()
                .uri("/api/band-rooms/studios/readAll/"+bandRoomId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<StudioResponse>>() {});
    }


}
