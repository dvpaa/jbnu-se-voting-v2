package jbnu.se.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Headquarter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "pledge_id")
    private Pledge pledge;

    @Column(nullable = false)
    private String name;

    @Builder
    public Headquarter(Election election, String name) {
        this.election = election;
        this.name = name;
    }
}
