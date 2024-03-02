package jbnu.se.api.controller;

import jbnu.se.api.request.ElectionRequest;
import jbnu.se.api.service.ElectionService;
import jbnu.se.api.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @PostMapping("/api/elections")
    @ResponseStatus(HttpStatus.CREATED)
    public void createElection(@RequestBody ElectionRequest electionRequest) {
        String userId = SecurityUtils.getUserId();
        electionService.registerElection(userId, electionRequest);
    }
}
