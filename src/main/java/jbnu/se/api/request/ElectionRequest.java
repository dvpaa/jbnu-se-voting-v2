package jbnu.se.api.request;

import jbnu.se.api.domain.ElectionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ElectionRequest {

    private String title;

    private Period period;

    private ElectionType electionType;

    public ElectionRequest() {
        this.period = new Period();
    }

    @Getter
    @Setter
    public static class Period {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }
}
