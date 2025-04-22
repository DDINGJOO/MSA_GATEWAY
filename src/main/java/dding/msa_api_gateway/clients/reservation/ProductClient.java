package dding.msa_api_gateway.clients.reservation;


import dding.msa_api_gateway.dto.product.ProductCreateRequest;
import dding.msa_api_gateway.dto.product.ProductResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ProductClient {
    private final WebClient wc;
    public ProductClient(@Qualifier("productWebClient") WebClient wc)
    {
        this.wc = wc;
    }


    public Mono<ProductResponse> createBandRoomProduct(String bandRoomId, ProductCreateRequest req)
    {
        return wc.post()
                .uri("/api/products/"+bandRoomId)
                .bodyValue(req)
                .retrieve()
//                .onStatus(HttpStatus::isError, resp ->
//                        resp.bodyToMono(String.class)
//                                .flatMap(body -> Mono.error(new RuntimeException("BFF 에러 " + body)))
//                )
                .bodyToMono(ProductResponse.class);
    }

    public Mono<ProductResponse> getProduct(String productId)
    {
        return wc.get()
                .uri("/api/products/"+productId)
                .retrieve()
//                .onStatus(HttpStatus::isError, resp ->
//                        resp.bodyToMono(String.class)
//                                .flatMap(body -> Mono.error(new RuntimeException("BFF 에러 " + body)))
//                )
                .bodyToMono(ProductResponse.class);
    }


    public Mono<List<ProductResponse>> getBandRoomAllProducts(String bandRoomId)
    {
        return wc.get()
                .uri("/api/products/all/"+bandRoomId)
                .retrieve()
//                .onStatus(HttpStatus::isError, resp ->
//                        resp.bodyToMono(String.class)
//                                .flatMap(body -> Mono.error(new RuntimeException("BFF 에러 " + body)))
//                )
                .bodyToMono(new ParameterizedTypeReference<List<ProductResponse>>() {});
    }




}
