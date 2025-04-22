package dding.msa_api_gateway.clients.user;

import dding.msa_api_gateway.dto.product.ProductCreateRequest;
import dding.msa_api_gateway.dto.product.ProductResponse;
import dding.msa_api_gateway.dto.user.ProfileReadResponse;
import dding.msa_api_gateway.dto.user.ProfileResponse;
import dding.msa_api_gateway.dto.user.ProfileSimpleResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Component
public class UserClient {
    private final WebClient wc;

    public UserClient(@Qualifier("userWebClient") WebClient wc) {
        this.wc = wc;

    }

    public Mono<ProfileSimpleResponse> createProfile(String userId, ProductCreateRequest req)
    {
        return wc.post()
                .uri("/api/profiles/"+userId)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ProfileSimpleResponse.class);
    }

    public Mono<ProfileResponse> getProfile(String userId)
    {
        return wc.get()
                .uri("/api/profiles/"+userId)
                .retrieve()
                .bodyToMono(ProfileResponse.class);
    }

    public Mono<Void> upDateProfile(String userId ,ProductCreateRequest req) {
        return wc.put()
                .uri("/api/profiles/" + userId)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<ProfileReadResponse> readProfile(String userId)
    {
        return wc.get()

                .uri("/read/"+userId)
                .retrieve()
                .bodyToMono(ProfileReadResponse.class);
    }

    public Mono<Page<ProfileSimpleResponse>> searchUsers(
            String nickname,
            String preferred1,
            String preferred2,
            int page,
            int size
    ) {
        return wc.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users")
                        .queryParamIfPresent("nickname", Optional.ofNullable(nickname))
                        .queryParamIfPresent("preferred1", Optional.ofNullable(preferred1))
                        .queryParamIfPresent("preferred2", Optional.ofNullable(preferred2))
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Page<ProfileSimpleResponse>>() {});
    }


}




