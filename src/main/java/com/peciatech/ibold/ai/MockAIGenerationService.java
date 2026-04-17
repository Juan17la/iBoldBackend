package com.peciatech.ibold.ai;

import com.peciatech.ibold.api.dto.GenerationRequest;
import com.peciatech.ibold.api.dto.GenerationResponse;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service("coreAIGenerationService")
public class MockAIGenerationService implements AIGenerationService {

    private static final List<String> MOCK_TEXTS = List.of(
            "Este trabajo esta de 5.0 profe",
            "muchas lagrimas en el trabajo profe, se lo juro",
            "5.0 profe 5.0",
            "mas de 5.0 esta el trabajo, de mas tiempo",
            "muyt poco tiempo mucha ansiedad"
    );

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("AI simulation was interrupted");
        }

        String base = MOCK_TEXTS.get(ThreadLocalRandom.current().nextInt(MOCK_TEXTS.size()));
        int tokens = request.requestedTokens() == null ? 100 : request.requestedTokens();
        String generatedText = base + " Prompt: " + request.prompt();

        return new GenerationResponse(
            request.email(),
                generatedText,
                tokens,
                Instant.now(),
                "mock-text-generator"
        );
    }
}
