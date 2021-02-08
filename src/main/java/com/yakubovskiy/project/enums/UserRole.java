package com.yakubovskiy.project.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMIN, USER;

    @JsonValue
    public String getStatus() {
        return this.name();
    }
}
