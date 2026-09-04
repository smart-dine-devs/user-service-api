package com.devstack.SmartDine.dtos.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GitHubLoginRequestDto {
    @NotBlank(message = "Github Authentication Code is required")
    private String code;


}
