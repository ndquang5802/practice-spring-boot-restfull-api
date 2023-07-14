package com.store.api.practiceapi.service;

import com.store.api.practiceapi.dto.RegisterDTO;
import com.store.api.practiceapi.controller.exception.UserNotFoundException;
import com.store.api.practiceapi.model.User;
import com.store.api.practiceapi.repository.UserRepository;
import com.store.api.practiceapi.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public String saveUser(RegisterDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserNotFoundException("username is exist: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .build();

        userRepository.save(user);
        return "Add new user success!";
    }

    public MessageResponse changePassword(String username, String oldPassword, String newPassword) {
        // Retrieve the user from the database based on the username
        Optional<User> user = userRepository.findByUsername(username);

        // Verify the user's old password
        if (!passwordEncoder.matches(oldPassword, user.get().getPassword())) {
            return MessageResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Old password does not match")
                    .build();
        }

        // Update the user's password with the new password
        user.get().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user.get());

        return MessageResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Change password success")
                .build();
    }
}
