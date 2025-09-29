package com.mikael.project.backend.model.dtos.fine;

public record UserFineStatsResponse(
        FineStatsResponse givenStats,
        FineStatsResponse receivedStats
){}