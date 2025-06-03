package dding.msa_api_gateway.controller.band;


import dding.msa_api_gateway.clients.Band.BandClient;
import dding.msa_api_gateway.clients.Bandroom.Bnadroom.BandRoomClient;
import dding.msa_api_gateway.clients.Bandroom.studio.StudioClient;
import dding.msa_api_gateway.clients.TimeManager.TimeManagerClient;
import dding.msa_api_gateway.clients.address.AddressClient;
import dding.msa_api_gateway.clients.image.ImageClient;
import dding.msa_api_gateway.clients.reservation.ProductClient;
import dding.msa_api_gateway.clients.user.UserClient;
import dding.msa_api_gateway.dto.Band.request.BandCreateRequest;
import dding.msa_api_gateway.dto.Band.request.BandMemberJoinRequest;
import dding.msa_api_gateway.dto.Band.response.BandMemberResponse;
import dding.msa_api_gateway.dto.Band.response.BandResponse;
import dding.msa_api_gateway.dto.My.reponse.BandResponseDto;
import dding.msa_api_gateway.dto.My.reponse.MemberWithProfileDto;
import dding.msa_api_gateway.dto.My.request.AddressCreateRequestDto;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomCreateRequest;
import dding.msa_api_gateway.dto.bandRoom.request.server.BandRoomCreateRequestDto;
import dding.msa_api_gateway.dto.user.ProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bander/band")
public class BandController {
    private final BandClient bandClient;

    private final ImageClient imageClient;

    private final UserClient userClient;

    public BandController(ImageClient imageClient, BandClient bandClient, UserClient userClient)
    {
        this.bandClient = bandClient;
        this.imageClient = imageClient;
        this.userClient = userClient;

    }




    @PostMapping()
    public Mono<String> createBand(
            @RequestBody BandCreateRequest req
            )
    {
        if(req.getBandId() == null)
        {
            req.setBandId(UUID.randomUUID().toString());
        }

        return bandClient.createBand(req);
    }





    @GetMapping()
    public Mono<BandResponseDto> getBand(@RequestParam String bandId) {
        Mono<BandResponse> bandMono    = bandClient.getBandDetail(bandId);
        Mono<List<String>> imagesMono  = imageClient.getImageUrls(bandId);

        return Mono.zip(bandMono, imagesMono)
                .flatMap(tuple -> {
                    List<String> imgUrls   = tuple.getT2();
                    List<BandMemberResponse> members = tuple.getT1().getMembers();

                    BandResponse b         = tuple.getT1();
                    // ① 모든 멤버를 Flux 로 순회하며 프로필 + 프로필 이미지 URL fetch
                    return Flux.fromIterable(members)
                            .flatMap(member ->
                                    Mono.zip(
                                                    userClient.getProfile(member.getUserId()),
                                                    imageClient.getProfileUrl(member.getUserId())

                                            )
                                            .map(t2 -> {
                                                ProfileResponse profile = t2.getT1();
                                                profile.setProfileImageUrl(t2.getT2());
                                                return MemberWithProfileDto.builder()
                                                        .userId(member.getUserId())
                                                        .position(member.getPosition())
                                                        .description(member.getDescription())
                                                        .confirmedAt(member.getConfirmedAt())
                                                        .profile(profile)
                                                        .build();
                                            })
                            )
                            .collectList()
                            // ② 최종적으로 BandResponseDto 에 멤버 리스트와 이미지 URL 추가
                            .map(memberWithProfiles -> BandResponseDto.builder()
                                    .bandId(bandId)
                                    .name(b.getName())
                                    .description(b.getDescription())
                                    .category(b.getCategory())
                                    .leaderId(b.getLeaderId())
                                    .preferredRegion(b.getPreferredRegion())
                                    .imageUrls(imgUrls)
                                    .members(memberWithProfiles)
                                    .build()
                            );
                });
    }



