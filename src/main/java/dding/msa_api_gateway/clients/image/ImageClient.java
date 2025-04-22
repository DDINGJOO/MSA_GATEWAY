package dding.msa_api_gateway.clients.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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





}
