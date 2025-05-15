package com.klaxon.daily.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;

@Builder
public record DialogItem(int id, Role role, Type type, String content) {
    public enum Role {
        SYSTEM,
        ASSISTANT,
        USER;

        @JsonValue
        public String toJson() {
            return name().toLowerCase();
        }

        @JsonCreator
        public static Role fromString(String value) {
            return Role.valueOf(value.toUpperCase());
        }
    }

    public enum Type {
        QUESTION,
        ANSWER,
        FINAL;

        @JsonValue
        public String toJson() {
            return name().toLowerCase();
        }

        @JsonCreator
        public static Type fromString(String value) {
            return Type.valueOf(value.toUpperCase());
        }
    }
}
