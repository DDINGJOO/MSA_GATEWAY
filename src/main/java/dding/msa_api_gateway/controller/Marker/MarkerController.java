package dding.msa_api_gateway.controller.Marker;


import dding.msa_api_gateway.clients.Bandroom.Bnadroom.BandRoomClient;
import dding.msa_api_gateway.clients.address.AddressClient;
import dding.msa_api_gateway.clients.image.ImageClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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

}

