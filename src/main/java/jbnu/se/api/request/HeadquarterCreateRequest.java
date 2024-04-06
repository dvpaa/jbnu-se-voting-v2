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
public class HeadquarterCreateRequest {

    @NotNull(message = "선거 id를 입력해 주세요.")
    private Long electionId;

    @NotBlank(message = "선본 이름을 입력해 주세요.")
    private String name;

    @NotBlank(message = "기호를 입력해 주세요.")
    private String symbol;

    @Builder
    public HeadquarterCreateRequest(Long electionId, String name, String symbol) {
        this.electionId = electionId;
        this.name = name;
        this.symbol = symbol;
    }
}