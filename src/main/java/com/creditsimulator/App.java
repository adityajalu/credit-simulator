package com.creditsimulator;

import com.creditsimulator.command.LoadCommand;
import com.creditsimulator.command.SheetManager;
import com.creditsimulator.controller.CommandMenu;
import com.creditsimulator.controller.CommandMenu.Command;
import com.creditsimulator.controller.PromptLoanRequestSource;
import com.creditsimulator.controller.SimulatorSession;
import com.creditsimulator.input.InputSource;
import com.creditsimulator.input.ReaderInputSource;
import com.creditsimulator.model.Calculation;
import com.creditsimulator.view.ConsoleView;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Clock;
import java.util.Optional;

/**
 * Entry point aplikasi Credit Simulator.
 *
 * <p>Mode penggunaan:
 * <ul>
 *   <li><b>Interaktif</b>: {@code ./credit_simulator}
 *       — membaca dari System.in.</li>
 *   <li><b>File</b>: {@code ./credit_simulator file_inputs.txt}
 *       — membaca dari file, setiap baris yang dibaca ikut dicetak.</li>
 * </ul>
 */
public class App {

    public static void main(String[] args) throws IOException {
        // ── Setup input & view ─────────────────────────────────────────────
        boolean interactive;
        InputSource inputSource;

        if (args.length >= 1) {
            inputSource = new ReaderInputSource(new FileReader(args[0]));
            interactive = false;
        } else {
            inputSource = new ReaderInputSource(new InputStreamReader(System.in));
            interactive = true;
        }

        ConsoleView view  = new ConsoleView(inputSource, System.out, interactive);
        Clock        clock = Clock.systemDefaultZone();

        // ── Komponen utama ─────────────────────────────────────────────────
        CommandMenu  menu    = new CommandMenu(view);
        SheetManager sheets  = new SheetManager(view);
        LoadCommand  loader  = new LoadCommand();

        // Menyimpan hasil kalkulasi terakhir agar bisa di-save sebagai sheet
        Calculation lastCalc = null;

        view.println("=== Credit Simulator ===");

        // ── Command loop ───────────────────────────────────────────────────
        while (true) {
            Command cmd = menu.readCommand();

            switch (cmd) {

                case KELUAR -> {
                    view.println("Sampai jumpa!");
                    return;
                }

                case SHOW -> {
                    // Tampilkan ulang semua perintah yang tersedia
                    menu.printCommands();
                }

                case SIMULASI -> {
                    // Jalankan simulasi interaktif
                    PromptLoanRequestSource src = new PromptLoanRequestSource(view);
                    SimulatorSession session = new SimulatorSession(view, clock, src);
                    Optional<Calculation> result = session.run();
                    if (result.isPresent()) {
                        lastCalc = result.get();
                        view.println("(Ketik \"save\" untuk menyimpan hasil ini sebagai sheet.)");
                    }
                }

                case LOAD -> {
                    // Ambil data dari web service (atau fallback) lalu hitung otomatis
                    view.println("Menghubungi server...");
                    var request = loader.fetch();   // tidak throw — sudah ada fallback

                    SimulatorSession session =
                            new SimulatorSession(view, clock, () -> request);
                    Optional<Calculation> result = session.run();
                    if (result.isPresent()) {
                        lastCalc = result.get();
                        view.println("(Ketik \"save\" untuk menyimpan hasil ini sebagai sheet.)");
                    }
                }

                case SAVE -> {
                    // Simpan hasil kalkulasi terakhir ke daftar sheet
                    if (lastCalc == null) {
                        view.showError(
                                "Belum ada hasil yang bisa disimpan. " +
                                "Jalankan simulasi atau load terlebih dahulu.");
                    } else {
                        sheets.save(lastCalc);
                    }
                }

                case SHEETS -> {
                    // Tampilkan daftar sheet dan beri pilihan untuk melihat salah satunya
                    sheets.showAndSelect();
                }
            }
        }
    }
}
