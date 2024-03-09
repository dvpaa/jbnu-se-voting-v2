package jbnu.se.api.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PledgeRequest {

    private String description;

    private Long headquarterId;

    private MultipartFile imageFile;
}
