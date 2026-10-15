package com.creditsimulator.controller;

import com.creditsimulator.view.ConsoleView;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Mengelola command loop: menampilkan menu, membaca pilihan user,
 * dan mengembalikan perintah yang dipilih.
 */
public class CommandMenu {

    /** Semua perintah yang dikenali aplikasi. */
    public enum Command {
        SIMULASI, LOAD, SAVE, SHEETS, SHOW, KELUAR
    }

    private static final Map<String, String> DESCRIPTIONS = new LinkedHashMap<>();

    static {
        DESCRIPTIONS.put("simulasi", "Mulai simulasi kredit baru");
        DESCRIPTIONS.put("load",     "Load data dari server dan hitung otomatis");
        DESCRIPTIONS.put("save",     "Simpan hasil terakhir sebagai sheet");
        DESCRIPTIONS.put("sheets",   "Lihat dan pilih sheet tersimpan");
        DESCRIPTIONS.put("show",     "Tampilkan semua perintah");
        DESCRIPTIONS.put("keluar",   "Keluar dari aplikasi");
    }

    private final ConsoleView view;

    public CommandMenu(ConsoleView view) {
        this.view = view;
    }

    /**
     * Menampilkan daftar perintah, membaca input user, dan mengembalikan
     * {@link Command} yang dipilih. Tanya ulang jika input tidak dikenal.
     */
    public Command readCommand() {
        while (true) {
            view.println("");
            view.showCommands(DESCRIPTIONS);
            view.prompt("Perintah");    
            String line = view.readLine();

            if (line == null) return Command.KELUAR; // input habis (mode file)

            switch (line.trim().toLowerCase(Locale.ROOT)) {
                case "simulasi" -> { return Command.SIMULASI; }
                case "load"     -> { return Command.LOAD; }
                case "save"     -> { return Command.SAVE; }
                case "sheets"   -> { return Command.SHEETS; }
                case "show"     -> { return Command.SHOW; }
                case "keluar"   -> { return Command.KELUAR; }
                default -> view.showError("Perintah tidak dikenal: \"" + line.trim() + "\"");
            }
        }
    }

    /** Mencetak ulang daftar perintah (dipanggil ketika user mengetik "show"). */
    public void printCommands() {
        view.showCommands(DESCRIPTIONS);
    }
}
