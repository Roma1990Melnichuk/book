package com.bookstore.service;

import com.bookstore.dto.UserRegistrationRequestDto;
import com.bookstore.dto.UserResponseDto;
import com.bookstore.entity.User;
import com.bookstore.exception.RegistrationException;
import com.bookstore.mapper.UserMapper;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with this email already exists");
        }
        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }
}
