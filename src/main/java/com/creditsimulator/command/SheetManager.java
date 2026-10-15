package com.creditsimulator.command;

import com.creditsimulator.model.Calculation;
import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.view.ConsoleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Menyimpan dan mengelola daftar hasil kalkulasi (sheet).
 *
 * <p>Setiap kali user memilih "save", hasil kalkulasi terakhir
 * ditambahkan ke daftar dengan nama otomatis (Sheet 1, Sheet 2, dst).
 * User bisa melihat daftar dan memilih sheet mana yang ingin ditampilkan.
 */
public class SheetManager {

    private final List<Calculation> sheets = new ArrayList<>();
    private final ConsoleView view;

    public SheetManager(ConsoleView view) {
        this.view = view;
    }

    /** Menyimpan kalkulasi baru. */
    public void save(Calculation calculation) {
        sheets.add(calculation);
        view.println("Tersimpan sebagai Sheet " + sheets.size() + ".");
    }

    /** Menampilkan daftar ringkas semua sheet tersimpan. */
    public void showList() {
        if (sheets.isEmpty()) {
            view.println("Belum ada sheet tersimpan. Jalankan simulasi atau load terlebih dahulu.");
            return;
        }
        view.println("Sheet tersimpan (" + sheets.size() + "):");
        for (int i = 0; i < sheets.size(); i++) {
            LoanRequest r = sheets.get(i).request();
            view.println(String.format("  %d. %s %s %d | Pinjaman Rp %,.0f | Tenor %d thn",
                    i + 1,
                    capitalize(r.vehicleType().name()),
                    capitalize(r.vehicleCondition().name()),
                    r.vehicleYear(),
                    r.totalLoanAmount(),
                    r.tenorYears()));
        }
    }

    /**
     * Menampilkan daftar sheet, lalu mempersilakan user memilih nomor
     * untuk melihat detail hasil kalkulasinya.
     */
    public void showAndSelect() {
        showList();
        if (sheets.isEmpty()) return;

        view.println("");
        view.prompt("Pilih nomor sheet untuk ditampilkan (Enter untuk kembali)");
        String line = view.readLine();

        if (line == null || line.trim().isEmpty()) return;

        try {
            int number = Integer.parseInt(line.trim());
            if (number < 1 || number > sheets.size()) {
                view.showError("Nomor tidak valid. Pilih antara 1 sampai " + sheets.size());
            } else {
                view.println("");
                view.println("=== Sheet " + number + " ===");
                view.showResult(sheets.get(number - 1));
            }
        } catch (NumberFormatException e) {
            view.showError("Masukkan angka yang valid");
        }
    }

    public boolean isEmpty() {
        return sheets.isEmpty();
    }

    private static String capitalize(String text) {
        return text.charAt(0) + text.substring(1).toLowerCase(Locale.ROOT);
    }
}
