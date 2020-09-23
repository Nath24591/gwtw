package com.gwtw.spring.DTO;

import com.sun.istack.NotNull;

public class ResetPasswordDto {
    @NotNull
    private String password;
    @NotNull
    private String passwordConf;
    @NotNull
    private String token;

    public ResetPasswordDto(String token){
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConf() {
        return passwordConf;
    }

    public void setPasswordConf(String passwordConf) {
        this.passwordConf = passwordConf;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

