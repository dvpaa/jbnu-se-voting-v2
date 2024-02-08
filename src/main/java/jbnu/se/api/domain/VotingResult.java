package jbnu.se.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class VotingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    /**
     * 단선인 경우 "찬성", "반대"
     * 경선인 경우 선거운동본부 이름
     */
    @Column(nullable = false)
    private String result;

    private LocalDateTime createdDate;
}
