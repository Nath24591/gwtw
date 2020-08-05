package com.gwtw.spring.DTO;

import com.sun.istack.NotNull;

public class LoginDto {

    @NotNull
    private String password;
    private String matchingPassword;

    @NotNull
    private String email;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
