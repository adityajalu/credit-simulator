package com.creditsimulator.model;

import java.util.Locale;

public enum VehicleType {
    MOBIL, MOTOR;

    public static VehicleType parse(String input) {
        String value = input == null ? "" : input.trim().toUpperCase(Locale.ROOT);
        for (VehicleType type : values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Jenis Kendaraan harus Mobil atau Motor");
    }
}
