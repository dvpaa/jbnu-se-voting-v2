package jbnu.se.api.response;

import jbnu.se.api.domain.Election;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ElectionResponse {

    private final Long id;
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String electionType;

    public ElectionResponse(Election election) {
        this.id = election.getId();
        this.title = election.getTitle();
        this.startDate = election.getPeriod().getStartDate();
        this.endDate = election.getPeriod().getEndDate();
        this.electionType = election.getElectionType().name();
    }

    @Builder
    public ElectionResponse(Long id, String title, LocalDateTime startDate, LocalDateTime endDate, String electionType) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.electionType = electionType;
    }
}