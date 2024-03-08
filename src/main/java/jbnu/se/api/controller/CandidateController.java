package jbnu.se.api.controller;

import jbnu.se.api.request.CandidatePairRequests;
import jbnu.se.api.service.CandidateService;
import jbnu.se.api.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping("/candidates")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerCandidate(@Validated(ValidationSequence.class) @RequestBody CandidatePairRequests candidatePairRequests) {
        candidateService.registerCandidate(candidatePairRequests);
    }
}
