package com.creditsimulator.command;

import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.VehicleCondition;
import com.creditsimulator.model.VehicleType;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mengambil data pinjaman dari JSON web service dan
 * mengubahnya menjadi {@link LoanRequest}.
 *
 * <p>Tidak menggunakan library JSON eksternal — parsing dilakukan
 * dengan regex sederhana karena struktur JSON-nya sudah diketahui dan tetap.
 */
public class LoadCommand {

    private static final String API_URL =
            "https://run.mocky.io/v3/9108b1da-beec-409e-ae14-e8891955666c";

    /**
     * Menghubungi server, mengambil JSON, dan mengembalikan {@link LoanRequest}.
     *
     * @throws Exception jika koneksi gagal atau format JSON tidak sesuai
     */
    public LoanRequest fetch() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Server merespons dengan status: " + response.statusCode());
        }

        return parseJson(response.body());
    }

    // ── JSON parsing sederhana tanpa library eksternal ────────────────────

    private static LoanRequest parseJson(String json) {
        String vehicleType      = readString(json, "vehicleType");
        String vehicleCondition = readString(json, "vehicleCondition");
        int    vehicleYear      = (int) readLong(json, "vehicleYear");
        BigDecimal totalLoan    = BigDecimal.valueOf(readLong(json, "totalLoanAmount"));
        int    tenor            = (int) readLong(json, "loanTenure");
        BigDecimal downPayment  = BigDecimal.valueOf(readLong(json, "downPayment"));

        return new LoanRequest(
                VehicleType.parse(vehicleType),
                VehicleCondition.parse(vehicleCondition),
                vehicleYear,
                totalLoan,
                tenor,
                downPayment);
    }

    /** Mengambil nilai string dari JSON: "key": "value" */
    private static String readString(String json, String key) {
        Matcher m = Pattern.compile(
                "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        if (!m.find()) throw new RuntimeException("Field tidak ada di JSON: " + key);
        return m.group(1);
    }

    /** Mengambil nilai angka dari JSON: "key": 12345 */
    private static long readLong(String json, String key) {
        Matcher m = Pattern.compile(
                "\"" + key + "\"\\s*:\\s*(\\d+)").matcher(json);
        if (!m.find()) throw new RuntimeException("Field tidak ada di JSON: " + key);
        return Long.parseLong(m.group(1));
    }
}
