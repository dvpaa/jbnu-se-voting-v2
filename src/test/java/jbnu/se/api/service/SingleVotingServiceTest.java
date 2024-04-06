package jbnu.se.api.service;

import jbnu.se.api.domain.*;
import jbnu.se.api.exception.AlreadyVotedException;
import jbnu.se.api.exception.InvalidVotingResultException;
import jbnu.se.api.exception.UnmatchedElectionTypeException;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.VotingRepository;
import jbnu.se.api.request.VotingRequest;
import jbnu.se.api.request.VotingResultRequest;
import jbnu.se.api.response.SingleVotingResultResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SingleVotingServiceTest {

    @Autowired
    private VotingService singleVotingService;

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
    @DisplayName("단선 투표")
    void singleVoteTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .election(savedElection)
                .symbol("1")
                .build();

        headquarterRepository.save(headquarter);

        Member voter = new Member("id", "name");

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        // when
        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setResult("agree");
        votingRequest.setElectionType("SINGLE");

        singleVotingService.vote(voter, votingRequest);

        // then
        List<Voting> all = votingRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getElection().getId()).isEqualTo(savedElection.getId());
        assertThat(all.get(0).getResult()).isEqualTo("agree");

        List<ElectoralRoll> electoralRolls = electoralRollRepository.findAll();
        assertThat(electoralRolls).hasSize(1);
        assertThat(electoralRolls.get(0).getVoted()).isTrue();
    }

    @Test
    @DisplayName("단선 투표 결과 검증")
    void singleVoteResultValidationTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .election(savedElection)
                .symbol("1")
                .build();

        headquarterRepository.save(headquarter);

        Member voter = new Member("id", "name");

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        // when
        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setResult("test");
        votingRequest.setElectionType("SINGLE");

        // then
        assertThatThrownBy(() -> singleVotingService.vote(voter, votingRequest)).isInstanceOf(InvalidVotingResultException.class);
    }

    @Test
    @DisplayName("선거 종류가 맞지 않으면 오류")
    void singleElectionTypeValidationTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .election(savedElection)
                .symbol("1")
                .build();

        headquarterRepository.save(headquarter);

        Member voter = new Member("id", "name");

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        // when
        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setResult("1");
        votingRequest.setElectionType("PRIMARY");

        // then
        assertThatThrownBy(() -> singleVotingService.vote(voter, votingRequest)).isInstanceOf(UnmatchedElectionTypeException.class);
    }

    @Test
    @DisplayName("유권자가 이미 투표했으면 오류")
    void alreadyVoteValidationTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .election(savedElection)
                .symbol("1")
                .build();

        headquarterRepository.save(headquarter);

        Member voter = new Member("id", "name");

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);
        electoralRoll.setVoted(true);

        electoralRollRepository.save(electoralRoll);

        // when
        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setResult("agree");
        votingRequest.setElectionType("SINGLE");

        // then
        assertThatThrownBy(() -> singleVotingService.vote(voter, votingRequest)).isInstanceOf(AlreadyVotedException.class);
    }

    @Test
    @DisplayName("단선 - 개표")
    void singleVotingCountTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .election(savedElection)
                .symbol("1")
                .build();

        headquarterRepository.save(headquarter);

        Member voter = new Member("id", "name");

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);
        electoralRoll.setVoted(true);

        electoralRollRepository.save(electoralRoll);

        Voting voting = new Voting();
        voting.setElection(savedElection);
        voting.setResult("agree");

        votingRepository.save(voting);

        // when
        VotingResultRequest votingResultRequest = new VotingResultRequest();
        votingResultRequest.setElectionId(savedElection.getId());
        votingResultRequest.setElectionType("SINGLE");

        SingleVotingResultResponse votingResult = (SingleVotingResultResponse) singleVotingService.getVotingResult(votingResultRequest);

        // then
        assertThat(votingResult.getVoterCount()).isEqualTo(1);
        assertThat(votingResult.getAgreeCount()).isEqualTo(1);
        assertThat(votingResult.getDisagreeCount()).isZero();
        assertThat(votingResult.getVoidCount()).isZero();
    }
}