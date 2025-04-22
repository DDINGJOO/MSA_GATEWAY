package dding.msa_api_gateway.controller.bandRoom;


import dding.msa_api_gateway.clients.Bandroom.Bnadroom.BandRoomClient;
import dding.msa_api_gateway.clients.TimeManager.TimeManagerClient;
import dding.msa_api_gateway.clients.address.AddressClient;
import dding.msa_api_gateway.clients.image.ImageClient;
import dding.msa_api_gateway.dto.My.reponse.BandRoomCardResponse;
import dding.msa_api_gateway.dto.My.reponse.BandRoomDetailResponse;
import dding.msa_api_gateway.dto.address.request.AddressCreateRequestDto;
import dding.msa_api_gateway.dto.address.response.AddressResponse;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomCreateRequest;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomSearchRequest;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomWeekRequest;
import dding.msa_api_gateway.dto.bandRoom.request.server.BandRoomCreateRequestDto;
import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bander/bandroom")
public class BandRoomController {
    private final BandRoomClient bandRoomClient;
    private final AddressClient addressClient;
    private final ImageClient imageClient;
    private final TimeManagerClient timeManagerClient;

    public BandRoomController(BandRoomClient bandRoomClient, AddressClient addressClient, ImageClient imageClient, TimeManagerClient timeManagerClient)
    {
        this.addressClient = addressClient;
        this.bandRoomClient =bandRoomClient;
        this.imageClient = imageClient;
        this.timeManagerClient = timeManagerClient;
    }

    @PostMapping
    public Mono<String>  create (@RequestBody BandRoomCreateRequest req)
    {
        if(req.getId() == null)
        {
            req.setId(UUID.randomUUID().toString());
        }
        BandRoomCreateRequestDto bandRoomCreateRequestDto = req.toBandRoomCreateDto(req);
        AddressCreateRequestDto addressCreateRequestDto = req.toAddressCreateDto(req);

        System.out.println(addressCreateRequestDto.getAddressType());
        Mono<String> bandMono = bandRoomClient.createBandRoom(bandRoomCreateRequestDto);
        Mono<String> addrMono = bandMono
                .flatMap(b -> addressClient.createAddress(addressCreateRequestDto));


        return Mono.zip(bandMono, addrMono)
                .map(tuple -> {
                    String b = tuple.getT1();
                    String a = tuple.getT2();
                    return a + b;
                });
    }

    @PostMapping("/{bandRoomId}/weeks")
    public Mono<String> registerBandRoomWeeks(
            @PathVariable(name= "bandRoomId") String bandRoomId,
            @RequestBody List<BandRoomWeekRequest> req)
    {
        return  timeManagerClient.createBandRoomWeeks(bandRoomId, req);

    }
    @GetMapping("/list")
    public Mono<ResponseEntity<Page<BandRoomCardResponse>>> getBandRooms(
            @ModelAttribute BandRoomSearchRequest req,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return bandRoomClient.getBandRooms(req, pageable)
                .flatMap(origPage -> {
                    Flux<BandRoomCardResponse> enriched = Flux.fromIterable(origPage.getContent())
                            .flatMap(bandRoom -> {
                                Mono<AddressResponse> addrMono   = addressClient.getAddress(bandRoom.getId());
                                Mono<List<String>>     imgsMono   = imageClient.getImageUrls(bandRoom.getId());
                                // date가 있으면 isOpen 호출, 없으면 Mono.just(null)
                                Mono<Boolean>          openMono = timeManagerClient.isOpen(bandRoom.getId(), req.getDate(), req.getTime());

                                return Mono.zip(Mono.just(bandRoom), addrMono, imgsMono, openMono)
                                        .map(tuple -> {
                                            BandRoomResponse b       = tuple.getT1();
                                            AddressResponse  a       = tuple.getT2();
                                            List<String>     imgs    = tuple.getT3();
                                            Boolean          isOpen  = tuple.getT4();

                                            return BandRoomCardResponse.builder()
                                                    .id(b.getId())
                                                    .name(b.getName())
                                                    .shortDescription(b.getShortDescription())
                                                    .roadAddress(a.getRoadAddress())
                                                    .city(a.getCity())
                                                    .district(a.getDistrict())
                                                    .thumbnailUrl(imgs.isEmpty() ? null : imgs.get(0))
                                                    // date 파라미터가 없었다면 null, 있었다면 true/false
                                                    .isOpen(isOpen)
                                                    .build();
                                        });
                            });

                    return enriched
                            .collectList()
                            .map(list -> new PageImpl<>(list, pageable, origPage.getTotalElements()));
                })
                .map(ResponseEntity::ok);
    }




    @GetMapping("/detail/{bandRoomId}")
    public Mono<BandRoomDetailResponse> getBandRoomDetail(@PathVariable(name = "bandRoomId") String bandRoomId)
    {


        Mono<BandRoomResponse> bandMono = bandRoomClient.getBandRoom(bandRoomId);
        Mono<AddressResponse> addMono = bandMono
                .flatMap(b -> addressClient.getAddress(bandRoomId));

        Mono<List<String>> images = imageClient.getImageUrls(bandRoomId);


        return Mono.zip(bandMono, addMono, images)
                .map(tuple ->{
                    BandRoomResponse b = tuple.getT1();
                    AddressResponse a = tuple.getT2();
                    List<String> i = tuple.getT3();

                    return BandRoomDetailResponse.builder()
                            .id(b.getId())
                            .name(b.getName())
                            .homepageUrls(b.getHomepageUrls())
                            .parkingAvailable(b.getParkingAvailable())
                            .category(a.getAddressType())
                            .city(a.getCity())
                            .district(a.getDistrict())
                            .keywords(b.getKeywords())
                            .legalDongCode(a.getLegalDongCode())
                            .neighborhood(a.getNeighborhood())
                            .notes(b.getNotes())
                            .images(i)
                            .shortDescription(b.getShortDescription())
                            .thumbnailUrl(i.getFirst())
                            .phone(b.getPhone())
                            .roadAddress(a.getRoadAddress())
                            .longitude(a.getLongitude())
                            .latitude(a.getLatitude())
                            .detailedDescription(b.getDetailedDescription())
                            .build();
                });
    }







}




