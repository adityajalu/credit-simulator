package com.creditsimulator.command;

import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.VehicleCondition;
import com.creditsimulator.model.VehicleType;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mengambil data pinjaman dari JSON web service dan
 * mengubahnya menjadi {@link LoanRequest}.
 *
 * <p>Tidak menggunakan library JSON eksternal — parsing dilakukan
 * dengan regex sederhana karena struktur JSON-nya sudah diketahui dan tetap.
 *
 * <p>Jika server tidak dapat dijangkau, akan digunakan data fallback
 * sehingga fitur tetap dapat didemonstrasikan.
 */
public class LoadCommand {

    private static final String API_URL =
            "https://run.mocky.io/v3/9108b1da-beec-409e-ae14-e8891955666c";

    /** JSON fallback dipakai saat server tidak dapat dijangkau. */
    private static final String FALLBACK_JSON =
            "{\"vehicleType\":\"Mobil\",\"vehicleCondition\":\"Baru\"," +
            "\"vehicleYear\":2025,\"totalLoanAmount\":200000000," +
            "\"loanTenure\":3,\"downPayment\":80000000}";

    /**
     * Menghubungi server, mengambil JSON, dan mengembalikan {@link LoanRequest}.
     * Bila server tidak dapat dijangkau, dikembalikan data fallback.
     */
    public LoanRequest fetch() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .sslContext(buildTrustAllSslContext())
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Data berhasil dimuat dari server.");
                return parseJson(response.body());
            }
            
            System.out.println("Server merespons " + response.statusCode()
                    + ". Menggunakan data contoh.");
        } catch (Exception e) {
            System.out.println("Server tidak dapat dijangkau (" + e.getMessage() + ").");
            System.out.println("Menggunakan data contoh sebagai gantinya.");
        }

        return parseJson(FALLBACK_JSON);
    }

    /**
     * Membuat SSLContext yang menerima semua certificate.
     * Diperlukan untuk kompatibilitas di Windows karena perbedaan trust store Java.
     */
    private static SSLContext buildTrustAllSslContext() throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] c, String a) { }
                public void checkServerTrusted(X509Certificate[] c, String a) { }
            }
        };
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustAll, null);
        return ctx;
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
