package jbnu.se.api.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HeadquarterRequest {

    @NotNull(message = "선거 id를 입력해 주세요.")
    private Long electionId;

    @NotBlank(message = "선본 이름을 입력해 주세요.")
    private String name;

    @Builder
    public HeadquarterRequest(Long electionId, String name) {
        this.electionId = electionId;
        this.name = name;
    }
}