package com.devstack.SmartDine.dtos.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequestDto {

    private String email;
    private String otpCode;
    private String newPassword;
    private String confirmPassword;

}