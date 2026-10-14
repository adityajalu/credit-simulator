package com.creditsimulator.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.creditsimulator.input.InputException;
import com.creditsimulator.input.ListInputSource;
import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.VehicleCondition;
import com.creditsimulator.model.VehicleType;
import com.creditsimulator.view.ConsoleView;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class PromptLoanRequestSourceTest {

    private static ConsoleView view(List<String> lines, boolean interactive,
                                    ByteArrayOutputStream bytes) {
        return new ConsoleView(new ListInputSource(lines),
                new PrintStream(bytes, true, StandardCharsets.UTF_8), interactive);
    }

    @Test
    void readsSixAnswersInOrder() {
        ConsoleView view = view(
                List.of("mobil", " BARU ", "2025", "1000000000", "6", "500000000"),
                false, new ByteArrayOutputStream());

        LoanRequest request = new PromptLoanRequestSource(view).read();

        assertEquals(VehicleType.MOBIL, request.vehicleType());
        assertEquals(VehicleCondition.BARU, request.vehicleCondition());
        assertEquals(2025, request.vehicleYear());
        assertEquals(0, new BigDecimal("1000000000").compareTo(request.totalLoanAmount()));
        assertEquals(6, request.tenorYears());
        assertEquals(0, new BigDecimal("500000000").compareTo(request.downPayment()));
    }

    @Test
    void interactiveModeAsksAgainOnBadInput() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ConsoleView view = view(
                List.of("truk", "motor", "baru", "abc", "2025", "100000000", "3", "35000000"),
                true, bytes);

        LoanRequest request = new PromptLoanRequestSource(view).read();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertEquals(VehicleType.MOTOR, request.vehicleType());
        assertEquals(2025, request.vehicleYear());
        assertTrue(output.contains("Error: Jenis kendaraan harus Motor atau Mobil"));
        assertTrue(output.contains("Error: Tahun kendaraan harus berupa angka bulat positif"));
    }

    @Test
    void fileModeStopsOnBadInput() {
        ConsoleView view = view(List.of("truk"), false, new ByteArrayOutputStream());
        assertThrows(InputException.class, () -> new PromptLoanRequestSource(view).read());
    }

    @Test
    void stopsWhenInputEndsEarly() {
        ConsoleView view = view(List.of("mobil", "baru"), false, new ByteArrayOutputStream());
        assertThrows(InputException.class, () -> new PromptLoanRequestSource(view).read());
    }

    @Test
    void rejectsThousandSeparators() {
        ConsoleView view = view(List.of("mobil", "baru", "2025", "1.000.000.000"),
                false, new ByteArrayOutputStream());
        assertThrows(InputException.class, () -> new PromptLoanRequestSource(view).read());
    }
}