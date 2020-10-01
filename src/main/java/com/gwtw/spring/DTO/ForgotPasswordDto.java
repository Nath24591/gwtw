package com.gwtw.spring.DTO;

import com.sun.istack.NotNull;

public class ForgotPasswordDto {

    @NotNull
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
