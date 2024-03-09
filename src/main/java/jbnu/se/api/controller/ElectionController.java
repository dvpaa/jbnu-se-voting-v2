package jbnu.se.api.controller;

import jbnu.se.api.domain.Election;
import jbnu.se.api.request.ElectionRequest;
import jbnu.se.api.response.ElectionResponse;
import jbnu.se.api.service.ElectionService;
import jbnu.se.api.util.SecurityUtils;
import jbnu.se.api.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @PostMapping("/admin/elections")
    @ResponseStatus(HttpStatus.CREATED)
    public ElectionResponse createElection(@Validated(ValidationSequence.class) @RequestBody ElectionRequest electionRequest) {
        String userId = SecurityUtils.getUserId();
        Election election = electionService.registerElection(userId, electionRequest);

        return new ElectionResponse(election);
    }

    @GetMapping("/elections")
    public List<ElectionResponse> getAllElections() {
        return electionService.findAllElections();
    }
}
