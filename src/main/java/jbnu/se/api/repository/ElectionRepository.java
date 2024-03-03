package jbnu.se.api.repository;

import jbnu.se.api.domain.Election;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<Election, Long> {
}
