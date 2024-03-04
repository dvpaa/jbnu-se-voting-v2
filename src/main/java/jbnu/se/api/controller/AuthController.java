package jbnu.se.api.controller;

import jbnu.se.api.request.LoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("/api/login")
    public void login(@RequestBody LoginRequest loginRequest) {
        // Spring Security intercepts the request.
    }
}
