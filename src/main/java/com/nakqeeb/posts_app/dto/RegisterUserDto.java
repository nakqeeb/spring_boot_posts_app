package com.nakqeeb.posts_app.dto;

public class RegisterUserDto {
    private String email;
    private String password;
    private String name;

    public String getEmail() {
        return email;
    }

    public RegisterUserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return name;
    }

    public RegisterUserDto setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "RegisterUserDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}