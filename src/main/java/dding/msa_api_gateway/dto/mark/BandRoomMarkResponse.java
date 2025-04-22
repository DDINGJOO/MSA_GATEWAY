package dding.msa_api_gateway.dto.mark;


import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BandRoomMarkResponse {

    private String bandRoomId;
    private String thumbnailUrl;
    private String name;
    private String category;
    private String color;
    private String roadAddress;
    private String score;
    private String description;
    private Double latitude;
    private Double longitude;

}
