package com.creditsimulator.controller;

import com.creditsimulator.input.InputException;
import com.creditsimulator.input.LoanRequestSource;
import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.VehicleCondition;
import com.creditsimulator.model.VehicleType;
import com.creditsimulator.view.ConsoleView;
import java.math.BigDecimal;
import java.util.function.Function;

public class PromptLoanRequestSource implements LoanRequestSource {

    private final ConsoleView view;

    public PromptLoanRequestSource(ConsoleView view) {
        this.view = view;
    }

    @Override
    public LoanRequest read() {
        VehicleType type = ask("Jenis kendaraan (Motor/Mobil)", VehicleType::parse);
        VehicleCondition condition = ask("Kondisi kendaraan (Baru/Bekas)", VehicleCondition::parse);
        int year = ask("Tahun kendaraan (4 digit)", text -> parseInt(text, "Tahun kendaraan"));
        BigDecimal loan = ask("Jumlah pinjaman total (maks 1 miliar)",
                text -> parseAmount(text, "Jumlah pinjaman"));
        int tenor = ask("Tenor pinjaman (1-6 tahun)", text -> parseInt(text, "Tenor"));
        BigDecimal downPayment = ask("Jumlah DP", text -> parseAmount(text, "Jumlah DP"));
        return new LoanRequest(type, condition, year, loan, tenor, downPayment);
    }

    /**
     * Mengajukan satu pertanyaan. Interaktif: tanya ulang kalau salah. Mode file:
     * langsung gagal.
     */
    private <T> T ask(String question, Function<String, T> parser) {
        while (true) {
            view.prompt(question);
            String line = view.readLine();
            if (line == null) {
                throw new InputException("Input berakhir sebelum semua data terisi");
            }
            try {
                return parser.apply(line.trim());
            } catch (IllegalArgumentException e) {
                if (!view.isInteractive()) {
                    throw new InputException(e.getMessage());
                }
                view.showError(e.getMessage());
            }
        }
    }

    private static int parseInt(String text, String label) {
        if (!text.matches("\\d{1,9}")) {
            throw new IllegalArgumentException(label + " harus berupa angka bulat positif");
        }
        return Integer.parseInt(text);
    }

    private static BigDecimal parseAmount(String text, String label) {
        if (!text.matches("\\d+(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException(
                    label + " harus berupa angka tanpa pemisah ribuan, contoh: 100000000");
        }
        return new BigDecimal(text);
    }
}