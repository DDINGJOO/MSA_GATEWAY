package dding.msa_api_gateway.clients.TimeManager;


import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomWeekRequest;
import dding.msa_api_gateway.dto.studio.StudioRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class TimeManagerClient {
    private final WebClient wc;
    public TimeManagerClient(@Qualifier("timemanagerWebClient") WebClient wc)
    {
        this.wc = wc;
    }

    public Mono<String> createBandRoomWeeks(String bandRoomId, List<BandRoomWeekRequest> req)
    {
        return wc.post()
                .uri("/api/time-manager/band-rooms/"+bandRoomId+"weeks")
                .bodyValue(req)
                .retrieve()
//                .onStatus(HttpStatus::isError, resp ->
//                        resp.bodyToMono(String.class)
//                                .flatMap(body -> Mono.error(new RuntimeException("BFF 에러 " + body)))
//                )
                .bodyToMono(String.class);
    }

    public Mono<Boolean> isOpen(String bandRoomId, LocalDate date, LocalTime time) {
        return wc.get()
                .uri(uriBuilder -> {
                    UriBuilder b = uriBuilder
                            // baseUrl에 /api/time-manager 가 이미 설정되어 있으면
                            .path("/band-rooms/{bandRoomId}/open-check")
                            // 아니면 .path("/api/time-manager/band-rooms/{bandRoomId}/open-check")
                            .queryParam("date", date);
                    if (time != null) {
                        b = b.queryParam("time", time);
                    }
                    return b.build(bandRoomId);
                })
                .retrieve()
                .bodyToMono(Boolean.class);
    }



}
