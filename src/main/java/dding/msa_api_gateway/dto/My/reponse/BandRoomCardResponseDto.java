package dding.msa_api_gateway.dto.My.reponse;

import dding.msa_api_gateway.dto.bandRoom.response.BandRoomResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BandRoomCardResponseDto {
    private String id;
    private String name;
    private String shortDescription;
    private String roadAddress;
    private String city;
    private String district;
    private String thumbnailUrl;
    private boolean isOpen;

    /**
     * BandRoomResponse와 isOpen 플래그를 받아
     * BandRoomCardResponseDto로 변환해 주는 팩토리 메서드
     */
    public static BandRoomCardResponseDto from(BandRoomResponse room, boolean isOpen) {
        return BandRoomCardResponseDto.builder()
                .id(room.getId())
                .name(room.getName())
                .shortDescription(room.getShortDescription())

                // room에 city, district 프로퍼티가 없으면 제거하거나,
                // 필요한 로직(파싱 등)을 추가하세요

                .isOpen(isOpen)
                .build();
    }
}




