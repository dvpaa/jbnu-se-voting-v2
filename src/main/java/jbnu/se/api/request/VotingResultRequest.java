package jbnu.se.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotingResultRequest {

    private Long electionId;

    private String electionType;
}
