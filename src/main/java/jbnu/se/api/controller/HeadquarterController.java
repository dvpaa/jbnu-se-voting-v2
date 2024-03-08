package jbnu.se.api.controller;

import jbnu.se.api.domain.Candidate;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.request.ElectionIdRequest;
import jbnu.se.api.request.HeadquarterRequests;
import jbnu.se.api.response.HeadquarterResponse;
import jbnu.se.api.service.CandidateService;
import jbnu.se.api.service.HeadquarterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HeadquarterController {

    private final HeadquarterService headquarterService;

    private final CandidateService candidateService;

    @PostMapping("/headquarters")
    @ResponseStatus(HttpStatus.CREATED)
    public List<HeadquarterResponse> registerHeadquarter(@Validated @RequestBody HeadquarterRequests headquarterRequest) {
        List<Headquarter> headquarters = headquarterService.registerHeadquarter(headquarterRequest);
        return headquarters.stream()
                .map(HeadquarterResponse::new)
                .toList();
    }

    @GetMapping("/headquarters")
    public List<HeadquarterResponse> getHeadquartersByElection(@RequestBody ElectionIdRequest electionIdRequest) {
        List<Headquarter> headquarters = headquarterService.getHeadquartersByElection(electionIdRequest.getElectionId());

        return headquarters.stream()
                .map(headquarter -> {
                    List<Candidate> candidates = candidateService.getCandidatesByHeadquarter(headquarter.getId());
                    HeadquarterResponse headquarterResponse = new HeadquarterResponse(headquarter);
                    headquarterResponse.setCandidatePair(candidates);

                    return headquarterResponse;
                })
                .toList();
    }
}
