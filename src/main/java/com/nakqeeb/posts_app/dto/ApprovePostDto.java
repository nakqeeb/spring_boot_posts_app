package com.nakqeeb.posts_app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovePostDto {

    @NotNull(message = "This field is required")
    private boolean approved;

}
