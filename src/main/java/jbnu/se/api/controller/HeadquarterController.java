package jbnu.se.api.controller;

import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.request.HeadquarterRequest;
import jbnu.se.api.response.HeadquarterResponse;
import jbnu.se.api.service.HeadquarterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HeadquarterController {

    private final HeadquarterService headquarterService;

    @PostMapping("/headquarters")
    @ResponseStatus(HttpStatus.CREATED)
    public List<HeadquarterResponse> registerHeadquarter(@RequestBody List<HeadquarterRequest> headquarterRequest) {
        List<Headquarter> headquarters = headquarterService.registerHeadquarter(headquarterRequest);
        return headquarters.stream()
                .map(HeadquarterResponse::new)
                .toList();
    }
}
