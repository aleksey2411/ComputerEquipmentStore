package com.yakubovskiy.project.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    ENABLE, BLOCKED, NOT_CONFIRMED;

    @JsonValue
    public String getStatus() {
        return this.name();
    }
}
