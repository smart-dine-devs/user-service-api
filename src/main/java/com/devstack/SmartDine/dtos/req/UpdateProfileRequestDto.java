package com.devstack.SmartDine.dtos.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequestDto {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePictureUrl; // direct image -> s3 -> link -> API

}