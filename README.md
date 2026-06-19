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
Secara kodingan, aplikasi ini **sudah nol konfigurasi (zero config)**. Kamu tidak perlu gonta-ganti IP Address di dalam kode. Aplikasi sudah diatur untuk otomatis mendeteksi apakah kamu memakai Emulator atau HP Asli.

- **Opsi A: Pakai Emulator (Android Studio / AVD)**
  Aplikasi akan otomatis menggunakan IP `10.0.2.2`. Kamu **tidak perlu melakukan konfigurasi tambahan apapun**. Langsung *Run* saja aplikasinya.

- **Opsi B: Pakai HP Asli (Lewat kabel USB)**
  Aplikasi akan otomatis menggunakan `127.0.0.1` (*localhost* HP). Agar *localhost* HP bisa tersambung ke *localhost* laptop, kamu **wajib** menjalankan 1 baris perintah ini di terminal (hanya 1x selama dicolok):
  
  ```bash
  adb reverse tcp:8000 tcp:8000
  ```
  > **💡 Solusi jika error 'adb is not recognized':**
  > Artinya path ADB belum disetting di Windows. Kamu bisa pakai path lengkap (sesuaikan username PC kamu):
  > `& "C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe" reverse tcp:8000 tcp:8000`


### 3. Cara Menjalankan Aplikasi (Build & Run)
Buka folder `src` di **Android Studio** dan tunggu sampai proses *Gradle Sync* (loading di bagian bawah) selesai. Setelah itu, ikuti panduan sesuai perangkat yang kamu pilih:

#### Menjalankan di Emulator (Virtual Device)
1. Buka **Device Manager** (Ikon HP di kanan atas atau `View -> Tool Windows -> Device Manager`).
2. Klik **Create Device** jika belum punya emulator. Pilih tipe HP (misal: Pixel 6), lalu klik Next dan download System Image yang disarankan (misal: API 34), lalu Finish.
3. Klik tombol **Play (▶️)** di samping nama emulator pada Device Manager untuk menyalakannya.
4. Di bagian atas Android Studio (sebelah tombol Run), pastikan nama emulator-mu sudah terpilih.
5. Klik tombol **Run 'app' (▶️ hijau)** di menu atas.
6. Tunggu proses build selesai dan aplikasi akan terbuka otomatis di Emulator.

#### Menjalankan di HP Asli (Physical Device)
1. Siapkan kabel data (USB) dan sambungkan HP kamu ke laptop.
2. Di HP, masuk ke **Pengaturan (Settings) -> Opsi Pengembang (Developer Options)**. *(Jika belum ada, buka Tentang Ponsel/About Phone, lalu ketuk 'Build Number' 7 kali).*
3. Aktifkan **USB Debugging** di dalam menu Opsi Pengembang.
4. Akan muncul *popup* konfirmasi di layar HP-mu yang meminta izin USB debugging dari komputer, pilih **Izinkan (Allow)**.
5. Pastikan nama HP-mu muncul di menu drop-down perangkat di bagian atas Android Studio (sebelah tombol Run).
6. **[PENTING]** Jalankan perintah `adb reverse tcp:8000 tcp:8000` di terminal seperti yang dijelaskan pada langkah 2 di atas.
7. Klik tombol **Run 'app' (▶️ hijau)**. Aplikasi akan di-install dan terbuka di HP-mu.
8. Silakan login menggunakan akun pelamar yang ada di database! 🎉
