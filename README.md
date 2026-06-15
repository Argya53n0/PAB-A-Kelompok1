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
Aplikasi ini butuh API dari repo RPL (Laravel). Buka project web/Laravel kalian, dan jalankan servernya:

- **Kalau kamu pakai Emulator (Virtual Device) Android Studio:**
  Cukup jalankan artisan biasa:
  ```bash
  php artisan serve
  ```
  *(Biarkan jalan di `http://127.0.0.1:8000`)*

- **Kalau kamu pakai HP Asli (Lewat kabel data / WiFi debugging):**
  Kamu harus buka servernya ke IP WiFi laptop kamu supaya HP-nya bisa mengakses. Jalankan perintah ini:
  ```bash
  php artisan serve --host=0.0.0.0
  ```
  *(Ini akan mengekspos API-nya ke jaringan lokal/WiFi kamu).*

### 2. Atur IP Address di Aplikasi Mobile (SANGAT PENTING!)
Sebelum menjalankan aplikasi Android, kamu **wajib** mengecek pengaturan IP di file koneksi API.

1. Buka file `src/app/src/main/java/com/example/jobhub/network/ApiClient.kt` di Android Studio.
2. Temukan variabel `BASE_URL` dan ganti alamatnya:

   **Opsi A: Pakai Emulator (Aman dari bawaan)**
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:8000/api/"
   ```
   *(Catatan: `10.0.2.2` adalah IP khusus yang dipakai emulator untuk mengakses `localhost` laptopmu).*

   **Opsi B: Pakai HP Asli**
   Ubah IP-nya jadi IP WiFi laptop kamu. Contoh jika IP laptopmu `192.168.100.5`:
   ```kotlin
   private const val BASE_URL = "http://192.168.100.5:8000/api/"
   ```

### 3. Build & Run
1. Buka repo PAB ini menggunakan **Android Studio**.
2. Tunggu sampai proses *Gradle Sync* di bagian bawah kanan selesai dan sukses (tanda *loading* hijau selesai).
3. Pilih perangkatmu (Emulator / HP yang sudah dicolok).
4. Tekan tombol **Play / Run** (ikon segitiga hijau) di bagian atas.
5. Silakan login pakai akun pelamar yang ada di database-mu! 🎉
