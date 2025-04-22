package dding.msa_api_gateway.dto.bandRoom.request.client;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BandRoomSearchRequest {
    private String name;
    private String keyword;
    private Boolean isOpen;
    private String roadAddress;
    private String Category;
    private LocalDate date;
    private LocalTime time;
}
