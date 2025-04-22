package dding.msa_api_gateway.clients.image;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class ImageClient {
    private final WebClient wc;
    public ImageClient(@Qualifier("imageWebClient") WebClient wc)
    {
        this.wc = wc;
    }


    public Mono<List<String>> getImageUrls(String articleId) {
        return wc.get()
                .uri("/api/post-images/urls/{articleId}", articleId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
    }

    public Mono<String> getProfileUrl(String userId)
    {
        return wc.get()
                .uri("/api/profile-image/"+userId)
                .retrieve()
                .bodyToMono(String.class);

    }





}
