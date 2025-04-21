package com.klaxon.diary.dto.request;

import com.klaxon.diary.config.log.hidden.Hidden;

public record AuthRequest(String nickname, @Hidden String password) {}
