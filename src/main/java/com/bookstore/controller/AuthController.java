package com.bookstore.controller;

import com.bookstore.dto.UserRegistrationRequestDto;
import com.bookstore.dto.UserResponseDto;
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

    @PostMapping("/registration")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with the "
                    + "provided information and returns a confirmation of successful registration."
    )
    @ResponseStatus(HttpStatus.CREATED)

    public UserResponseDto registerUser(@Valid @RequestBody UserRegistrationRequestDto requestDto) {

        return userService.register(requestDto);
    }
}
