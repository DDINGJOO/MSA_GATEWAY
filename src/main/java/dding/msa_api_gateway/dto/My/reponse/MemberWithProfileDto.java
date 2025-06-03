package dding.msa_api_gateway.dto.My.reponse;

import dding.msa_api_gateway.config.BandPosition;
import dding.msa_api_gateway.dto.user.ProfileResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberWithProfileDto {
    private String userId;
    private BandPosition position;
    private String description;
    private LocalDateTime confirmedAt;

    // 추가된 프로필 응답 전체
    private ProfileResponse profile;
}
