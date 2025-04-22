package dding.msa_api_gateway.controller.Marker;


import dding.msa_api_gateway.clients.Bandroom.Bnadroom.BandRoomClient;
import dding.msa_api_gateway.clients.address.AddressClient;
import dding.msa_api_gateway.clients.image.ImageClient;
import dding.msa_api_gateway.dto.mark.BandRoomMarkResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/bander/markers")
public class MarkerController {

    private final BandRoomClient bandRoomClient;
    private final AddressClient addressClient;
    private final ImageClient imageClient;

    public MarkerController(BandRoomClient bandRoomClient, AddressClient addressClient, ImageClient imageClient)
    {
        this.addressClient = addressClient;
        this.bandRoomClient =bandRoomClient;
        this.imageClient = imageClient;
    }



    //TODO: 마커는 캐시 기반으로 후순위 개발로 미뤄둠.
    @GetMapping()
    public List<BandRoomMarkResponse> getMarkers()
    {
        List<BandRoomMarkResponse> responses = new ArrayList<>();
        return responses;
    }

}

