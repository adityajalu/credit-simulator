# Credit Simulator

Aplikasi console untuk menghitung simulasi cicilan kredit kendaraan bermotor.

---

## Persyaratan

- **Java 17** atau lebih baru
- **Apache Maven 3.6+**

Cek versi yang terinstal:
```sh
java -version
mvn -version
```

---

## Build (Kompilasi)

Jalankan perintah berikut di folder root project:

```sh
mvn package
```

Perintah ini akan:
1. Mengkompilasi kode
2. Menjalankan semua unit test (34 test)
3. Membuat file JAR di `target/credit-simulator-1.0.0.jar`

Untuk build tanpa menjalankan test:
```sh
mvn package -DskipTests
```

---

## Cara Menjalankan

### Mode Interaktif (ketik sendiri)

```sh
./credit_simulator
```
atau dari subfolder bin:
```sh
bin/credit_simulator
```

Aplikasi akan menampilkan menu dan menunggu inputan.

### Mode File (input dari file .txt)

```sh
./credit_simulator file_inputs.txt
```
atau:
```sh
bin/credit_simulator file_inputs.txt
```

Setiap baris di file adalah satu jawaban, dibaca dari atas ke bawah.

---

## Format File Input

Setiap baris adalah satu jawaban untuk satu pertanyaan, urut dari atas:

```
simulasi          ← perintah (simulasi/load/save/sheets/show/keluar)
Mobil             ← jenis kendaraan (Motor atau Mobil)
Baru              ← kondisi (Baru atau Bekas)
2025              ← tahun kendaraan (4 digit)
200000000         ← jumlah pinjaman total (maks 1 miliar, tanpa titik/koma)
3                 ← tenor dalam tahun (1–6)
80000000          ← jumlah DP
keluar            ← keluar dari aplikasi
```

File contoh sudah tersedia di `contoh_input.txt`.

---

## Perintah yang Tersedia

| Perintah   | Keterangan |
|------------|------------|
| `simulasi` | Mulai simulasi kredit baru (input manual) |
| `load`     | Load data dari server dan hitung otomatis |
| `save`     | Simpan hasil terakhir sebagai sheet |
| `sheets`   | Lihat dan pilih sheet tersimpan |
| `show`     | Tampilkan semua perintah |
| `keluar`   | Keluar dari aplikasi |

---

## Aturan Bisnis

1. Kendaraan **Baru** tidak boleh bertahun lebih dari `tahun sekarang - 1`
2. Tenor maksimal **6 tahun**
3. DP kendaraan **Baru** minimal **35%** dari jumlah pinjaman
4. DP kendaraan **Bekas** minimal **25%** dari jumlah pinjaman
5. Jumlah pinjaman maksimal **1 miliar**
6. Bunga naik **0,1%** setiap 1 tahun (tahun ganjil)
7. Bunga naik **0,5%** setiap 2 tahun (tahun genap)
8. Bunga dasar: **Mobil = 8%**, **Motor = 9%**

---

## Unit Test

Menjalankan semua test:
```sh
mvn test
```

Menjalankan satu kelas test tertentu:
```sh
mvn test -Dtest=LoanRequestValidatorTest
```

---

## Menjalankan dengan Docker

Build image:
```sh
docker build -t credit-simulator .
```

Jalankan mode interaktif:
```sh
docker run -it credit-simulator
```

Jalankan dengan file input:
```sh
docker run -i credit-simulator < contoh_input.txt
```

---

## Struktur Project

```
credit_simulator/          ← root project
├── credit_simulator       ← script executable (./credit_simulator)
├── bin/
│   └── credit_simulator   ← script executable (bin/credit_simulator)
├── contoh_input.txt       ← contoh file input
├── Dockerfile
├── pom.xml
└── src/
    ├── main/java/com/creditsimulator/
    │   ├── App.java                        ← entry point
    │   ├── calculator/                     ← logika kalkulasi angsuran
    │   ├── command/                        ← load dari API, manajemen sheet
    │   ├── controller/                     ← menu & session
    │   ├── input/                          ← sumber input (console/file)
    │   ├── model/                          ← data model (LoanRequest, dll.)
    │   ├── rate/                           ← kebijakan suku bunga
    │   ├── validation/                     ← aturan validasi
    │   └── view/                           ← tampilan console
    └── test/                               ← unit test
```


test cicd