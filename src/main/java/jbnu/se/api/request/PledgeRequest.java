package jbnu.se.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PledgeRequest {

    private String description;

    @NotNull(message = "선본 id를 입력해 주세요.")
    private Long headquarterId;

    private MultipartFile imageFile;
}
