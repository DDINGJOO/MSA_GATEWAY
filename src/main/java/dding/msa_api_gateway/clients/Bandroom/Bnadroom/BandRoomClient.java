package dding.msa_api_gateway.clients.Bandroom.Bnadroom;


import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomSearchRequest;
import dding.msa_api_gateway.dto.bandRoom.request.server.BandRoomCreateRequestDto;

import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
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

    public Mono<Page<BandRoomResponse>> getBandRooms(
            BandRoomSearchRequest req,
            Pageable pageable
    ) {
        return wc.get()
                // baseUrl 이 이미 http://…/api/bander/bandroom 으로 설정되어 있다고 가정
                .uri(uriBuilder -> buildListUri(uriBuilder, req, pageable))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Page<BandRoomResponse>>() {});
    }

    private URI buildListUri(
            UriBuilder uriBuilder,
            BandRoomSearchRequest req,
            Pageable pageable
    ) {
        UriBuilder b = uriBuilder
                .path("/list")
                .queryParam("page",  pageable.getPageNumber())
                .queryParam("size",  pageable.getPageSize());

        // sort 파라미터: 예) sort=name,ASC
        pageable.getSort().forEach(order ->
                b.queryParam("sort", order.getProperty() + "," + order.getDirection())
        );

        // 검색 필터가 있다면
        if (req.getName() != null) {
            b.queryParam("name", req.getName());
        }
        if (req.getCategory() != null) {
            b.queryParam("category", req.getCategory());
        }
        // … 필요에 따라 다른 req 필드도 추가

        return b.build();
    }
}



