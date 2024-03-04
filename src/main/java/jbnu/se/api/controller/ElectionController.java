package jbnu.se.api.controller;

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

    @PostMapping("/elections")
    @ResponseStatus(HttpStatus.CREATED)
    public void createElection(@Validated(ValidationSequence.class) @RequestBody ElectionRequest electionRequest) {
        String userId = SecurityUtils.getUserId();
        electionService.registerElection(userId, electionRequest);
    }

    @GetMapping("/elections")
    public List<ElectionResponse> getAllElections() {
        return electionService.findAllElections();
    }
}
