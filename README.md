# Tugas 2 PEMOB C 5027251124
# 🧮 Calculator Android Application

![Android](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Java-orange)
![UI](https://img.shields.io/badge/UI-XML%20RelativeLayout-blue)
![Build](https://img.shields.io/badge/Build-Gradle-purple)

## 📌 Overview

**Calculator** adalah aplikasi kalkulator Android yang dikembangkan menggunakan **Java** sebagai bahasa pemrograman utama dan **XML Layout** untuk desain antarmuka.

Aplikasi ini dibuat dengan pendekatan native Android tanpa menggunakan Kotlin maupun Jetpack Compose. Seluruh logika perhitungan dibuat secara mandiri menggunakan Java dengan dukungan sistem evaluasi ekspresi matematika.

Project ini mendukung mode:

* Standard Calculator
* Scientific Calculator
* Memory Calculator
* Calculation History
* Dark & Light Theme

Aplikasi dirancang agar ringan, dapat berjalan offline, dan tidak membutuhkan akun pengguna maupun koneksi internet.

---

# ✨ Features

## 🔢 Basic Calculator

Mendukung operasi matematika dasar:

* Addition (+)
* Subtraction (-)
* Multiplication (×)
* Division (÷)
* Decimal numbers
* Parentheses
* Percentage (%)
* Positive/Negative conversion (±)
* Clear (AC)
* Delete last character (⌫)

Contoh:

```
2 + 3 × 4 = 14
```

Sistem mengikuti aturan prioritas matematika:

1. Parentheses
2. Power
3. Multiplication / Division
4. Addition / Subtraction

---

# 🔬 Scientific Calculator

Mode scientific menyediakan fungsi matematika lanjutan:

### Trigonometry

* sin
* cos
* tan
* sin⁻¹
* cos⁻¹
* tan⁻¹

Mendukung:

* Degree (DEG)
* Radian (RAD)

Contoh:

```
sin(30°) = 0.5
```

---

### Advanced Mathematics

Fitur tambahan:

* Square root (√)
* Square (x²)
* Power (xʸ)
* Factorial (x!)
* Reciprocal (1/x)
* Logarithm base 10
* Natural logarithm (ln)
* Exponential (eˣ)
* Constant π
* Constant e

Contoh:

```
5! = 120

2⁸ = 256

log(1000) = 3
```

---

# 🧠 Smart Calculation Features

## Preview Result

Aplikasi dapat menampilkan hasil sementara sebelum tombol `=` ditekan.

Contoh:

Input:

```
10 + 5 × 2
```

Preview:

```
20
```

---

## Ans Function

Menyimpan hasil perhitungan terakhir.

Contoh:

```
5 × 5 = 25

Ans + 10

Result:
35
```

---

## Memory Function

Mendukung kalkulator memory:

| Button | Function        |
| ------ | --------------- |
| MC     | Clear Memory    |
| MR     | Recall Memory   |
| M+     | Add Memory      |
| M−     | Subtract Memory |

Contoh:

```
20 M+

AC

MR

Result:
20
```

---

# 📜 Calculation History

Aplikasi menyimpan hingga:

```
50 calculation records
```

Fitur:

* Melihat riwayat perhitungan
* Menggunakan kembali hasil sebelumnya
* Menghapus data history
* Penyimpanan lokal perangkat

---

# 🎨 User Interface

## Layout

Menggunakan:

* XML Layout
* RelativeLayout
* Custom Drawable

Komponen UI:

* Custom calculator button
* Scientific panel
* History panel
* Theme selector

## Responsive Design

Aplikasi mendukung:

* Smartphone kecil
* Landscape orientation
* Scientific mode scrolling
* Dynamic button sizing

---

# 🌙 Theme Support

Tersedia dua mode tampilan:

## Light Mode

Tampilan terang untuk penggunaan normal.

## Dark Mode

Tampilan gelap untuk kenyamanan penggunaan malam hari.

Pengaturan theme disimpan secara otomatis.

---

# 🛡️ Privacy

Aplikasi tidak menggunakan:

✅ Internet Permission
✅ Advertisement
✅ Analytics Tracking
✅ User Account
✅ External Server

Semua data:

* History
* Memory
* Preferences

disimpan secara lokal pada perangkat.

---

# 🏗️ Project Structure

```
Calculator
│
├── app
│   ├── src
│   │   ├── main
│   │   │
│   │   ├── java
│   │   │   └── com.guin.calculator
│   │   │       ├── MainActivity.java
│   │   │       ├── CalculatorState.java
│   │   │       └── ExpressionEvaluator.java
│   │   │
│   │   └── res
│   │       ├── layout
│   │       ├── drawable
│   │       └── values
│
├── build.gradle
├── settings.gradle
└── gradle
```

---

# ⚙️ Technology Stack

| Component            | Technology                  |
| -------------------- | --------------------------- |
| Programming Language | Java                        |
| UI Design            | XML                         |
| Build System         | Gradle                      |
| Android Plugin       | Android Gradle Plugin 8.7.3 |
| Compile SDK          | Android API 35              |
| Minimum SDK          | API 23                      |
| Target SDK           | API 35                      |
| Java Version         | Java 17                     |

---

# 📋 Requirements

Sebelum menjalankan project:

## Software

Install:

* Android Studio
* Android SDK Platform 35
* Gradle JDK 17

## Device Requirement

Minimum:

```
Android 6.0 (API 23)
```

Recommended:

```
Android 10+
```

---

# 🚀 Installation Guide

## Windows

### 1. Extract Project

Jangan membuka project langsung dari ZIP.

Extract terlebih dahulu:

```
Calculator.zip
```

---

### 2. Setup Gradle

Masuk folder:

```
Calculator
```

Jalankan:

```
PERSIAPAN_WINDOWS.bat
```

Script akan:

* Download Gradle Wrapper
* Memeriksa SHA-256
* Menyiapkan environment build

---

### 3. Open Android Studio

Pilih:

```
File
 ↓
Open
 ↓
Calculator folder
```

Jangan membuka:

```
Calculator/app
```

---

### 4. Gradle Sync

Tunggu proses:

```
Gradle Sync
```

Jika diminta:

Install:

```
Android SDK Platform 35
Build Tools 34.0.0
```

---

### 5. Run Application

Pilih:

* Emulator
  atau
* Android Device

Klik:

```
Run ▶
```

---

# 🖥️ macOS / Linux

Jalankan:

```bash
sh setup-gradle.sh
```

Kemudian buka folder project melalui Android Studio.

---

# 🧪 Testing

Project menyediakan unit test:

```
tests/
└── CalculatorTests.java
```

Testing mencakup:

* Basic arithmetic
* Operator priority
* Expression evaluation
* Error handling

---

# ⚠️ Error Handling

Aplikasi menangani berbagai kondisi:

| Error                | Handling      |
| -------------------- | ------------- |
| Division by zero     | Error message |
| Invalid logarithm    | Error message |
| Negative square root | Error message |
| Invalid factorial    | Error message |
| Missing parentheses  | Error message |
| Overflow number      | Warning       |

---

# 📱 Example Usage

## Basic

Input:

```
100 / 5 + 20
```

Output:

```
40
```

---

## Percentage

Input:

```
200 + 10%
```

Output:

```
220
```

---

## Scientific

Input:

```
√81
```

Output:

```
9
```

---

# 🔧 Development Notes

Project architecture:

```
MainActivity
      |
      |
CalculatorState
      |
      |
ExpressionEvaluator
```

Responsibilities:

### MainActivity.java

Mengatur:

* UI interaction
* Button event
* Display

### CalculatorState.java

Mengatur:

* Current expression
* Memory
* History
* Preferences

### ExpressionEvaluator.java

Mengatur:

* Parsing expression
* Mathematical calculation
* Error validation

---

# 📌 Future Improvements

Pengembangan berikutnya:

* [ ] Voice calculator
* [ ] Graph plotting
* [ ] Currency converter
* [ ] Unit converter
* [ ] Cloud backup
* [ ] Tablet optimization
* [ ] More scientific functions

---

# 👨‍💻 Developer

**Calculator Android Project**

Developed using:

* Java
* Android SDK
* XML Layout
* Gradle

---

# 📄 License

Project ini dibuat untuk tujuan pembelajaran dan pengembangan aplikasi Android.

Silakan digunakan dan dikembangkan kembali sesuai kebutuhan.
