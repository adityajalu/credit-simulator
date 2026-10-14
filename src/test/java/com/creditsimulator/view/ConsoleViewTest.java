package com.creditsimulator.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.creditsimulator.calculator.InstallmentCalculator;
import com.creditsimulator.input.ListInputSource;
import com.creditsimulator.model.Calculation;
import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.VehicleCondition;
import com.creditsimulator.model.VehicleType;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConsoleViewTest {

    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    private ConsoleView view(List<String> lines, boolean interactive) {
        return new ConsoleView(new ListInputSource(lines),
                new PrintStream(bytes, true, StandardCharsets.UTF_8), interactive);
    }

    private String output() {
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void showResultUsesSampleFormat() {
        LoanRequest request = new LoanRequest(VehicleType.MOBIL, VehicleCondition.BARU, 2025,
                new BigDecimal("100000000"), 3, new BigDecimal("25000000"));
        Calculation calculation =
                new Calculation(request, new InstallmentCalculator().calculate(request));

        view(List.of(), false).showResult(calculation);

        assertTrue(output().contains("tahun 1 : Rp. 2,250,000.00/bln , Suku Bunga : 8%"));
        assertTrue(output().contains("tahun 2 : Rp. 2,432,250.00/bln , Suku Bunga : 8,1%"));
        assertTrue(output().contains("tahun 3 : Rp. 2,641,423.50/bln , Suku Bunga : 8,6%"));
    }

    @Test
    void fileModeEchoesTheLinesItReads() {
        ConsoleView view = view(List.of("mobil"), false);
        assertEquals("mobil", view.readLine());
        assertTrue(output().contains("mobil"));
    }

    @Test
    void interactiveModeDoesNotEcho() {
        ConsoleView view = view(List.of("mobil"), true);
        assertEquals("mobil", view.readLine());
        assertEquals("", output());
    }
}