package dev.luanfernandes.domain.response;

import java.io.Serializable;

public record ErrorProcessing(int linha, String content, String reason) implements Serializable {}
