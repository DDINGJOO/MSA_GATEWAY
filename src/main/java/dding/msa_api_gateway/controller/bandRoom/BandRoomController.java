package dding.msa_api_gateway.controller.bandRoom;


import dding.msa_api_gateway.clients.Bandroom.Bnadroom.BandRoomClient;
import dding.msa_api_gateway.clients.Bandroom.studio.StudioClient;
import dding.msa_api_gateway.clients.TimeManager.TimeManagerClient;
import dding.msa_api_gateway.clients.address.AddressClient;
import dding.msa_api_gateway.clients.image.ImageClient;
import dding.msa_api_gateway.clients.reservation.ProductClient;
import dding.msa_api_gateway.dto.My.reponse.BandRoomCardResponseDto;
import dding.msa_api_gateway.dto.My.reponse.BandRoomDetailResponseDto;
import dding.msa_api_gateway.dto.My.request.AddressCreateRequestDto;
import dding.msa_api_gateway.dto.address.response.AddressResponse;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomCreateRequest;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomSearchRequest;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomWeekRequest;
import dding.msa_api_gateway.dto.bandRoom.request.server.BandRoomCreateRequestDto;
import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import dding.msa_api_gateway.dto.product.ProductCreateRequest;
import dding.msa_api_gateway.dto.product.ProductResponse;
import dding.msa_api_gateway.dto.My.reponse.ProductResponseDto;
import dding.msa_api_gateway.dto.studio.StudioResponse;
import dding.msa_api_gateway.dto.My.reponse.StudioResponseDto;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bander/bandroom")
public class BandRoomController {
    private final BandRoomClient bandRoomClient;
    private final AddressClient addressClient;
    private final ImageClient imageClient;
    private final TimeManagerClient timeManagerClient;
    private final StudioClient studioClient;

    private ProductClient productClient;

    public BandRoomController(BandRoomClient bandRoomClient, ProductClient productClient,AddressClient addressClient, ImageClient imageClient, TimeManagerClient timeManagerClient
    ,StudioClient studioClient)
    {
        this.addressClient = addressClient;
        this.bandRoomClient =bandRoomClient;
        this.imageClient = imageClient;
        this.timeManagerClient = timeManagerClient;
        this.studioClient = studioClient;
        this.productClient = productClient;
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
        Mono<String> bandMono = bandRoomClient.createBandRoom(bandRoomCreateRequestDto).cache();
        Mono<String> addrMono = bandMono.flatMap(b -> addressClient.createAddress(addressCreateRequestDto));

        return Mono.zip(bandMono, addrMono)
                .map(tuple -> {
                    String b = tuple.getT1();
                    String a = tuple.getT2();
                    return a + b;
                });

    }


