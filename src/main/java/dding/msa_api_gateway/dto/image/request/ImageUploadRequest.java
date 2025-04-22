package dding.msa_api_gateway.dto.image.request;

import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUploadRequest {
    private List<MultipartFile> file;
    private String articleId;
}
