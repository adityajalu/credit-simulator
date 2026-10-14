package com.creditsimulator.view;

import com.creditsimulator.input.InputSource;
import com.creditsimulator.model.Calculation;
import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.YearlyInstallment;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConsoleView {

    private final InputSource input;
    private final PrintStream out;
    private final boolean interactive;

    public ConsoleView(InputSource input, PrintStream out, boolean interactive) {
        this.input = input;
        this.out = out;
        this.interactive = interactive;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public void println(String text) {
        out.println(text);
    }

    public void prompt(String question) {
        out.print(question + ": ");
        out.flush();
    }

    /** Membaca satu baris. Di mode file, baris yang dibaca ikut dicetak supaya log-nya terbaca. */
    public String readLine() {
        String line = input.nextLine();
        if (line != null && !interactive) {
            out.println(line);
        }
        return line;
    }

    public void showError(String message) {
        out.println("Error: " + message);
    }

    public void showErrors(List<String> messages) {
        out.println("Input tidak valid:");
        for (String message : messages) {
            out.println(" - " + message);
        }
    }

    public void showCommands(Map<String, String> commands) {
        out.println("Perintah yang tersedia:");
        commands.forEach((name, description) ->
                out.println(String.format("  %-10s %s", name, description)));
    }

    public void showResult(Calculation calculation) {
        LoanRequest r = calculation.request();
        out.println("Kendaraan : " + capitalize(r.vehicleType().name()) + " "
                + capitalize(r.vehicleCondition().name()) + " tahun " + r.vehicleYear());
        out.println("Pinjaman  : " + money(r.totalLoanAmount()) + " | DP : "
                + money(r.downPayment()) + " | Tenor : " + r.tenorYears() + " tahun");
        for (YearlyInstallment y : calculation.installments()) {
            out.println("tahun " + y.year() + " : " + money(y.monthlyInstallment())
                    + "/bln , Suku Bunga : " + percent(y.rate()));
        }
    }

    private static String capitalize(String text) {
        return text.charAt(0) + text.substring(1).toLowerCase(Locale.ROOT);
    }

    private static String money(BigDecimal amount) {
        return "Rp. " + String.format(Locale.US, "%,.2f", amount);
    }

    private static String percent(BigDecimal rate) {
        return rate.movePointRight(2).stripTrailingZeros().toPlainString().replace('.', ',') + "%";
    }
}