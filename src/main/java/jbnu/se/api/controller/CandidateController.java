package jbnu.se.api.controller;

import jbnu.se.api.request.CandidatePair;
import jbnu.se.api.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping("/candidates")
    public void registerCandidate(@RequestBody List<CandidatePair> candidatePairs) {
        candidateService.registerCandidate(candidatePairs);
    }
}
