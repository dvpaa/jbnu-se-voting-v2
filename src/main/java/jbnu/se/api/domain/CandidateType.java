package jbnu.se.api.domain;

import lombok.Getter;

@Getter
public enum CandidateType {
    PRESIDENT(1),
    VICE_PRESIDENT(2);

    private final int priority;

    CandidateType(int priority) {
        this.priority = priority;
    }

}
