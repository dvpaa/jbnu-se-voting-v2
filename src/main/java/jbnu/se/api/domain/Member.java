package jbnu.se.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String name;
}
