package jbnu.se.api.request;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HeadquarterRequest {

    private Long electionId;

    private String name;

    @Builder
    public HeadquarterRequest(Long electionId, String name) {
        this.electionId = electionId;
        this.name = name;
    }
}
