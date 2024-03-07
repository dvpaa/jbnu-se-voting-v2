package jbnu.se.api.repository;

import jbnu.se.api.domain.Headquarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeadquarterRepository extends JpaRepository<Headquarter, Long> {
    List<Headquarter> findAllByElectionId(Long id);
}
