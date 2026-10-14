package com.creditsimulator.model;

import java.util.Locale;

public enum VehicleCondition {
    BARU, BEKAS;

    public static VehicleCondition parse(String input) {
        String value = input == null ? "" : input.trim().toUpperCase(Locale.ROOT);
        for (VehicleCondition condition : values()) {
            if (condition.name().equals(value)) {
                return condition;
            } 
        }
        throw new IllegalArgumentException("Kondisi kendaraan harus Baru atau Bekas");
    }
}
