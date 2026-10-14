package com.creditsimulator.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.StringReader;
import org.junit.jupiter.api.Test;

class ReaderInputSourceTest {

    @Test
    void stripsBomAndHandlesWindowsLineEndings() {
        ReaderInputSource source = new ReaderInputSource(new StringReader("\uFEFFmobil\r\nbaru\r\n"));
        assertEquals("mobil", source.nextLine());
        assertEquals("baru", source.nextLine());
        assertNull(source.nextLine());
    }
}