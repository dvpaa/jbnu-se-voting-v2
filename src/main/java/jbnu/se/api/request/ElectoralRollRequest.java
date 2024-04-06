package jbnu.se.api.request;

import jakarta.validation.constraints.NotNull;
import jbnu.se.api.annotation.ValidCsvExtension;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import static jbnu.se.api.validation.ValidationGroups.FirstGroup;
import static jbnu.se.api.validation.ValidationGroups.SecondGroup;

@Getter
@Setter
@NoArgsConstructor
public class ElectoralRollRequest {

    private Long electionId;

    @NotNull(message = "명부 파일을 업로드 해주세요.", groups = FirstGroup.class)
    @ValidCsvExtension(message = "명부 파일은 csv 이어야 합니다.", groups = SecondGroup.class)
    private MultipartFile file;
}
