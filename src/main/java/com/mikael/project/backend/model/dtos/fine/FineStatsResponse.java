package com.mikael.project.backend.model.dtos.fine;

public record FineStatsResponse (
        Long amount,
        Integer count
){}