    /** 밴드 멤버 전체 (컨트롤러에서 프로필+이미지 병합) */
    @GetMapping("/members")
    public Mono<ResponseEntity<Page<MemberWithProfileDto>>> getBandMembers(
            @RequestParam("bandId") String bandId,
            @PageableDefault(size = 10, sort = "joinedAt") Pageable pageable
    ) {
        return bandClient.getBandMembers(bandId, pageable)
                // Page<BandMemberResponse> 를 받아서…
                .flatMap(page -> {
                    Flux<MemberWithProfileDto> enriched = Flux.fromIterable(page.getContent())
                            .flatMap(member ->
                                    Mono.zip(
                                                    userClient.getProfile(member.getUserId()),
                                                    imageClient.getProfileUrl(member.getUserId())
                                                            .onErrorResume(e -> Mono.just((String)null))
                                            )
                                            .map(tuple -> {
                                                ProfileResponse profile = tuple.getT1();
                                                profile.setProfileImageUrl(tuple.getT2());
                                                return MemberWithProfileDto.builder()
                                                        .userId(member.getUserId())
                                                        .position(member.getPosition())
                                                        .description(member.getDescription())
                                                        .confirmedAt(member.getConfirmedAt())
                                                        .profile(profile)
                                                        .build();
                                            })
                            );

                    return enriched
                            .collectList()
                            .map(list -> new PageImpl<>(
                                    list,
                                    pageable,
                                    page.getTotalElements()
                            ));
                })
                .map(ResponseEntity::ok);
    }

    /** 지원자 목록 (컨트롤러에서 프로필+이미지 병합) */
    @GetMapping("/members/req")
    public Mono<ResponseEntity<Page<MemberWithProfileDto>>> getReqBandMembers(
            @RequestParam("bandId") String bandId,
            @PageableDefault(size = 10, sort = "joinedAt") Pageable pageable
    ) {
        return bandClient.getReqBandMembers(bandId, pageable)
                .flatMap(page -> {
                    Flux<MemberWithProfileDto> enriched = Flux.fromIterable(page.getContent())
                            .flatMap(member ->
                                    Mono.zip(
                                                    userClient.getProfile(member.getUserId()),
                                                    imageClient.getProfileUrl(member.getUserId())
                                                            .onErrorResume(e -> Mono.just((String)null))
                                            )
                                            .map(tuple -> {
                                                ProfileResponse profile = tuple.getT1();
                                                profile.setProfileImageUrl(tuple.getT2());
                                                return MemberWithProfileDto.builder()
                                                        .userId(member.getUserId())
                                                        .position(member.getPosition())
                                                        .description(member.getDescription())
                                                        .confirmedAt(member.getConfirmedAt())
                                                        .profile(profile)
                                                        .build();
                                            })
                            );

                    return enriched
                            .collectList()
                            .map(list -> new PageImpl<>(
                                    list,
                                    pageable,
                                    page.getTotalElements()
                            ));
                })
                .map(ResponseEntity::ok);
    }

    @PostMapping("/reqBandMember")
    public Mono<ResponseEntity<String>> joinBand(
            @RequestBody BandMemberJoinRequest req
            ) {
        return bandClient.requestMember(req)
                .map(ResponseEntity::ok);
    }

    /** 멤버 승인 */
    @PutMapping("/members/confirmed/{leaderId}/{userId}")
    public Mono<ResponseEntity<String>> acceptBandMember(
            @PathVariable String leaderId,
            @PathVariable String userId
    ) {
        return bandClient.acceptBandMember(leaderId, userId)
                .map(ResponseEntity::ok);
    }

    /** 멤버 거절 */
    @PutMapping("/members/reject/{leaderId}/{userId}")
    public Mono<ResponseEntity<String>> rejectBandMember(
            @PathVariable String leaderId,
            @PathVariable String userId
    ) {
        return bandClient.rejectBandMember(leaderId, userId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/list")
    public Mono<ResponseEntity<Page<BandResponse>>> getBandList(
            @RequestParam(value = "userId", required = false) String userId,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable
    ) {
        return bandClient.getBandList(userId, pageable)
                .map(ResponseEntity::ok);
    }
}
