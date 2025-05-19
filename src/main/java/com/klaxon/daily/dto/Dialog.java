package com.klaxon.daily.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record Dialog(UUID userId, LocalDate date, List<DialogItem> messages, Status status) {
    public enum Status {
        IN_PROGRESS,
        FINISHED,
        ERROR;

        @JsonValue
        public String toJson() {
            return name().toLowerCase();
        }

        @JsonCreator
        public static Status fromString(String value) {
            return Status.valueOf(value.toUpperCase());
        }
    }
}
