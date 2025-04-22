package dding.msa_api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient bandRoomWebClient(WebClient.Builder builder,
                                       @Value("${service.bandroom.base-url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public WebClient addressWebClient(WebClient.Builder builder,
                                      @Value("${service.address.base-url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public WebClient imageWebClient(WebClient.Builder builder,
                                      @Value("${service.image.base-url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public WebClient timemanagerWebClient(WebClient.Builder builder,
                                    @Value("${service.time-manager.base-url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public WebClient productWebClient(WebClient.Builder builder,
                                       @Value("${service.product-bandroom.base-url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public WebClient userWebClient(WebClient.Builder builder,
                                      @Value("${service.user.base-url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

}
