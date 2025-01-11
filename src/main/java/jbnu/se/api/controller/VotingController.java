package jbnu.se.api.controller;

import jbnu.se.api.domain.Member;
import jbnu.se.api.request.VotingRequest;
import jbnu.se.api.request.VotingResultRequest;
import jbnu.se.api.response.VotingResultResponse;
import jbnu.se.api.service.VotingService;
import jbnu.se.api.util.SecurityUtils;
import jbnu.se.api.util.VotingServiceFinder;
import jbnu.se.api.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VotingController {

    private final VotingServiceFinder votingServiceFinder;

    @PostMapping("/voting")
    @ResponseStatus(HttpStatus.CREATED)
    public void vote(@Validated(ValidationSequence.class) @RequestBody VotingRequest votingRequest) {
        Member member = new Member(SecurityUtils.getUserId(), SecurityUtils.getUsername());

        VotingService votingService = votingServiceFinder.find(votingRequest.getElectionId());
        votingService.vote(member, votingRequest);
    }

    @GetMapping("/voting/count")
    public VotingResultResponse getVotingResult(@RequestBody VotingResultRequest votingResultRequest) {
        VotingService votingService = votingServiceFinder.find(votingResultRequest.getElectionId());
        return votingService.getVotingResult(votingResultRequest);
    }
}
