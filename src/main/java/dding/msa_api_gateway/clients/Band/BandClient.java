package dding.msa_api_gateway.clients.Band;

import dding.msa_api_gateway.dto.Band.request.BandCreateRequest;
import dding.msa_api_gateway.dto.Band.request.BandMemberJoinRequest;
import dding.msa_api_gateway.dto.Band.response.BandMemberResponse;
import dding.msa_api_gateway.dto.Band.response.BandResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class BandClient {

    private final WebClient wc;

    public BandClient(@Qualifier("bandWebClient") WebClient wc) {
        this.wc = wc;
    }

    /** 1) 밴드 생성 */
    public Mono<String> createBand(BandCreateRequest req) {
        return wc.post()
                .uri("/api/band")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(String.class);
    }

    /** 2) 전체 밴드 리스트 (userId 옵션 포함) */
    public Mono<Page<BandResponse>> getBandList(String userId, Pageable pageable) {
        return wc.get()
                .uri(uriBuilder -> buildListUri(uriBuilder, userId, pageable))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Page<BandResponse>>() {});
    }

    private URI buildListUri(UriBuilder uriBuilder, String userId, Pageable pageable) {
        UriBuilder b = uriBuilder
                .path("/api/band/list")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        if (pageable.getSort().isEmpty()) {
            b.queryParam("sort", "createdAt,desc");
        } else {
            pageable.getSort().forEach(o ->
                    b.queryParam("sort", o.getProperty() + "," + o.getDirection())
            );
        }

        if (userId != null) {
            b.queryParam("userId", userId);
        }
        return b.build();
    }

    /** 3) 밴드 상세 조회 */
    public Mono<BandResponse> getBandDetail(String bandId) {
        return wc.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/band")
                        .queryParam("bandId", bandId)
                        .build())
                .retrieve()
                .bodyToMono(BandResponse.class);
    }

    /** 4) 밴드 삭제 */
    public Mono<String> deleteBand(String bandId) {
        return wc.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/band")
                        .queryParam("bandId", bandId)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    /** 5) 밴드 멤버 삭제(탈퇴) */
    public Mono<String> deleteBandMember(String bandId, String userId) {
        return wc.delete()
                .uri("/api/band/{bandId}/{userId}", bandId, userId)
                .retrieve()
                .bodyToMono(String.class);
    }

    /** 6) 밴드 멤버 목록 */
    public Mono<Page<BandMemberResponse>> getBandMembers(String bandId, Pageable pageable) {
        return wc.get()
                .uri(uriBuilder -> buildMembersUri(uriBuilder, "/api/band/members", bandId, pageable))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Page<BandMemberResponse>>() {});
    }

    /** 7) 지원자 목록 */
    public Mono<Page<BandMemberResponse>> getReqBandMembers(String bandId, Pageable pageable) {
        return wc.get()
                .uri(uriBuilder -> buildMembersUri(uriBuilder, "/api/band/members/req", bandId, pageable))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Page<BandMemberResponse>>() {});
    }

    private URI buildMembersUri(UriBuilder uriBuilder, String path, String bandId, Pageable pageable) {
        UriBuilder b = uriBuilder
                .path(path)
                .queryParam("bandId", bandId)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());
        pageable.getSort().forEach(o ->
                b.queryParam("sort", o.getProperty() + "," + o.getDirection()));
        return b.build();
    }

    /** 8) 멤버 승인 */
    public Mono<String> acceptBandMember(String leaderId, String userId) {
        return wc.put()
                .uri("/api/band/confirmed/{leaderId}/{userId}", leaderId, userId)
                .retrieve()
                .bodyToMono(String.class);
    }

    /** 9) 멤버 거절 */
    public Mono<String> rejectBandMember(String leaderId, String userId) {
        return wc.put()
                .uri("/api/band/reject/{leaderId}/{userId}", leaderId, userId)
                .retrieve()
                .bodyToMono(String.class);
    }

    /** 10) 밴드 지원 요청 */
    public Mono<String> requestMember(BandMemberJoinRequest req) {
        return wc.post()
                .uri("/api/band/reqBandMember")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(String.class);
    }
}
