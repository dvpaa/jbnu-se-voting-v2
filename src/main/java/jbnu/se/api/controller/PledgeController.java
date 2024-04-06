package jbnu.se.api.controller;

import jbnu.se.api.request.PledgeRequest;
import jbnu.se.api.service.PledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PledgeController {

    private final PledgeService pledgeService;

    @PostMapping(value = "/admin/pledges", consumes = {MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public void registerPledge(@Validated PledgeRequest pledgeRequest) {
        pledgeService.createPledge(pledgeRequest);
    }
}
