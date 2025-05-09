package com.klaxon.diary.dto.request;

import com.klaxon.diary.config.log.hidden.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank(message = "Nickname is required")
                              @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
                              @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Nickname can contain only letters, digits, and underscores")
                              String nickname,
                              @NotBlank(message = "Password is required")
                              @Size(min = 8, message = "Password must be at least 8 characters long")
                              @Pattern(
                                      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
                                      message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)"
                              )
                              @Hidden
                              String password
) {
}
