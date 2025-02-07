package dev.luanfernandes.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record TokenRequest(@NotBlank String username, @NotBlank String password) {}
