package com.creditsimulator.controller;

import com.creditsimulator.controller.CommandMenu.Command;
import com.creditsimulator.input.ListInputSource;
import com.creditsimulator.view.ConsoleView;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandMenuTest {

    private static ConsoleView view(List<String> lines, ByteArrayOutputStream out) {
        return new ConsoleView(
                new ListInputSource(lines),
                new PrintStream(out, true, StandardCharsets.UTF_8),
                /*interactive=*/ false);
    }

    private static String output(ByteArrayOutputStream out) {
        return out.toString(StandardCharsets.UTF_8);
    }

    // ── Test: setiap perintah valid dikembalikan dengan benar ─────────────
    @Test
    void returnsSimulasiCommand() {
        CommandMenu menu = new CommandMenu(view(List.of("simulasi"), new ByteArrayOutputStream()));
        assertEquals(Command.SIMULASI, menu.readCommand());
    }

    @Test
    void returnsLoadCommand() {
        CommandMenu menu = new CommandMenu(view(List.of("load"), new ByteArrayOutputStream()));
        assertEquals(Command.LOAD, menu.readCommand());
    }

    @Test
    void returnsSaveCommand() {
        CommandMenu menu = new CommandMenu(view(List.of("save"), new ByteArrayOutputStream()));
        assertEquals(Command.SAVE, menu.readCommand());
    }

    @Test
    void returnsSheetsCommand() {
        CommandMenu menu = new CommandMenu(view(List.of("sheets"), new ByteArrayOutputStream()));
        assertEquals(Command.SHEETS, menu.readCommand());
    }

    @Test
    void returnsShowCommand() {
        CommandMenu menu = new CommandMenu(view(List.of("show"), new ByteArrayOutputStream()));
        assertEquals(Command.SHOW, menu.readCommand());
    }

    @Test
    void returnsKeluarCommand() {
        CommandMenu menu = new CommandMenu(view(List.of("keluar"), new ByteArrayOutputStream()));
        assertEquals(Command.KELUAR, menu.readCommand());
    }

    // ── Test: input tidak peka huruf besar/kecil ──────────────────────────
    @Test
    void isCaseInsensitive() {
        CommandMenu menu = new CommandMenu(view(List.of("SIMULASI"), new ByteArrayOutputStream()));
        assertEquals(Command.SIMULASI, menu.readCommand());
    }

    // ── Test: perintah tidak dikenal → tampilkan error lalu baca lagi ─────
    @Test
    void showsErrorForUnknownCommandThenReadsAgain() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CommandMenu menu = new CommandMenu(view(List.of("abc", "keluar"), out));

        Command cmd = menu.readCommand();

        assertEquals(Command.KELUAR, cmd);
        assertTrue(output(out).contains("Perintah tidak dikenal"));
    }

    // ── Test: input habis → anggap KELUAR ─────────────────────────────────
    @Test
    void returnsKeluarWhenInputIsExhausted() {
        CommandMenu menu = new CommandMenu(view(List.of(), new ByteArrayOutputStream()));
        assertEquals(Command.KELUAR, menu.readCommand());
    }

    // ── Test: menu selalu menampilkan daftar perintah ─────────────────────
    @Test
    void alwaysShowsCommandList() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new CommandMenu(view(List.of("keluar"), out)).readCommand();

        String printed = output(out);
        assertTrue(printed.contains("simulasi"));
        assertTrue(printed.contains("load"));
        assertTrue(printed.contains("save"));
        assertTrue(printed.contains("sheets"));
        assertTrue(printed.contains("show"));
        assertTrue(printed.contains("keluar"));
    }
}
