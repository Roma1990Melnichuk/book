package com.bookstore.service;

import com.bookstore.dto.UserRegistrationRequestDto;
import com.bookstore.dto.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);
}
