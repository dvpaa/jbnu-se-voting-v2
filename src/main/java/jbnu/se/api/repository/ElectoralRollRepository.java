package jbnu.se.api.repository;

import jbnu.se.api.domain.ElectoralRoll;
import jbnu.se.api.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ElectoralRollRepository extends JpaRepository<ElectoralRoll, Long> {
    Optional<ElectoralRoll> findByElectionIdAndMember(Long electionId, Member member);
}
