package dding.msa_api_gateway.controller.User;

import dding.msa_api_gateway.clients.image.ImageClient;
import dding.msa_api_gateway.clients.user.UserClient;
import dding.msa_api_gateway.dto.My.reponse.ProductResponseDto;
import dding.msa_api_gateway.dto.My.reponse.ProfileSimpleResponseDto;
import dding.msa_api_gateway.dto.My.reponse.StudioResponseDto;
import dding.msa_api_gateway.dto.address.response.AddressResponse;
import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import dding.msa_api_gateway.dto.product.ProductCreateRequest;
import dding.msa_api_gateway.dto.user.ProfileResponse;
import dding.msa_api_gateway.dto.user.ProfileSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/api/profiles")
public class UserController {

    private final ImageClient imageClient;
    private final UserClient userClient;

    public UserController(UserClient userClient , ImageClient imageClient)
    {

        this.imageClient = imageClient;
        this.userClient = userClient;
    }


    @PostMapping("/{userId}")
    public Mono<ProfileSimpleResponseDto> createProfile(
            @PathVariable(name = "userId") String userId,
            @RequestBody ProductCreateRequest req)
    {
        Mono<ProfileSimpleResponse> profileMono = userClient.createProfile(userId,req);
        Mono<String> images = imageClient.getProfileUrl(userId);
        return Mono.zip(profileMono, images)
                .map(tuple -> {
                    ProfileSimpleResponse p = tuple.getT1();
                    String i = tuple.getT2();

                    return ProfileSimpleResponseDto.builder()
                            .city(p.getCity())
                            .nickname(p.getNickname())
                            .preferred1(p.getPreferred1())
                            .preferred2(p.getPreferred2())
                            .profileImageUrl(i)
                            .userId(p.getUserId())
                            .build();
                });
    }

    @PutMapping("/{userId}")
    public Mono<ProfileResponse> updateProfile(
            @PathVariable(name = "userId") String userId,
            @RequestBody ProductCreateRequest req)
    {
        userClient.upDateProfile(userId,req);
        return getProfileWithImage(userId);
    }

    @GetMapping("/{userId}")
    public Mono<ProfileResponse> getProfileWithImage(@PathVariable("userId") String userId) {
        Mono<ProfileResponse> profileMono = userClient.getProfile(userId);
        Mono<String> profileImageMono = imageClient.getProfileUrl(userId)
                .onErrorResume(e -> Mono.just((String) null));

        return Mono.zip(profileMono, profileImageMono)
                .map(tuple -> {
                    ProfileResponse profile = tuple.getT1();
                    String profileImageUrl = tuple.getT2();

                    // ProfileResponse 안에 프로필 이미지 URL 추가해서 반환
                    profile.setProfileImageUrl(profileImageUrl);
                    return profile;
                });
    }
    @GetMapping("/users")
    public Mono<ResponseEntity<Page<ProfileSimpleResponse>>> getUserProfiles(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String preferred1,
            @RequestParam(required = false) String preferred2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userClient.searchUsers(nickname, preferred1, preferred2, page, size)
                .flatMap(origPage -> {
                    Flux<ProfileSimpleResponse> enriched = Flux.fromIterable(origPage.getContent())
                            .flatMap(profile -> {
                                return imageClient.getProfileUrl(profile.getUserId())
                                        .onErrorResume(e -> Mono.just((String) null)) // ❗ 이미지 못 찾으면 null
                                        .map(imageUrl -> {
                                            profile.setProfileImageUrl(imageUrl);  // ❗ 프로필 객체에 이미지 URL 추가
                                            return profile;
                                        });
                            });

                    return enriched
                            .collectList()
                            .map(list -> new PageImpl<>(list, origPage.getPageable(), origPage.getTotalElements()))
                            .map(ResponseEntity::ok);
                });
    }

}

