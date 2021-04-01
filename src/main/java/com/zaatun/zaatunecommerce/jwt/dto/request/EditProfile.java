package com.zaatun.zaatunecommerce.jwt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EditProfile {

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    private String address;

    private String dateOfBirth;

    private String thana;

    private String district;

    @Size(max = 50)
    @Email
    private String email;

    @Size(min = 11, max = 11)
    String phoneNo;

}
