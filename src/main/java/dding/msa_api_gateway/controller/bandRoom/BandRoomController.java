package dding.msa_api_gateway.controller.bandRoom;


import dding.msa_api_gateway.clients.Bandroom.Bnadroom.BandRoomClient;
import dding.msa_api_gateway.clients.Bandroom.studio.StudioClient;
import dding.msa_api_gateway.clients.TimeManager.TimeManagerClient;
import dding.msa_api_gateway.clients.address.AddressClient;
import dding.msa_api_gateway.clients.image.ImageClient;
import dding.msa_api_gateway.clients.reservation.ProductClient;
import dding.msa_api_gateway.dto.My.reponse.*;
import dding.msa_api_gateway.dto.My.request.AddressCreateRequestDto;
import dding.msa_api_gateway.dto.address.response.AddressResponse;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomCreateRequest;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomSearchRequest;
import dding.msa_api_gateway.dto.bandRoom.request.client.BandRoomWeekRequest;
import dding.msa_api_gateway.dto.bandRoom.request.server.BandRoomCreateRequestDto;
import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import dding.msa_api_gateway.dto.product.ProductCreateRequest;
import dding.msa_api_gateway.dto.product.ProductResponse;
import dding.msa_api_gateway.dto.studio.StudioResponse;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/bander/bandroom")
public class BandRoomController {
    private final BandRoomClient bandRoomClient;
    private final AddressClient addressClient;
    private final ImageClient imageClient;
    private final TimeManagerClient timeManagerClient;
    private final StudioClient studioClient;

    private final ProductClient productClient;

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


    @GetMapping("/list")
    public Mono<Page<BandRoomCardResponseDto>> list(
            BandRoomSearchRequest req,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,ASC") List<String> sort,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time
    ) {
        // 1) 검색 DTO
        BandRoomSearchRequestDto dto = BandRoomSearchRequestDto.builder()
                .isOpen(req.getIsOpen())
                .keyword(req.getKeyword())
                .name(req.getName())
                .roadAddress(req.getRoadAddress())
                .build();

        // 2) Pageable
        Sort s = Sort.by(sort.stream()
                .map(o -> {
                    String[] parts = o.split(",");
                    return new Sort.Order(
                            Sort.Direction.fromString(parts[1].trim()),
                            parts[0].trim()
                    );
                })
                .toList());
        Pageable pg = PageRequest.of(page, size, s);

        // 3) 원본 페이지 가져오기
        return bandRoomClient.getBandRooms(dto, pg)
                .flatMap(pageImpl -> {
                    // 4) 각 방마다 isOpen 조회 → Card DTO 변환
                    var monos = pageImpl.getContent().stream()
                            .map(room -> timeManagerClient
                                    .isBandRoomOpen(room.getId(), date, time)
                                    .map(isOpen -> BandRoomCardResponseDto.from(room, isOpen))
                            ).toList();

                    return Flux.concat(monos)
                            .collectList()
                            .map(cards -> new PageImpl<>(
                                    cards,
                                    pg,
                                    pageImpl.getTotalElements()
                            ));
                });
    }



    //TIME


    @GetMapping("/isOpen")
    public Mono<Boolean> isStudioOpen(
            @RequestParam(name = "bandRoomId") String bandRoomId,
            @RequestParam (required = false) String studioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time
    ) {
        if(studioId == null) {
            return timeManagerClient.isBandRoomOpen(bandRoomId, date, time);
        }
        return timeManagerClient.isStudioOpen(bandRoomId, studioId, date, time);
    }

    @PostMapping("/weeks")
    public Mono<String> registerBandRoomWeeks(
            @RequestParam(name= "bandRoomId") String bandRoomId,
            @RequestBody List<BandRoomWeekRequest> req)
    {
        return  timeManagerClient.createBandRoomWeeks(bandRoomId, req);
    }



    /// ///////////////////////////////////

    @PostMapping("/products")
    public Mono<ProductResponse> createProduct(
            @RequestParam(name ="bandRoomId") String bandRoomId,
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

    @PutMapping("/products")
    public Mono<ProductResponseDto> updateProduct(
            @RequestParam(name ="productId") String productId,
            @RequestBody ProductCreateRequest req
    )
    {
        Mono<ProductResponse> productMono = productClient.updateProduct(productId,req);
        Mono<List<String>> images = imageClient.getImageUrls(productId)
                .onErrorResume(e -> {
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


    @GetMapping("/products/list")
    public Mono<List<ProductResponseDto>> getAllProducts(
            @RequestParam(name ="bandRoomId") String bandRoomId
    )
    {
        Mono<List<ProductResponse>> proMono = productClient.getBandRoomProducts(bandRoomId);
        return proMono
                .flatMapMany(Flux::fromIterable)
                .flatMap(product -> {
                    Mono<List<String>> imageUrlsMono = imageClient.getImageUrls(product.getProductId())
                            .onErrorResume(e -> {
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




///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
