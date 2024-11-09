package com.bookstore.controller;

import com.bookstore.dto.UserLoginRequestDto;
import com.bookstore.dto.UserLoginResponseDto;
import com.bookstore.dto.UserRegistrationRequestDto;
import com.bookstore.dto.UserResponseDto;
import com.bookstore.exception.RegistrationException;
import com.bookstore.response.ResponseHandler;
import com.bookstore.response.SuccessResponse;
import com.bookstore.security.AuthenticationService;
import com.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with the "
                    + "provided information and returns a confirmation of successful registration."
    )

    public SuccessResponse<UserResponseDto> register(
            @Valid @RequestBody UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return ResponseHandler.getSuccessResponse(
                userService.register(requestDto),
                HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public SuccessResponse<UserLoginResponseDto> login(
            @Valid @RequestBody UserLoginRequestDto requestDto) {
        return ResponseHandler.getSuccessResponse(
                authenticationService.authenticate(requestDto)
        );
    }
}
