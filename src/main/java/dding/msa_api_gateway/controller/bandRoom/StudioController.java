package dding.msa_api_gateway.controller.bandRoom;


import dding.msa_api_gateway.clients.Bandroom.studio.StudioClient;
import dding.msa_api_gateway.clients.TimeManager.TimeManagerClient;
import dding.msa_api_gateway.clients.image.ImageClient;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomWeekRequest;
import dding.msa_api_gateway.dto.studio.AvailableHourResponse;
import dding.msa_api_gateway.dto.studio.StudioRequest;
import dding.msa_api_gateway.dto.studio.StudioResponse;
import dding.msa_api_gateway.dto.My.reponse.StudioResponseDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bander/studio")
public class StudioController {
    private final StudioClient studioClient;
    private final ImageClient imageClient;
    private final TimeManagerClient timeManagerClient;

    public StudioController(StudioClient studioClient,  ImageClient imageClient, TimeManagerClient timeManagerClient)
    {

        this.studioClient =studioClient;
        this.imageClient = imageClient;
        this.timeManagerClient = timeManagerClient;
    }


    @PostMapping("/{bandRoomId}/{studioId}/weeks")
    public Mono<String> registerStudioWeeks(
            @PathVariable(name= "bandRoomId") String bandRoomId,
            @PathVariable(name= "studioId") String studioId,
            @RequestBody List<BandRoomWeekRequest> req)
    {
        return  timeManagerClient.createStudioRoomWeeks(bandRoomId,studioId, req);
    }


    @PostMapping("/{bandRoomId}/{studioId}/weeks/upDate")
    public Mono<Void> upDateStudioWeeks(
            @PathVariable(name= "bandRoomId") String bandRoomId,
            @PathVariable(name= "studioId") String studioId,
            @RequestBody List<BandRoomWeekRequest> req)
    {
        return  timeManagerClient.upDateStudioWeeks(bandRoomId,studioId, req);
    }




    @PostMapping()
    public Mono<String> createStudio(
            @RequestBody StudioRequest req)
    {
        System.out.println("coi");
        return  studioClient.createStudio(req);
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

    @GetMapping("/times/{bandRoomId}/{studioId}")
    public Mono<List<AvailableHourResponse>> getAvailableTimes(
            @PathVariable(name = "bandRoomId") String bandRoomId,
            @PathVariable(name ="studioId") String studioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return timeManagerClient.getAvailableTimes(bandRoomId,studioId, date);
    }

}
