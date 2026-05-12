package com.digital.auth_service.dto;

import com.digital.auth_service.enums.RoleEnum;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String email;
    private Set<RoleEnum> roles;
}