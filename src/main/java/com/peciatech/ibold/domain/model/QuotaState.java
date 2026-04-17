package com.peciatech.ibold.domain.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotaState {
    private String userId;
    private long usedTokens;
    private LocalDate resetDate;
}
