package jbnu.se.api.controller;

import jbnu.se.api.request.CandidatePair;
import jbnu.se.api.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping("/candidates")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerCandidate(@RequestBody List<CandidatePair> candidatePairs) {
        candidateService.registerCandidate(candidatePairs);
    }
}
