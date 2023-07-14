package com.store.api.practiceapi.controller;

import com.store.api.practiceapi.service.JwtService;
import com.store.api.practiceapi.service.LogoutService;
import com.store.api.practiceapi.dto.ChangePasswordDTO;
import com.store.api.practiceapi.response.MessageResponse;
import com.store.api.practiceapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {
    private final JwtService jwtService;
    private final UserService userService;
    private final LogoutService logoutService;

    @GetMapping("/home")
    public ResponseEntity<MessageResponse> home() {
        MessageResponse messageResponse = MessageResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Home page")
                .build();
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordDTO request) {
        // Validate JWT token
        String jwt = token.substring(7); // Remove "Bearer " prefix
        if (!jwtService.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Extract user information
        String username = jwtService.getUsernameFromToken(jwt);
        // You can also extract the user ID if needed: String userId = tokenProvider.getUserIdFromToken(jwt);
        return ResponseEntity.ok(userService.changePassword(username, request.getOldPassword(), request.getNewPassword()));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        logoutService.logout(request, response, null);
        response.setStatus(HttpServletResponse.SC_OK);
        MessageResponse messageResponse = MessageResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Logout successful")
                .build();
        return ResponseEntity.ok(messageResponse);
    }
}
