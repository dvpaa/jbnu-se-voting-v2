package jbnu.se.api.service;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.ElectoralRoll;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.domain.Member;
import jbnu.se.api.domain.Period;
import jbnu.se.api.domain.Voting;
import jbnu.se.api.exception.AlreadyVotedException;
import jbnu.se.api.exception.InvalidVotingResultException;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.VotingRepository;
import jbnu.se.api.request.VotingRequest;
import jbnu.se.api.request.VotingResultRequest;
import jbnu.se.api.response.HeadquarterResult;
import jbnu.se.api.response.PrimaryVotingResultResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrimaryVotingServiceTest {

    @Autowired
    private VotingService primaryVotingService;

    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private ElectoralRollRepository electoralRollRepository;

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @AfterEach
    void tearDown() {
        votingRepository.deleteAll();
        electoralRollRepository.deleteAll();
        headquarterRepository.deleteAll();
        electionRepository.deleteAll();
    }

    @Test
    @DisplayName("결선 투표")
    void primaryVoteTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter1 = Headquarter.builder()
                .name("test1")
                .election(savedElection)
                .symbol("1")
                .build();

        Headquarter headquarter2 = Headquarter.builder()
                .name("test2")
                .election(savedElection)
                .symbol("2")
                .build();

        headquarterRepository.save(headquarter1);
        headquarterRepository.save(headquarter2);

        Member voter = new Member("id", "name");

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        // when
        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setResult("1");

        primaryVotingService.vote(voter, votingRequest);

        // then
        List<Voting> all = votingRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getElection().getId()).isEqualTo(savedElection.getId());
        assertThat(all.get(0).getResult()).isEqualTo("1");

        List<ElectoralRoll> electoralRolls = electoralRollRepository.findAll();
        assertThat(electoralRolls).hasSize(1);
        assertThat(electoralRolls.get(0).getVoted()).isTrue();
    }

    @Test
    @DisplayName("결선 투표 결과 검증")
    void primaryVoteResultValidationTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter1 = Headquarter.builder()
                .name("test1")
                .election(savedElection)
                .symbol("1")
                .build();

        Headquarter headquarter2 = Headquarter.builder()
                .name("test2")
                .election(savedElection)
                .symbol("2")
                .build();

        headquarterRepository.save(headquarter1);
        headquarterRepository.save(headquarter2);

        Member voter = new Member("id", "name");

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        // when
        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setResult("3");

        // then
        assertThatThrownBy(() -> primaryVotingService.vote(voter, votingRequest)).isInstanceOf(
                InvalidVotingResultException.class);
    }

    @Test
    @DisplayName("유권자가 이미 투표했으면 오류")
    void alreadyVoteValidationTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter1 = Headquarter.builder()
                .name("test1")
                .election(savedElection)
                .symbol("1")
                .build();

        Headquarter headquarter2 = Headquarter.builder()
                .name("test2")
                .election(savedElection)
                .symbol("2")
                .build();

        headquarterRepository.save(headquarter1);
        headquarterRepository.save(headquarter2);

        Member voter = new Member("id", "name");

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);
        electoralRoll.setVoted(true);

        electoralRollRepository.save(electoralRoll);

        // when
        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setResult("1");

        // then
        assertThatThrownBy(() -> primaryVotingService.vote(voter, votingRequest)).isInstanceOf(
                AlreadyVotedException.class);
    }

    @Test
    @DisplayName("결선 개표")
    void primaryVoteCountTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter1 = Headquarter.builder()
                .name("test1")
                .election(savedElection)
                .symbol("1")
                .build();

        Headquarter headquarter2 = Headquarter.builder()
                .name("test2")
                .election(savedElection)
                .symbol("2")
                .build();

        headquarterRepository.save(headquarter1);
        headquarterRepository.save(headquarter2);

        Member voter = new Member("id", "name");

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        Voting voting = Voting.builder()
                .election(savedElection)
                .result("1")
                .build();

        votingRepository.save(voting);

        // when
        VotingResultRequest votingResultRequest = new VotingResultRequest();
        votingResultRequest.setElectionType("PRIMARY");
        votingResultRequest.setElectionId(savedElection.getId());

        PrimaryVotingResultResponse votingResult = (PrimaryVotingResultResponse) primaryVotingService.getVotingResult(
                votingResultRequest);

        // then
        assertThat(votingResult.getVoterCount()).isEqualTo(1);
        assertThat(votingResult.getVoidCount()).isZero();

        List<HeadquarterResult> headquarterResults = votingResult.getHeadquarterResults();
        assertThat(headquarterResults).hasSize(2);

        assertThat(headquarterResults.get(0).getCount()).isEqualTo(1);
        assertThat(headquarterResults.get(1).getCount()).isZero();
    }
}
