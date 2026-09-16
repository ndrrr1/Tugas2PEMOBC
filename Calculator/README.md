# Calculator — Java + RelativeLayout + XML onClick

Proyek kalkulator Android dengan mode standar dan scientific. Nama aplikasinya **Calculator**.
Seluruh logika aplikasi ditulis dalam Java. XML dipakai untuk tampilan; Gradle Groovy dipakai untuk konfigurasi build. Tidak ada Kotlin atau Jetpack Compose.

## Mulai di Windows

1. Ekstrak ZIP terlebih dahulu. Jangan membuka proyek dari dalam ZIP.
2. Masuk ke folder **Calculator**, lalu klik dua kali **PERSIAPAN_WINDOWS.bat**. Sambungkan internet. Script mengunduh Gradle Wrapper resmi 8.9 dan memeriksa SHA-256; cukup dilakukan sekali.
3. Buka Android Studio → **Open** → pilih folder **Calculator** yang berisi `settings.gradle`, `build.gradle`, dan folder `app`. Jangan memilih folder `app` saja.
4. Tunggu Gradle Sync. Jika Android Studio meminta pemasangan SDK, setujui unduhan **Android SDK Platform 35** dan **SDK Build-Tools 34.0.0** melalui SDK Manager.
5. Di Settings → Build, Execution, Deployment → Build Tools → Gradle, gunakan **Gradle JDK 17**. Jika belum ada, pilih Download JDK dan versi 17.
6. Pilih emulator atau HP Android (Android 6.0/API 23 atau lebih tinggi), kemudian klik **Run ▶**.

Gradle, Android SDK, dan plugin build perlu internet saat persiapan pertama. Setelah diinstal, aplikasi kalkulator berjalan offline. ZIP berisi source project dan bootstrap wrapper; file biner `gradle-wrapper.jar` diunduh oleh langkah 2, bukan disertakan dalam ZIP. Tidak perlu membuat project baru atau menyalin Java/XML satu per satu.

macOS/Linux: jalankan `sh setup-gradle.sh`, kemudian buka folder yang sama di Android Studio.

## Fitur

- Tambah, kurang, kali, bagi, desimal, kurung, persen, positif/negatif, AC, dan hapus satu karakter.
- Urutan operasi matematika: pangkat, kali/bagi, lalu tambah/kurang; kurung dapat mengubah urutan.
- Pratinjau hasil sebelum menekan `=`.
- Scientific: akar, kuadrat, pangkat bebas, faktorial, 1/x, sin/cos/tan, sin⁻¹/cos⁻¹/tan⁻¹, log basis 10, ln, eˣ, π, dan e.
- DEG (derajat) / RAD (radian).
- Ans (jawaban terakhir) dan memori MC, MR, M+, M−.
- 50 riwayat terbaru tersimpan di perangkat. Ketuk riwayat untuk memakai hasilnya lagi.
- Salin hasil ke clipboard.
- Tema gelap dan terang; pilihan tema, mode, memori, dan input disimpan.
- Ukuran tombol menyesuaikan lebar layar. Layar dapat digulir pada HP kecil, orientasi landscape, dan saat mode scientific dibuka.
- Pesan kesalahan untuk pembagian nol, akar negatif, log tidak valid, faktorial tidak valid, kurung tidak lengkap, dan angka di luar rentang.
- Tanpa iklan, akun, analytics, atau izin internet dalam aplikasi.

## Cara menggunakan fitur

| Kebutuhan | Tombol / urutan | Hasil |
|---|---|---|
| Operasi dasar | `2 + 3 × 4 =` | 14 |
| Kurung | `( 2 + 3 ) × 4 =` | 20 |
| Tambah persen | `200 + 10 % =` | 220 |
| Kurangi persen | `200 − 10 % =` | 180 |
| Persen dari angka | `200 × 10 % =` | 20 |
| Akar | `√ 81 ) =` | 9 |
| Kuadrat | `5 x² =` | 25 |
| Pangkat bebas | Scientific → `2 xʸ 8 =` | 256 |
| Faktorial | Scientific → `5 x! =` | 120 |
| Sinus | Scientific, DEG → `sin 30 ) =` | 0.5 |
| Radian | Scientific → ketuk DEG menjadi RAD → `sin π ÷ 2 ) =` | 1 |
| Log basis 10 | Scientific → `log 1000 ) =` | 3 |
| Memori | `25 M+`, lalu `AC`, kemudian `MR` | 25 |
| Jawaban terakhir | Selesaikan hitungan, lalu gunakan `Ans` pada ekspresi baru | Hasil sebelumnya |

Tombol fungsi (`sin`, `√`, `log`, dll.) membuka tanda kurung secara otomatis. Lengkapi nilai dan tekan `)` untuk menutupnya. `x²`, `1/x`, dan `±` bekerja pada operand terakhir. Tombol `DEG/RAD` ada dalam panel Scientific. Tekan tombol **Standar** untuk menutup panel itu.

Persen mengikuti perilaku praktis: `200 + 10%` menambahkan 10% dari 200; `200 × 10%` mengalikan 200 dengan 0,1. Aturan persen relatif berlaku pada operand persen tunggal setelah + atau −. Ekspresi kompleks seperti `200 + (10%)` mengikuti matematika biasa (= 200,1). Tombol desimal menggunakan titik.

