package jbnu.se.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Member {

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String name;
}
