package com.peciatech.ibold.ai;

import com.peciatech.ibold.api.dto.GenerationRequest;
import com.peciatech.ibold.api.dto.GenerationResponse;

public interface AIGenerationService {
    GenerationResponse generate(GenerationRequest request);
}
