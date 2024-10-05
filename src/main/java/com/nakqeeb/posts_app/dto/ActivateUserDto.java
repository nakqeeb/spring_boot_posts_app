package com.nakqeeb.posts_app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActivateUserDto {

    @NotNull(message = "This field is required")
    private boolean activated;

}