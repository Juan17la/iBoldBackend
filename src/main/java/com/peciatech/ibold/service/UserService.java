package com.peciatech.ibold.service;

import com.peciatech.ibold.api.dto.RegisterUserRequest;
import com.peciatech.ibold.domain.Plan;
import com.peciatech.ibold.domain.model.UserAccount;
import com.peciatech.ibold.exception.BadRequestException;
import com.peciatech.ibold.exception.ConflictException;
import com.peciatech.ibold.exception.NotFoundException;
import com.peciatech.ibold.exception.UnauthorizedException;
import com.peciatech.ibold.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserAccount register(RegisterUserRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required for user registration");
        }

        String email = normalizeEmail(request.email());
        String password = normalizeRequired(request.password(), "password");
        String name = normalizeRequired(request.name(), "name");
        Plan plan = request.plan() == null ? Plan.FREE : request.plan();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("User already exists with email: " + email);
        }

        UserAccount userAccount = new UserAccount(
                UUID.randomUUID().toString(),
                email,
                name,
                password,
                plan,
                Instant.now()
        );
        userRepository.save(userAccount);
        return userAccount;
    }

    public UserAccount authenticate(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedPassword = normalizeRequired(password, "password");

        UserAccount user = findByEmailOrThrow(normalizedEmail);
        if (!normalizedPassword.equals(user.getEncryptedPassword())) {
            throw new UnauthorizedException("Invalid password for email: " + normalizedEmail);
        }

        return user;
    }

    public UserAccount login(String email, String password) {
        return authenticate(email, password);
    }

    public UserAccount findByUserIdOrThrow(String userId) {
        String normalizedUserId = normalizeRequired(userId, "userId");
        return userRepository.findByUserId(normalizedUserId)
                .orElseThrow(() -> new NotFoundException("BAD 404: Element not found. User " + normalizedUserId + " was not found"));
    }

    public UserAccount findByEmailOrThrow(String email) {
        String normalizedEmail = normalizeEmail(email);
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("BAD 404: Element not found. User with email " + normalizedEmail + " was not found"));
    }

    public UserAccount upgradePlan(String email, Plan targetPlan) {
        UserAccount user = findByEmailOrThrow(email);
        Plan requestedTargetPlan = targetPlan == null ? Plan.PRO : targetPlan;

        if (requestedTargetPlan != Plan.PRO) {
            throw new BadRequestException("Only FREE -> PRO upgrade is currently supported");
        }

        if (user.getPlan() != Plan.FREE) {
            throw new ConflictException("Plan upgrade is only available for FREE users");
        }

        user.setPlan(Plan.PRO);
        userRepository.save(user);
        return user;
    }

    private String normalizeEmail(String email) {
        String normalizedEmail = normalizeRequired(email, "email").toLowerCase();
        if (!normalizedEmail.contains("@") || normalizedEmail.startsWith("@") || normalizedEmail.endsWith("@")) {
            throw new BadRequestException("Field 'email' must be a valid email address");
        }
        return normalizedEmail;
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Field '" + fieldName + "' is required");
        }
        return value.trim();
    }
}
