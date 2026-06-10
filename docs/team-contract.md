# Team Contract - Aplikasi Mobile JOBHUB (Android)

Dokumen ini merupakan kesepakatan resmi anggota tim untuk memastikan kelancaran kolaborasi selama pengembangan aplikasi Mobile JOBHUB (PAB).

## 1. Peran Anggota Tim (Team Roles)
Setiap anggota bertanggung jawab atas tugas utama pengembangan versi mobile berikut:
| Nama               | Role                                  |
|--------------------|---------------------------------------|
| Argya Seno         | Lead Android Developer (Kotlin)       |
| Waldani Nabila     | UI/UX Designer & Android UI Developer |
| Izanahda Nurkhasna | QA Tester & Android Frontend          |
| Intan Trinanda     | Documentation & API Integrator        |

* **Argya Seno**: **Lead Android Developer**
  * Bertanggung jawab atas desain arsitektur aplikasi (misalnya MVVM/MVC) dan struktur basis data lokal (Room Database/Preferences).
  * Mengelola logika bisnis di sisi Android, *Network calls*, dan implementasi API.
  * Melakukan *code review* untuk setiap perubahan kode.

* **Izanahda Nurkhasna**: **QA Tester & Android Frontend**
  * Bertanggung jawab melakukan pengujian fungsionalitas aplikasi di emulator maupun perangkat *real device* (Black-box testing).
  * Mendokumentasikan bug atau *crash* ke dalam Issue Tracker dan memantau proses perbaikannya.
  * Membantu pengembangan komponen antarmuka pengguna Android (XML/Compose).

* **Intan Trinanda**: **Documentation & API Integrator**
  * Bertanggung jawab menyusun laporan dan mengelola dokumentasi teknis dalam repositori seperti *Wireframes*, SRS, dan arsitektur aplikasi.
  * Membantu menghubungkan (*integrate*) layanan *Backend API* dengan layanan Android (Retrofit/Ktor).

* **Waldani Nabila Tamamah**: **UI/UX Designer & Android UI Developer**
  * Bertanggung jawab atas pembuatan desain visual aplikasi *mobile* dan *Prototyping* layar di Figma.
  * Mengimplementasikan desain antarmuka menjadi kode Android yang responsif (Material Design).
  * Memastikan konsistensi *styling* (warna, *font*, dan aset) di seluruh layar aplikasi.

## 2. Jadwal Pertemuan (Meeting Schedule)
Pertemuan rutin dilakukan untuk sinkronisasi progress pengembangan mobile:
* **Hari**: [Disesuaikan dengan kesepakatan]
* **Waktu**: [Disesuaikan]
* **Platform**: Google Meet / Discord
* **Agenda**: Update progress mingguan, pembagian modul Android, penyelesaian kendala integrasi API, dan Evaluasi.

## 3. Saluran Komunikasi (Communication Channels)
* **Komunikasi Harian**: Whatsapp Group.
* **Diskusi Teknis**: Discord / Google Meet untuk membahas error atau kendala sinkronisasi data dengan *Backend*.
* **Manajemen Tugas**: Trello / GitHub Projects untuk *tracking* tugas dan jadwal *Sprint*.

## 4. Aturan Respons (Communication Policy)
* **Waktu Respons**: Maksimal **2 jam** setelah pesan dikirimkan pada jam aktif (09:00 - 17:00).
* **Ketersediaan**: Wajib menginfokan jika berhalangan lebih dari 12 jam.

## 5. Standar Pesan Commit (Commit Message Standards)
Gunakan format **imperative** untuk setiap perubahan di repositori Android:
* **Format**: `[Tag]: [Short Description]`
* **Contoh Tag**: `feat` (fitur baru), `fix` (perbaikan *bug*/*crash*), `docs` (dokumentasi), `ui` (perubahan tampilan XML/Compose).
* **Contoh Pesan**:
    * `feat: add Retrofit setup for login API`
    * `fix: resolve NullPointerException on job detail screen`
    * `ui: update primary color in themes.xml`

## 6. Mekanisme Eskalasi (Escalation Mechanism)
Jika terjadi masalah yang tidak dapat diselesaikan secara internal (misal: API tidak jalan, *library* bentrok):
1.  **Diskusi Internal**: Dibahas bersama di *group* atau *meeting*.
2.  **Keputusan Teknis**: Lead Developer mengambil keputusan arsitektur.
3.  **Bantuan Eksternal**: Meminta arahan asisten praktikum (PAB).

---
*Kontrak ini disepakati oleh seluruh anggota tim untuk pengembangan JOBHUB Mobile.*
