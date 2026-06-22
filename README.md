# JOBHUB Mobile

Aplikasi mobile JOBHUB untuk membantu Job Seeker mencari dan melamar pekerjaan melalui smartphone. Dibangun menggunakan **Kotlin** dan **Jetpack Compose**.

## Anggota Tim

- Argya Seno Ahmadi Rizqullah
- Intan Trinanda
- Izanahda Nurkhasna
- Waldani Nabila Tamamah

---

## 🚀 Cara Menjalankan Aplikasi Mobile (Untuk Anggota Tim)

Karena aplikasi mobile ini terhubung dengan backend Laravel, ada beberapa langkah penting agar aplikasinya bisa berjalan lancar dan terhubung dengan *database*.

### 1. Jalankan Backend Laravel Dulu
Aplikasi ini butuh API dari repo RPL (Laravel). Buka project web/Laravel kalian, masuk ke folder `src`, dan jalankan servernya seperti biasa:

```bash
cd src
php artisan serve
```
*(Server akan berjalan di `http://127.0.0.1:8000`)*

### 2. Atur Koneksi API (Sangat Penting!)
Untuk saat ini, aplikasi mobile telah dikonfigurasi **HANYA untuk dijalankan di Emulator Android Studio**.

- **Pakai Emulator (Android Studio / AVD)**
  Aplikasi menggunakan IP `10.0.2.2` secara default. Kamu **tidak perlu melakukan konfigurasi tambahan apapun**. Emulator akan otomatis terhubung ke *localhost* laptopmu (`127.0.0.1`). Langsung *Run* saja aplikasinya menggunakan *virtual device*!

> ⚠️ **Peringatan:** Jangan jalankan di HP Asli (fisik) untuk sementara waktu, karena kodenya sedang di-*lock* khusus untuk IP Emulator.

### 3. Cara Menjalankan Aplikasi (Build & Run)
Buka folder `src` di **Android Studio** dan tunggu sampai proses *Gradle Sync* (loading di bagian bawah) selesai. Setelah itu, ikuti panduan sesuai perangkat yang kamu pilih:

#### Menjalankan di Emulator (Virtual Device)
1. Buka **Device Manager** (Ikon HP di kanan atas atau `View -> Tool Windows -> Device Manager`).
2. Klik **Create Device** jika belum punya emulator. Pilih tipe HP (misal: Pixel 6), lalu klik Next dan download System Image yang disarankan (misal: API 34), lalu Finish.
3. Klik tombol **Play (▶️)** di samping nama emulator pada Device Manager untuk menyalakannya.
4. Di bagian atas Android Studio (sebelah tombol Run), pastikan nama emulator-mu sudah terpilih.
5. Klik tombol **Run 'app' (▶️ hijau)** di menu atas.
6. Tunggu proses build selesai dan aplikasi akan terbuka otomatis di Emulator.

