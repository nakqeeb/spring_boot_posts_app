package com.nakqeeb.posts_app.controller;

import com.nakqeeb.posts_app.dto.LoginUserDto;
import com.nakqeeb.posts_app.dto.RegisterUserDto;
import com.nakqeeb.posts_app.entity.User;
import com.nakqeeb.posts_app.response.LoginResponse;
import com.nakqeeb.posts_app.service.AuthenticationService;
import com.nakqeeb.posts_app.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Auth", description = "AuthenticationController")
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @Operation(
            description = "Sign up a new user",
            summary = "This is a summary for register endpoint"
    )
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            User registeredUser = authenticationService.signup(registerUserDto);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (Exception e) {
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status",  HttpStatus.CONFLICT.value());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    @Operation(
            description = "Log in an existing activated user",
            summary = "This is a summary for authenticate endpoint"
    )
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);

            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            // Prepare a response message with status
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status",  HttpStatus.UNAUTHORIZED.value());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

        }

    }
}