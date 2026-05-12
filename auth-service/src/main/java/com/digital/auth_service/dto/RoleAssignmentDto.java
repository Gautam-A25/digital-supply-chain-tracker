package com.digital.auth_service.dto;

import com.digital.auth_service.enums.RoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleAssignmentDto {

    @NotNull
    private Long userId;

    @NotNull
    private RoleEnum role;
}