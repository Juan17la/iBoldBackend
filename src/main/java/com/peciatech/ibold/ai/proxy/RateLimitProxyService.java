package com.peciatech.ibold.ai.proxy;

import com.peciatech.ibold.ai.AIGenerationService;
import com.peciatech.ibold.api.dto.GenerationRequest;
import com.peciatech.ibold.api.dto.GenerationResponse;
import com.peciatech.ibold.service.RateLimitService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service("rateLimitProxyService")
public class RateLimitProxyService implements AIGenerationService {
    private final AIGenerationService delegate;
    private final RateLimitService rateLimitService;

    public RateLimitProxyService(
            @Qualifier("quotaProxyService") AIGenerationService delegate,
            RateLimitService rateLimitService
    ) {
        this.delegate = delegate;
        this.rateLimitService = rateLimitService;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        rateLimitService.assertWithinLimit(request.email());
        return delegate.generate(request);
    }
}
