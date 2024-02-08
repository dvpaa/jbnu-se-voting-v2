package jbnu.se.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@Setter
public class Pledge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Lob
    private String discription;

    private String posterPath;
}
