package dding.msa_api_gateway.controller.bandRoom;


import dding.msa_api_gateway.clients.Bandroom.Bnadroom.BandRoomClient;
import dding.msa_api_gateway.clients.Bandroom.studio.StudioClient;
import dding.msa_api_gateway.clients.address.AddressClient;
import dding.msa_api_gateway.clients.image.ImageClient;
import dding.msa_api_gateway.dto.My.reponse.BandRoomDetailResponse;
import dding.msa_api_gateway.dto.address.response.AddressResponse;
import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import dding.msa_api_gateway.dto.studio.StudioRequest;
import dding.msa_api_gateway.dto.studio.StudioResponse;
import dding.msa_api_gateway.dto.studio.StudioResponseDto;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/bander/studio")
public class StudioController {
    private final StudioClient studioClient;
    private final ImageClient imageClient;

    public StudioController(StudioClient studioClient, AddressClient addressClient, ImageClient imageClient)
    {

        this.studioClient =studioClient;
        this.imageClient = imageClient;
    }


    @PostMapping()
    public Mono<String> createStudio(
            @RequestBody StudioRequest req)
    {
        return  studioClient.createStudio(req,req.getBandRoomId());
    }

    @GetMapping("/{studioId}")
    public Mono<StudioResponseDto> getStudio(
            @PathVariable(name = "studioId") String studioId)
    {

        Mono<StudioResponse> studioMono = studioClient.getStudio(studioId);
        Mono<List<String>> imagesMono = studioMono
                .flatMap(b-> imageClient.getImageUrls(studioId));
        return Mono.zip(studioMono, imagesMono)
            .map(tuple ->{
                StudioResponse s = tuple.getT1();
                List<String> i = tuple.getT2();


                return StudioResponseDto.builder()
                        .defaultPrice(s.getDefaultPrice())
                        .description(s.getDescription())
                        .id(s.getId())
                        .isAvailable(s.isAvailable())
                        .pricePoliciesDescription(s.getPricePoliciesDescription())
                        .name(s.getName())
                        .imageUrls(i)
                        .build();
            });

        /*
                Mono<AddressResponse> addMono = bandMono
                .flatMap(b -> addressClient.getAddress(bandRoomId));
         */


    }
}
