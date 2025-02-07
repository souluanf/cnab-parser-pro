package dev.luanfernandes.domain.response;

public record ProcessFileResponse(int totalLinesFile, int totalProcessedLines, String timeProcessing) {}
