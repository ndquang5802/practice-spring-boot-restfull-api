package com.store.api.practiceapi.controller;

import com.store.api.practiceapi.dto.RegisterDTO;
import com.store.api.practiceapi.model.User;
import com.store.api.practiceapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<User>> retrieveAllUsers() {
        return ResponseEntity.ok().body(userService.findAllUsers());
    }

    @PostMapping()
    public ResponseEntity<String> createUser(@RequestBody RegisterDTO request) {
        return ResponseEntity.ok(userService.saveUser(request));
    }
}
