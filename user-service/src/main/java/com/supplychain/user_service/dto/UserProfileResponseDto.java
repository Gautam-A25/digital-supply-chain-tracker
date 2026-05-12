package com.supplychain.user_service.dto;

import com.supplychain.user_service.enums.RoleEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDto {

    private Long userId;

    private String fullName;

    private String email;

    private String phone;

    private RoleEnum role;

    private Boolean isActive;

    private LocalDateTime createdAt;
}