    @GetMapping("/list")
    public Mono<ResponseEntity<Page<BandRoomCardResponseDto>>> getBandRooms(
            @ModelAttribute BandRoomSearchRequest req,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return bandRoomClient.getBandRooms(req, pageable)
                .flatMap(origPage -> {
                    Flux<BandRoomCardResponseDto> enriched = Flux.fromIterable(origPage.getContent())
                            .flatMap(bandRoom -> {
                                Mono<AddressResponse> addrMono   = addressClient.getAddress(bandRoom.getId());
                                Mono<List<String>>     imgsMono   = imageClient.getImageUrls(bandRoom.getId())
                                        .onErrorResume(e -> {
                                            // ❗ 이미지 못 찾으면 그냥 빈 리스트 반환
                                            return Mono.just(Collections.emptyList());
                                        });
                                // date가 있으면 isOpen 호출, 없으면 Mono.just(null)
                                Mono<Boolean>          openMono = timeManagerClient.isBandRoomOpen(bandRoom.getId(), req.getDate(), req.getTime());

                                return Mono.zip(Mono.just(bandRoom), addrMono, imgsMono, openMono)
                                        .map(tuple -> {
                                            BandRoomResponse b       = tuple.getT1();
                                            AddressResponse  a       = tuple.getT2();
                                            List<String>     imgs    = tuple.getT3();
                                            Boolean          isOpen  = tuple.getT4();

                                            return BandRoomCardResponseDto.builder()
                                                    .id(b.getId())
                                                    .name(b.getName())
                                                    .shortDescription(b.getShortDescription())
                                                    .roadAddress(a.getRoadAddress())
                                                    .city(a.getCity())
                                                    .district(a.getDistrict())
                                                    .thumbnailUrl(imgs.isEmpty() ? null : imgs.getFirst())
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
    public Mono<BandRoomDetailResponseDto> getBandRoomDetail(@PathVariable(name = "bandRoomId") String bandRoomId)
    {


        Mono<BandRoomResponse> bandMono = bandRoomClient.getBandRoom(bandRoomId);
        Mono<AddressResponse> addMono = bandMono
                .flatMap(b -> addressClient.getAddress(bandRoomId));
        Mono<List<ProductResponseDto>> productMono = getAllProducts(bandRoomId);
        Mono<List<StudioResponseDto>> studioMono = getStudios(bandRoomId);

        Mono<List<String>> images = imageClient.getImageUrls(bandRoomId);


        return Mono.zip(bandMono, addMono, images, productMono, studioMono)
                .map(tuple ->{
                    BandRoomResponse b = tuple.getT1();
                    AddressResponse a = tuple.getT2();
                    List<String> i = tuple.getT3();
                    List<ProductResponseDto> p = tuple.getT4();
                    List<StudioResponseDto> s = tuple.getT5();

                    return BandRoomDetailResponseDto.builder()
                            .id(b.getId())
                            .name(b.getName())
                            .homepageUrls(b.getHomepageUrls())
                            .parkingAvailable(b.getParkingAvailable())
                            .category(a.getAddressType())
                            .studioResponseDtos(s)
                            .city(a.getCity())
                            .district(a.getDistrict())
                            .keywords(b.getKeywords())
                            .legalDongCode(a.getLegalDongCode())
                            .productResponseDtos(p)
                            .neighborhood(a.getNeighborhood())
                            .notes(b.getNotes())
                            .thumbnailUrl(i.isEmpty() ? null : i.getFirst())
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
    @GetMapping("/studios/{bandRoomId}")
    public Mono<List<StudioResponseDto>> getStudios(@PathVariable(name = "bandRoomId") String bandRoomId) {
        Mono<List<StudioResponse>> studioMono = studioClient.getStudios(bandRoomId);

        return studioMono
                .flatMapMany(Flux::fromIterable)  // List -> Flux<StudioResponse>
                .flatMap(studio -> {
                    // 각 Studio에 대해 이미지 조회
                    Mono<List<String>> imageUrlsMono = imageClient.getImageUrls(studio.getId())
                            .onErrorResume(e -> {
                                // ❗ 이미지 못 찾으면 그냥 빈 리스트 반환
                                return Mono.just(Collections.emptyList());
                            });

                    // 스튜디오와 이미지 리스트를 합쳐서 StudioResponseDto를 만든다
                    return imageUrlsMono.map(imageUrls ->
                            StudioResponseDto.builder()
                                    .id(studio.getId())
                                    .name(studio.getName())
                                    .description(studio.getDescription())
                                    .defaultPrice(studio.getDefaultPrice())
                                    .pricePoliciesDescription(studio.getPricePoliciesDescription())
                                    .isAvailable(studio.isAvailable())
                                    .imageUrls(imageUrls.isEmpty() ? null : imageUrls)
                                    .build()
                    );
                })
                .collectList(); // Flux<StudioResponseDto> -> Mono<List<StudioResponseDto>>
    }


    @GetMapping("/isOpen/{bandRoomId}")
    public Mono<Boolean> isBandRoomOpen(
            @PathVariable(name = "bandRoomId") String bandRoomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time
    ) {
        return timeManagerClient.isBandRoomOpen(bandRoomId, date, time);
    }

    @GetMapping("/isOpen/{bandRoomId}/{studioId}")
    public Mono<Boolean> isStudioOpen(
            @PathVariable(name = "bandRoomId") String bandRoomId,
            @PathVariable(name ="studioId") String studioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time
    ) {
        return timeManagerClient.isStudioOpen(bandRoomId,studioId, date, time);
    }

    @PostMapping("/weeks/{bandRoomId}")
    public Mono<String> registerBandRoomWeeks(
            @PathVariable(name= "bandRoomId") String bandRoomId,
            @RequestBody List<BandRoomWeekRequest> req)
    {
        return  timeManagerClient.createBandRoomWeeks(bandRoomId, req);
    }



    /// ///////////////////////////////////

    @PostMapping("/products/{bandRoomId}")
    public Mono<ProductResponse> createProduct(
            @PathVariable(name ="bandRoomId") String bandRoomId,
            @RequestBody ProductCreateRequest req
            )
    {
        return productClient.createBandRoomProduct(bandRoomId,req);
    }

    @GetMapping("/products/{productId}")
    public Mono<ProductResponseDto> getProduct(
            @PathVariable(name = "productId") String productId
    )
    {
        Mono<ProductResponse> productMono = productClient.getProduct(productId);
        Mono<List<String>> images = imageClient.getImageUrls(productId)
                .onErrorResume(e -> {
                    // ❗ 이미지 못 찾으면 그냥 빈 리스트 반환
                    return Mono.just(Collections.emptyList());
                });

        return Mono.zip( images, productMono)
                .map(tuple ->{
                    List<String> i = tuple.getT1();
                    ProductResponse p = tuple.getT2();

                    return ProductResponseDto.builder()
                            .description(p.getDescription())
                            .name(p.getName())
                            .productId(p.getProductId())
                            .thumbnailUrl(i.isEmpty() ? null : i.get(0))
                            .price(p.getPrice())
                            .build();
                });
    }


    @GetMapping("/products/list/{bandRoomId}")
    public Mono<List<ProductResponseDto>> getAllProducts(
            @PathVariable(name ="bandRoomId") String bandRoomId
    )
    {
        Mono<List<ProductResponse>> proMono = productClient.getBandRoomAllProducts(bandRoomId);
        return proMono
                .flatMapMany(Flux::fromIterable)
                .flatMap(product -> {
                    Mono<List<String>> imageUrlsMono = imageClient.getImageUrls(product.getProductId())
                            .onErrorResume(e -> {
                                // ❗ 이미지 못 찾으면 그냥 빈 리스트 반환
                                return Mono.just(Collections.emptyList());
                            });
                    return imageUrlsMono.map(imageUrls ->

                            ProductResponseDto.builder()
                                    .price(product.getPrice())
                                    .thumbnailUrl(imageUrls.isEmpty() ? null : imageUrls.get(0))
                                    .productId(product.getProductId())
                                    .description(product.getDescription())
                                    .build());
                })
                .collectList();
    }
}




