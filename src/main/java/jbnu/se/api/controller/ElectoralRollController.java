package jbnu.se.api.controller;

import jbnu.se.api.request.ElectoralRollRequest;
import jbnu.se.api.service.ElectoralRollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ElectoralRollController {

    private final ElectoralRollService electoralRollService;

    @PostMapping(value = "/admin/electoralRoll", consumes = {MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public void registerElectoralRoll(ElectoralRollRequest electoralRollRequest) {
        electoralRollService.registerElectoralRoll(electoralRollRequest);
    }
}
