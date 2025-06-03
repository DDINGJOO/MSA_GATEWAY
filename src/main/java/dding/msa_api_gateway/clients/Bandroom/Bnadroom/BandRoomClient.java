package dding.msa_api_gateway.clients.Bandroom.Bnadroom;


import dding.msa_api_gateway.dto.My.reponse.BandRoomSearchRequestDto;
import dding.msa_api_gateway.dto.bandRoom.request.server.BandRoomCreateRequestDto;

import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;

@Component
public class BandRoomClient {
    private final WebClient wc;
    public BandRoomClient(@Qualifier("bandRoomWebClient") WebClient wc)
    {
        this.wc = wc;
    }


    public Mono<String> createBandRoom(BandRoomCreateRequestDto req)
    {
        System.out.println("call client");
        return wc.post()
                .uri("/api/band-rooms")
                .bodyValue(req)
                .retrieve()
//                .onStatus(HttpStatus::isError, resp ->
//                        resp.bodyToMono(String.class)
//                                .flatMap(body -> Mono.error(new RuntimeException("BFF 에러 " + body)))
//                )
                .bodyToMono(String.class)
                .retry(0);
    }

    public Mono<BandRoomResponse> getBandRoom(String bandRoomId)
    {
        return wc.get()
                .uri("/api/band-rooms/"+bandRoomId)
                .retrieve()
                .bodyToMono(BandRoomResponse.class);
    }
    public Mono<PageImpl<BandRoomResponse>> getBandRooms(
            BandRoomSearchRequestDto req,
            Pageable pageable
    ) {
        return wc.get()
                .uri(uriBuilder -> buildListUri(uriBuilder, req, pageable))
                .retrieve()
                // Page<…> 대신 PageImpl<…> 로 명시
                .bodyToMono(new ParameterizedTypeReference<PageImpl<BandRoomResponse>>() {})
                ;
    }

    private URI buildListUri(
            UriBuilder uriBuilder,
            BandRoomSearchRequestDto req,
            Pageable pageable
    ) {
        UriBuilder b = uriBuilder
                .path("/api/band-rooms/list")
                .queryParam("page",  pageable.getPageNumber())
                .queryParam("size",  pageable.getPageSize());

        // name,ASC 기본 정렬은 Pageable에 이미 설정되어 있다고 가정
        pageable.getSort().forEach(order ->
                b.queryParam("sort", order.getProperty() + "," + order.getDirection())
        );

        if (req.getName() != null) {
            b.queryParam("name", req.getName());
        }
        if (req.getRoadAddress() != null) {
            b.queryParam("address", req.getRoadAddress());
        }

        return b.build();
    }
}
