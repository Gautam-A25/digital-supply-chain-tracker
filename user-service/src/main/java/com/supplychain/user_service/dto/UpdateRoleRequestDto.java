package com.supplychain.user_service.dto;

import com.supplychain.user_service.enums.RoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequestDto {

    @NotNull
    private RoleEnum role;
}