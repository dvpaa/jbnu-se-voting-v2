package jbnu.se.api.repository;

import jbnu.se.api.domain.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotingRepository extends JpaRepository<Voting, Long> {

    Long countByElectionIdAndResult(Long electionId, String result);
}
