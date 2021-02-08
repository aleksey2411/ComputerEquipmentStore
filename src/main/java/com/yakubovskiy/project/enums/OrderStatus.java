package com.yakubovskiy.project.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    UNDER_CONSIDERATION, DENIED, PRODUCED;

    @JsonValue
    public String getStatus() {
        return this.name();
    }
}
