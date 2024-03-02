package jbnu.se.api.controller;

import jbnu.se.api.domain.Election;
import jbnu.se.api.request.ElectionRequest;
import jbnu.se.api.service.ElectionService;
import jbnu.se.api.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @PostMapping("/elections")
    @ResponseStatus(HttpStatus.CREATED)
    public void createElection(@RequestBody ElectionRequest electionRequest) {
        String userId = SecurityUtils.getUserId();
        electionService.registerElection(userId, electionRequest);
    }

    @GetMapping("/elections")
    public List<Election> getAllElections() {
        return electionService.findAllElections();
    }
}