Setelah `=`, angka baru memulai hitungan baru, sedangkan operator melanjutkan hasil. `=` berulang tidak mengulangi operasi terakhir. `AC` membersihkan input; memori hanya dibersihkan lewat `MC`.

## Sesuai ketentuan tugas

1. **Java**: `app/src/main/java/com/guin/calculator/` berisi `MainActivity.java`, `CalculatorState.java`, dan `ExpressionEvaluator.java`.
2. **RelativeLayout**: layout utama, kartu hasil, toolbar, keypad, panel scientific, dan panel riwayat menggunakan `RelativeLayout`. `ScrollView` / `HorizontalScrollView` hanya untuk kebutuhan menggulir.
3. **android:onClick**: tombol pada layout memakai `android:onClick="onButtonClick"`; tombol riwayat memakai `android:onClick="onHistoryResultClick"`. Handler Java berbentuk `public void ... (View view)`. Tidak ada pemasangan click listener lewat Java.
4. **Aplikasi sederhana sebagai dasar**: fitur scientific bersifat tambahan dan bisa disembunyikan.

## Struktur penting

| File | Fungsi |
|---|---|
| `app/src/main/java/com/guin/calculator/MainActivity.java` | Tampilan, handler tombol, tema, clipboard, dan riwayat |
| `app/src/main/java/com/guin/calculator/CalculatorState.java` | Pengaturan input, tanda negatif, memori, dan Ans |
| `app/src/main/java/com/guin/calculator/ExpressionEvaluator.java` | Parser dan perhitungan matematika |
| `app/src/main/res/layout/activity_main.xml` | Layout utama dan seluruh tombol standar/scientific |
| `app/src/main/res/layout/history_panel.xml` | Panel riwayat |
| `app/src/main/res/layout/history_row.xml` | Satu entri riwayat |
| `app/src/main/res/values/colors.xml` | Warna tema gelap |
| `app/src/main/res/values-notnight/colors.xml` | Warna tema terang |
| `app/src/main/res/drawable/` | Background tombol, kartu, dan ikon aplikasi |
| `tests/CalculatorTests.java` | Pemeriksaan mesin hitung dan alur input tanpa Android SDK |

## Membuat APK

Setelah Gradle Sync berhasil, pilih menu build APK yang tersedia di versi Android Studio kamu, atau jalankan dari terminal folder proyek:

Windows:
```bat
gradlew.bat assembleDebug
```

macOS/Linux:
```sh
./gradlew assembleDebug
```

APK debug akan berada di `app/build/outputs/apk/debug/app-debug.apk`. Paket ini **belum menyertakan APK**.

## Verifikasi dan batasan

Mesin hitung dan state input telah dikompilasi dengan Java 17 dan diuji dengan 76 pemeriksaan, termasuk urutan operasi, persen, trigonometri, memori, dan penolakan ekspresi tidak valid. XML dan hubungan resource diperiksa secara statis.

Build Android, instalasi APK, tampilan emulator, dan interaksi perangkat **belum dijalankan** di lingkungan pembuatan karena Android SDK/emulator tidak tersedia dan unduhan build dibatasi. Script persiapan unduhan perlu dijalankan pada komputer yang terkoneksi internet.

Operasi dasar memakai BigDecimal dengan presisi 16 digit; tampilan hasil dibulatkan ke 14 digit signifikan. Fungsi ilmiah memakai Math/double dan merupakan pendekatan numerik, bukan kalkulator simbolik. Faktorial menerima bilangan bulat 0–170. Batas eksponen hasil nonnol: −300 sampai 308. Tidak ada grafik fungsi, konversi satuan, atau kurs mata uang.

Untuk mengulang tes mesin hitung (JDK 17):
```sh
javac -d build-check app/src/main/java/com/guin/calculator/ExpressionEvaluator.java app/src/main/java/com/guin/calculator/CalculatorState.java tests/CalculatorTests.java
java -cp build-check CalculatorTests
```

## Jika ada kendala

- **Gradle Sync gagal**: pastikan internet aktif, mode Offline Gradle tidak menyala, dan SDK Platform 35 sudah terinstal.
- **SDK location not found**: atur lokasi SDK lewat SDK Manager / Project Structure. File `local.properties` dibuat khusus komputer kamu sehingga tidak disertakan.
- **JDK tidak kompatibel**: pilih JDK 17 di pengaturan Gradle.
- **Wrapper belum ditemukan**: jalankan `PERSIAPAN_WINDOWS.bat` atau `sh setup-gradle.sh`, lalu ulang Sync.
- **Checksum gagal**: jangan menjalankan file hasil unduhan yang gagal diverifikasi; periksa koneksi dan ulang persiapan.

## Referensi build

- [Kompatibilitas resmi AGP 8.7](https://developer.android.com/build/releases/agp-8-7-0-release-notes): Gradle 8.9, JDK 17, dan dukungan compile SDK 35.
- [Checksum resmi Gradle](https://gradle.org/release-checksums/): checksum wrapper dan distribusi Gradle 8.9 dipatok dalam proyek.
- [Referensi Android View](https://developer.android.com/reference/android/view/View): atribut View termasuk XML onClick.
