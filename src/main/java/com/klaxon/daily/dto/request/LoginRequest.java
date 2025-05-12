package com.klaxon.daily.dto.request;

import com.klaxon.daily.config.log.hidden.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank(message = "Nickname is required")
                           @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
                           @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Nickname can contain only letters, digits, and underscores")
                           String nickname,
                           @NotBlank(message = "Password is required")
                           @Hidden
                           String password
) {
}
