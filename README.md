<div align="center">
  <h1>JOBHUB Mobile</h1>
  <p><em>Platform pencari dan pelamar kerja cerdas berbasis mobile.</em></p>
</div>

<br/>

## Tentang Aplikasi
**JOBHUB Mobile** adalah aplikasi Android native yang dirancang khusus untuk membantu *Job Seeker* (Pencari Kerja) dalam menemukan dan melamar pekerjaan impian mereka dengan mudah dan cepat langsung dari genggaman tangan. Terintegrasi secara *real-time* dengan sistem backend Laravel, JOBHUB menawarkan pengalaman pengguna yang mulus, responsif, dan modern.

## Tech Stack & Library
Aplikasi ini dikembangkan dengan teknologi Android modern terbaik:
- **[Kotlin](https://kotlinlang.org/)** - Bahasa pemrograman utama yang ekspresif dan aman.
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Toolkit UI deklaratif modern untuk membangun antarmuka Android secara native.
- **[Material Design 3](https://m3.material.io/)** - Sistem panduan desain antarmuka yang dinamis dan *user-friendly*.
- **[Retrofit & OkHttp](https://square.github.io/retrofit/)** - HTTP Client tangguh untuk komunikasi API secara langsung dengan backend Laravel.
- **[Coil Compose](https://coil-kt.github.io/coil/compose/)** - Library pemuatan gambar asinkron yang sangat cepat dan ringan.
- **ViewModel & Navigation Compose** - Arsitektur canggih untuk manajemen *state* dan perpindahan layar.

---

## Tim Pengembang (Kelompok 1)
- **Argya Seno Ahmadi Rizqullah**
- **Intan Trinanda**
- **Izanahda Nurkhasna**
- **Waldani Nabila Tamamah**

---

## Panduan Memulai (Cara Penggunaan)

Aplikasi mobile ini merupakan *client-side* yang mengambil data API dari backend web Laravel. Oleh karena itu, backend harus dijalankan terlebih dahulu sebelum menjalankan aplikasi mobile.

### 1. Siapkan & Jalankan Server Backend (Laravel)
Aplikasi ini membutuhkan backend dari repositori RPL. Kunjungi dan unduh repositorinya di tautan berikut:
**[Backend JOBHUB (praktikum-rpl-a-1)](https://github.com/WaldaniNabila/praktikum-rpl-a-1)**

Buka repositori backend tersebut di komputermu, masuk ke direktori kode, dan jalankan server *development*:
```bash
cd src
php artisan serve
```
*(Server akan berjalan pada `http://127.0.0.1:8000`)*

### 2. Konfigurasi Jaringan & API
Aplikasi ini dapat dijalankan baik di **Emulator** maupun **HP Fisik**. Namun, kamu perlu menyesuaikan Base URL API yang ada di file `ApiClient.kt` (`src/app/src/main/java/com/example/jobhub/network/ApiClient.kt`):

- **Jika menggunakan HP Fisik (Disarankan):**
  Pastikan HP dan laptop kamu terhubung ke jaringan WiFi yang sama. Ubah `BASE_URL` dengan IP lokal laptopmu IPv4 (contoh: `http://192.168.100.203:8000/api/`).
- **Jika menggunakan Emulator Android Studio:**
  Ubah `BASE_URL` menjadi `http://10.0.2.2:8000/api/` (IP default emulator untuk mengakses *localhost* komputer).

### 3. Build & Run Aplikasi
1. Buka folder `src` dari repositori ini menggunakan **Android Studio**.
2. Tunggu hingga proses sinkronisasi Gradle (**Gradle Sync**) selesai sepenuhnya.
3. Hubungkan HP fisik menggunakan kabel USB/Wireless Debugging, atau siapkan emulator di **Device Manager**.
4. Pastikan nama perangkatmu sudah terpilih di bilah menu atas Android Studio.
5. Klik tombol **Run 'app'** (ikon Play warna hijau).
6. Tunggu proses kompilasi (*build*) hingga selesai dan aplikasi akan terbuka secara otomatis di perangkatmu.

---

## Fitur Utama
- **Autentikasi Aman:** Sistem Login dan Registrasi yang terintegrasi penuh dengan API.
- **Eksplorasi Lowongan:** Fitur pencarian dan eksplorasi pekerjaan berdasarkan minat dan *skills*.
- **Detail Pekerjaan:** Informasi komprehensif mengenai posisi pekerjaan, persyaratan, dan profil perusahaan.
- **Lamar Sekali Klik:** Proses pelamaran kerja yang *seamless* langsung dari aplikasi.

<br/>

<div align="center">
  <sub>Dibuat oleh Kelompok 1</sub>
</div>
