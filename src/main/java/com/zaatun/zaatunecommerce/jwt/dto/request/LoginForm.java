package com.zaatun.zaatunecommerce.jwt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {
    @NotBlank
    private String emailOrPhone;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

}