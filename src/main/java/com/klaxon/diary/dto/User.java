package com.klaxon.diary.dto;

import java.util.UUID;

public record User(UUID id, String nickname, String password) {}
