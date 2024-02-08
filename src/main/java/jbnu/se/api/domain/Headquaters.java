package jbnu.se.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Headquaters extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pledge_id")
    private Pledge pledge;

    @Column(nullable = false)
    private String name;
}
