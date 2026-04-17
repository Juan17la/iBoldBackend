package com.peciatech.ibold.domain.model;

import com.peciatech.ibold.domain.Plan;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    private String userId;
    private String email;
    private String name;
    private String encryptedPassword;
    private Plan plan;
    private Instant createdAt;
}
