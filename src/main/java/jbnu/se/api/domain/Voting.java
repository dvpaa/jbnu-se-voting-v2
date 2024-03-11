package jbnu.se.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Voting {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    /**
     * 단선인 경우 "찬성", "반대", "기권, 무효"
     * 경선인 경우 기호
     */
    @Column(nullable = false, updatable = false)
    private String result;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Builder
    public Voting(Election election, String result) {
        this.election = election;
        this.result = result;
    }
}
