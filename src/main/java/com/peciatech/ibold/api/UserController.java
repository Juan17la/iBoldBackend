package com.peciatech.ibold.api;

import com.peciatech.ibold.api.dto.LoginRequest;
import com.peciatech.ibold.api.dto.LoginResponse;
import com.peciatech.ibold.api.dto.RegisterUserRequest;
import com.peciatech.ibold.api.dto.RegisterUserResponse;
import com.peciatech.ibold.domain.model.UserAccount;
import com.peciatech.ibold.exception.BadRequestException;
import com.peciatech.ibold.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest request) {
        UserAccount userAccount = userService.register(request);
        RegisterUserResponse response = new RegisterUserResponse(
                userAccount.getEmail(),
                userAccount.getName(),
                userAccount.getPlan(),
                userAccount.getCreatedAt(),
                "User registered successfully"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        UserAccount user = userService.login(request.email(), request.password());
        LoginResponse response = new LoginResponse(
                user.getEmail(),
                user.getName(),
                user.getPlan(),
                "Login successful"
        );
        return ResponseEntity.ok(response);
    }
}
