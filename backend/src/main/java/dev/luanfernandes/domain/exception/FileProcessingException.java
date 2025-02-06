package dev.luanfernandes.domain.exception;

import dev.luanfernandes.domain.response.ErrorProcessing;
import java.io.Serial;
import java.util.List;
import lombok.Getter;

@Getter
public class FileProcessingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4899123440651808538L;

    private final List<ErrorProcessing> errors;

    public FileProcessingException(String message, List<ErrorProcessing> errors) {
        super(message);
        this.errors = errors;
    }
}
