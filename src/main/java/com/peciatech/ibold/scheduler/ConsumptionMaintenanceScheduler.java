package com.peciatech.ibold.scheduler;

import com.peciatech.ibold.service.QuotaService;
import com.peciatech.ibold.service.RateLimitService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ConsumptionMaintenanceScheduler {
    private final RateLimitService rateLimitService;
    private final QuotaService quotaService;

    public ConsumptionMaintenanceScheduler(RateLimitService rateLimitService, QuotaService quotaService) {
        this.rateLimitService = rateLimitService;
        this.quotaService = quotaService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void resetRateLimitWindow() {
        rateLimitService.resetAllLimits();
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyQuotas() {
        quotaService.resetMonthlyQuotas();
    }
}
