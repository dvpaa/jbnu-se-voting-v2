package jbnu.se.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HeadquarterRequests {

    @NotEmpty(message = "요청데이터가 존재하지 않습니다.")
    private List<@Valid HeadquarterRequest> headquarters;
}
