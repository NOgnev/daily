package com.klaxon.daily.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record Summary(LocalDate date, String summary) {}
