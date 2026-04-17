package com.peciatech.ibold.api;

import com.peciatech.ibold.api.dto.QuotaHistoryResponse;
import com.peciatech.ibold.api.dto.QuotaStatusResponse;
import com.peciatech.ibold.api.dto.UpgradePlanRequest;
import com.peciatech.ibold.api.dto.UpgradePlanResponse;
import com.peciatech.ibold.domain.Plan;
import com.peciatech.ibold.domain.model.UserAccount;
import com.peciatech.ibold.exception.BadRequestException;
import com.peciatech.ibold.service.QuotaService;
import com.peciatech.ibold.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {
    private final QuotaService quotaService;
    private final UserService userService;

    public QuotaController(QuotaService quotaService, UserService userService) {
        this.quotaService = quotaService;
        this.userService = userService;
    }

    @GetMapping("/status")
    public ResponseEntity<QuotaStatusResponse> getStatus(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-Password") String password
    ) {
        UserAccount user = userService.authenticate(email, password);
        return ResponseEntity.ok(quotaService.getStatus(user.getEmail()));
    }

    @GetMapping("/history")
    public ResponseEntity<QuotaHistoryResponse> getHistory(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-Password") String password
    ) {
        UserAccount user = userService.authenticate(email, password);
        return ResponseEntity.ok(quotaService.getLast7DaysHistory(user.getEmail()));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<UpgradePlanResponse> upgradePlan(@RequestBody UpgradePlanRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        String email = requiredValue(request.email(), "email");
        String password = requiredValue(request.password(), "password");
        Plan targetPlan = request.targetPlan() == null ? Plan.PRO : request.targetPlan();

        UserAccount authenticatedUser = userService.authenticate(email, password);
        Plan previousPlan = authenticatedUser.getPlan();
        UserAccount updatedUser = userService.upgradePlan(email, targetPlan);

        return ResponseEntity.ok(new UpgradePlanResponse(
            updatedUser.getEmail(),
                previousPlan,
                updatedUser.getPlan(),
                "Plan upgraded successfully"
        ));
    }

    private String requiredValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Field '" + fieldName + "' is required");
        }
        return value.trim();
    }
}
