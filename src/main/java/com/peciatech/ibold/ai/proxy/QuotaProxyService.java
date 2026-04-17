package com.peciatech.ibold.ai.proxy;

import com.peciatech.ibold.ai.AIGenerationService;
import com.peciatech.ibold.api.dto.GenerationRequest;
import com.peciatech.ibold.api.dto.GenerationResponse;
import com.peciatech.ibold.service.QuotaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("quotaProxyService")
public class QuotaProxyService implements AIGenerationService {
    private final AIGenerationService delegate;
    private final QuotaService quotaService;

    public QuotaProxyService(
            @Qualifier("coreAIGenerationService") AIGenerationService delegate,
            QuotaService quotaService
    ) {
        this.delegate = delegate;
        this.quotaService = quotaService;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        int requestedTokens = request.requestedTokens() == null ? 100 : request.requestedTokens();
        quotaService.consumeTokens(request.email(), requestedTokens);

        GenerationRequest normalizedRequest = new GenerationRequest(
            request.email(),
            request.password(),
                request.prompt(),
                requestedTokens
        );
        return delegate.generate(normalizedRequest);
    }
}
