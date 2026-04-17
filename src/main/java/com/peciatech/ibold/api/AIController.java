package com.peciatech.ibold.api;

import com.peciatech.ibold.ai.AIGenerationService;
import com.peciatech.ibold.api.dto.GenerationRequest;
import com.peciatech.ibold.api.dto.GenerationResponse;
import com.peciatech.ibold.exception.BadRequestException;
import com.peciatech.ibold.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIGenerationService aiGenerationService;
    private final UserService userService;

    public AIController(AIGenerationService aiGenerationService, UserService userService) {
        this.aiGenerationService = aiGenerationService;
        this.userService = userService;
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerationResponse> generate(@RequestBody GenerationRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        String email = requiredValue(request.email(), "email");
        String password = requiredValue(request.password(), "password");
        String prompt = requiredValue(request.prompt(), "prompt");
        int requestedTokens = request.requestedTokens() == null ? 100 : request.requestedTokens();

        if (requestedTokens <= 0) {
            throw new BadRequestException("requestedTokens must be greater than zero");
        }

        userService.authenticate(email, password);

        GenerationRequest normalizedRequest = new GenerationRequest(
            email,
            password,
                prompt,
                requestedTokens
        );

        return ResponseEntity.ok(aiGenerationService.generate(normalizedRequest));
    }

    private String requiredValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Field '" + fieldName + "' is required");
        }
        return value.trim();
    }
}
