package dding.msa_api_gateway.clients.TimeManager;


import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomWeekRequest;
import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import dding.msa_api_gateway.dto.studio.AvailableHourResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
                .uri("/api/time-manager/band-rooms/"+bandRoomId+"/weeks")
                .bodyValue(req)
                .retrieve()
//                .onStatus(HttpStatus::isError, resp ->
//                        resp.bodyToMono(String.class)
//                                .flatMap(body -> Mono.error(new RuntimeException("BFF 에러 " + body)))
//                )
                .bodyToMono(String.class);
    }



    //TODO: 밴드 수정 메소드랑 합쳐서 업데이트마다 동기화 구현 해야함.
    public Mono<String> createStudioRoomWeeks(String studioId, String BandRoomId, List<BandRoomWeekRequest> req)
    {
        return wc.post()
                .uri("/api/time-manager/studios/+" + BandRoomId+"/"+studioId+"/weeks")
                .bodyValue(req)
                .retrieve()
//                .onStatus(HttpStatus::isError, resp ->
//                        resp.bodyToMono(String.class)
//                                .flatMap(body -> Mono.error(new RuntimeException("BFF 에러 " + body)))
//                )
                .bodyToMono(String.class);
    }

    public Mono<Void> upDateStudioWeeks(String studioId, String bandRoomId, List<BandRoomWeekRequest> req) {
        return wc.post()
                .uri("/api/time-manager/studios/" + bandRoomId + "/" + studioId + "/weeks/upDate")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Void.class);
    }



    public Mono<Boolean> isBandRoomOpen(String bandRoomId, LocalDate date, LocalTime time) {
        return wc.get()
                .uri(uriBuilder -> {
                    UriBuilder b = uriBuilder
                            // baseUrl에 /api/time-manager 가 이미 설정되어 있으면
                            .path("/api/time-manager/band-rooms/"+bandRoomId+"/open-check")
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

    public Mono<Boolean> isStudioOpen(String bandRoomId, String studioId , LocalDate date, LocalTime time) {
        return wc.get()
                .uri(uriBuilder -> {
                    UriBuilder b = uriBuilder
                            // baseUrl에 /api/time-manager 가 이미 설정되어 있으면
                            .path("/api/time-manager/studios/"+bandRoomId+"/"+studioId+"/open-check")
                            // 아니면 .path("/api/time-manager/band-rooms/{bandRoomId}/open-check")
                            .queryParam("date", date);
                    if (time != null) {
                        b = b.queryParam("time", time);
                    }
                    return b.build();
                })
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<List<AvailableHourResponse>> getAvailableTimes(String bandRoomId, String studioId , LocalDate date) {
        return wc.get()
                .uri(uriBuilder -> {
                    UriBuilder b = uriBuilder
                            // baseUrl에 /api/time-manager 가 이미 설정되어 있으면
                            .path("/api/time-manager/studios/"+bandRoomId+"/"+studioId+"/available-hours")
                            // 아니면 .path("/api/time-manager/band-rooms/{bandRoomId}/open-check")
                            .queryParam("date", date);
                    return b.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AvailableHourResponse>>() {});
    }
}
