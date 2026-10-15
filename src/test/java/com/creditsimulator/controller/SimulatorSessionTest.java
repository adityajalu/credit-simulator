package com.creditsimulator.controller;

import com.creditsimulator.view.ConsoleView;
import com.creditsimulator.input.ListInputSource;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorSessionTest {

    // Tahun dikunci ke 2026 supaya aturan tahun kendaraan tidak berubah tiap tahun
    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-10-14T00:00:00Z"), ZoneOffset.UTC);

    // ── Helper ──────────────────────────────────────────────────────────────
    private static ConsoleView view(List<String> lines, ByteArrayOutputStream out) {
        return new ConsoleView(
                new ListInputSource(lines),
                new PrintStream(out, true, StandardCharsets.UTF_8),
                /*interactive=*/ false);
    }

    private static String output(ByteArrayOutputStream out) {
        return out.toString(StandardCharsets.UTF_8);
    }

    private static SimulatorSession session(List<String> lines, ByteArrayOutputStream out) {
        ConsoleView view = view(lines, out);
        return new SimulatorSession(view, CLOCK, new PromptLoanRequestSource(view));
    }

    // ── Test 1: skenario sukses end-to-end ──────────────────────────────────
    @Test
    void successfulSimulationPrintsResult() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Mobil Baru 2025 | pinjaman 100jt | tenor 3
        // DP minimal untuk BARU = 35% x 100jt = 35jt
        boolean ok = session(
                List.of("Mobil", "Baru", "2025", "100000000", "3", "35000000"), out)
                .run().isPresent();

        assertTrue(ok, "run() harus mengembalikan nilai jika simulasi berhasil");
        String printed = output(out);
        assertTrue(printed.contains("Kendaraan"), "Harus mencetak baris kendaraan");
        assertTrue(printed.contains("tahun 1"), "Harus mencetak angsuran tahun 1");
    }

    // ── Test 2: data tidak lolos validasi → tampilkan error, return empty ───
    @Test
    void failedValidationPrintsErrorsAndReturnsEmpty() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // DP hanya 1 rupiah → tidak memenuhi minimal 35% untuk kendaraan baru
        boolean ok = session(
                List.of("Mobil", "Baru", "2025", "100000000", "3", "1"), out)
                .run().isPresent();

        assertFalse(ok, "run() harus mengembalikan empty jika validasi gagal");
        assertTrue(output(out).contains("Input tidak valid"),
                "Harus menampilkan pesan validasi gagal");
    }

    // ── Test 3: kendaraan baru bertahun lama → validasi gagal ───────────────
    @Test
    void vehicleTooOldForNewConditionIsRejected() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean ok = session(
                List.of("Mobil", "Baru", "2020", "100000000", "3", "35000000"), out)
                .run().isPresent();

        assertFalse(ok);
        assertTrue(output(out).contains("Kendaraan baru tidak boleh bertahun"));
    }

    // ── Test 4: input terpotong → tampilkan error, return empty ─────────────
    @Test
    void incompleteInputReturnsEmpty() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean ok = session(List.of("Mobil", "Baru"), out).run().isPresent();

        assertFalse(ok, "run() harus mengembalikan empty jika input terpotong");
        assertTrue(output(out).contains("Error"), "Harus menampilkan pesan error");
    }

    // ── Test 5: verifikasi angsuran bulan pertama tepat secara numerik ───────
    @Test
    void firstMonthlyInstallmentMatchesCalculation() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Pinjaman 200jt, DP 80jt → principal 120jt
        // Tahun 1 bunga 8% → 3.600.000/bln
        session(List.of("Mobil", "Baru", "2025", "200000000", "3", "80000000"), out).run();

        assertTrue(output(out).contains("3,600,000.00"),
                "Angsuran bulan pertama harus Rp. 3,600,000.00");
    }
}
