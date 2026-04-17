package com.peciatech.ibold.service;

import com.peciatech.ibold.api.dto.QuotaHistoryEntryResponse;
import com.peciatech.ibold.api.dto.QuotaHistoryResponse;
import com.peciatech.ibold.api.dto.QuotaStatusResponse;
import com.peciatech.ibold.domain.Plan;
import com.peciatech.ibold.domain.model.DailyUsageRecord;
import com.peciatech.ibold.domain.model.QuotaState;
import com.peciatech.ibold.exception.BadRequestException;
import com.peciatech.ibold.exception.QuotaExceededException;
import com.peciatech.ibold.repository.QuotaRepository;
import com.peciatech.ibold.repository.UsageHistoryRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class QuotaService {
    private final QuotaRepository quotaRepository;
    private final UsageHistoryRepository usageHistoryRepository;
    private final UserService userService;

    public QuotaService(
            QuotaRepository quotaRepository,
            UsageHistoryRepository usageHistoryRepository,
            UserService userService
    ) {
        this.quotaRepository = quotaRepository;
        this.usageHistoryRepository = usageHistoryRepository;
        this.userService = userService;
    }

    public void consumeTokens(String email, int tokensToConsume) {
        if (tokensToConsume <= 0) {
            throw new BadRequestException("requestedTokens must be greater than zero");
        }

        String normalizedEmail = normalizeEmail(email);
        String internalUserId = userService.findByEmailOrThrow(normalizedEmail).getUserId();
        Plan plan = userService.findByUserIdOrThrow(internalUserId).getPlan();
        QuotaState quotaState = getOrCreateState(internalUserId);

        if (!LocalDate.now().isBefore(quotaState.getResetDate())) {
            quotaState.setUsedTokens(0L);
            quotaState.setResetDate(nextResetDate());
        }

        if (!plan.hasUnlimitedTokens()) {
            long available = plan.getMonthlyTokens() - quotaState.getUsedTokens();
            if (tokensToConsume > available) {
                throw new QuotaExceededException(
                        "Monthly token quota exhausted for plan " + plan.name(),
                        Map.of(
                                "availableTokens", available,
                                "requestedTokens", tokensToConsume,
                                "monthlyLimit", plan.getMonthlyTokens()
                        )
                );
            }
        }

        quotaState.setUsedTokens(quotaState.getUsedTokens() + tokensToConsume);
        quotaRepository.save(quotaState);
        usageHistoryRepository.addTokens(internalUserId, LocalDate.now(), tokensToConsume);
    }

    public QuotaStatusResponse getStatus(String email) {
        String normalizedEmail = normalizeEmail(email);
        String internalUserId = userService.findByEmailOrThrow(normalizedEmail).getUserId();
        Plan plan = userService.findByUserIdOrThrow(internalUserId).getPlan();
        QuotaState quotaState = getOrCreateState(internalUserId);

        if (!LocalDate.now().isBefore(quotaState.getResetDate())) {
            quotaState.setUsedTokens(0L);
            quotaState.setResetDate(nextResetDate());
            quotaRepository.save(quotaState);
        }

        Long remainingTokens = plan.hasUnlimitedTokens()
                ? null
                : Math.max(0, plan.getMonthlyTokens() - quotaState.getUsedTokens());

        return new QuotaStatusResponse(
                normalizedEmail,
                plan,
                quotaState.getUsedTokens(),
                remainingTokens,
                quotaState.getResetDate()
        );
    }

    public QuotaHistoryResponse getLast7DaysHistory(String email) {
        String normalizedEmail = normalizeEmail(email);
        String internalUserId = userService.findByEmailOrThrow(normalizedEmail).getUserId();

        Map<LocalDate, Long> usageByDate = usageHistoryRepository.findByUserId(internalUserId).stream()
                .collect(Collectors.toMap(DailyUsageRecord::getDate, DailyUsageRecord::getUsedTokens, Long::sum));

        LocalDate today = LocalDate.now();
        List<QuotaHistoryEntryResponse> entries = new ArrayList<>();
        for (int daysBack = 6; daysBack >= 0; daysBack--) {
            LocalDate date = today.minusDays(daysBack);
            entries.add(new QuotaHistoryEntryResponse(date, usageByDate.getOrDefault(date, 0L)));
        }

        entries.sort(Comparator.comparing(QuotaHistoryEntryResponse::date));
        return new QuotaHistoryResponse(normalizedEmail, entries);
    }

    public void resetMonthlyQuotas() {
        List<QuotaState> states = quotaRepository.findAll();
        LocalDate nextResetDate = nextResetDate();
        for (QuotaState state : states) {
            state.setUsedTokens(0L);
            state.setResetDate(nextResetDate);
        }
        quotaRepository.saveAll(states);
    }

    private QuotaState getOrCreateState(String userId) {
        return quotaRepository.findByUserId(userId)
                .orElseGet(() -> {
                    QuotaState state = new QuotaState(userId, 0L, nextResetDate());
                    quotaRepository.save(state);
                    return state;
                });
    }

    private LocalDate nextResetDate() {
        return LocalDate.now().withDayOfMonth(1).plusMonths(1);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Field 'email' is required");
        }
        return email.trim().toLowerCase();
    }
}
