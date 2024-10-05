package com.nakqeeb.posts_app.dto;

import com.nakqeeb.posts_app.entity.RoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleDto {

    @NotNull(message = "Role is required")
    private String role;
//    private RoleEnum role;

}
