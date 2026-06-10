# Software Requirements Specification (SRS)
# JOBHUB Mobile — Platform Pencarian Lowongan Kerja (Versi Pelamar)

---

## 1. Pendahuluan

### 1.1 Tujuan Dokumen
Dokumen SRS ini bertujuan untuk mendeskripsikan kebutuhan fungsional dan non-fungsional dari sistem JOBHUB Mobile secara formal dan terstruktur. Dokumen ini menjadi acuan bagi seluruh anggota tim dalam proses pengembangan, pengujian, dan evaluasi aplikasi Android yang **berfokus khusus pada Job Seeker (Pelamar)**.

### 1.2 Ruang Lingkup Sistem
JOBHUB Mobile adalah aplikasi berbasis Android yang ditujukan secara spesifik untuk pencari kerja (pelamar). Aplikasi ini memungkinkan pengguna untuk mencari lowongan pekerjaan, melamar pekerjaan, memantau status lamaran, dan menyimpan lowongan favorit dari perangkat *mobile*. Manajemen perusahaan dan moderasi admin tetap dikelola melalui aplikasi web.

### 1.3 Definisi Istilah

| Istilah | Definisi |
|---------|----------|
| Job Seeker / Pelamar | Pengguna aplikasi mobile yang mencari dan melamar pekerjaan |
| Lowongan | Informasi pekerjaan yang diposting oleh perusahaan (diambil dari API) |
| Lamaran | Pengajuan dari pelamar untuk posisi pekerjaan tertentu |
| MVP | Minimum Viable Product — fitur minimum yang harus ada |
| FR | Functional Requirement — kebutuhan fungsional sistem |
| NFR | Non-Functional Requirement — kebutuhan non-fungsional sistem |

---

## 2. Deskripsi Umum

### 2.1 Perspektif Produk
JOBHUB Mobile merupakan aplikasi native Android yang berjalan di smartphone. Aplikasi ini berinteraksi dengan *backend* server (API dari aplikasi web Laravel JOBHUB) untuk mendapatkan data lowongan dan mengirimkan data lamaran.

### 2.2 Fungsi Utama Sistem
- Mendaftar dan login akun pelamar.
- Mencari dan memfilter daftar lowongan kerja.
- Mengunggah dan mengelola dokumen CV.
- Mengirimkan lamaran pekerjaan secara online.
- Melacak (tracking) status dari lamaran yang telah dikirim.

### 2.3 Karakteristik Pengguna

| Pengguna | Karakteristik |
|----------|--------------|
| Pelamar | Masyarakat umum, mahasiswa, atau profesional yang sedang mencari pekerjaan dan memiliki *smartphone* Android. |

### 2.4 Batasan Sistem
- Aplikasi ini hanya untuk platform Android.
- Aplikasi **tidak menyediakan** fitur bagi *Company* untuk membuat lowongan.
- Aplikasi **tidak menyediakan** fitur bagi *Admin* untuk moderasi konten.
- Akses internet selalu dibutuhkan untuk menarik data dari server.

---

## 3. Kebutuhan Fungsional (Functional Requirements)

**FR-01:** Sistem menyediakan form registrasi bagi Job Seeker dengan input nama lengkap, email, dan password.  
**Prioritas:** High  
**Ref:** UC-M01

---

**FR-02:** Sistem menyediakan form login dengan email dan password, lalu menghasilkan token JWT untuk mengakses fitur aplikasi.  
**Prioritas:** High  
**Ref:** UC-M02

---

**FR-03:** Sistem menampilkan daftar lowongan dan menyediakan fitur pencarian (Search) berdasarkan kata kunci pekerjaan atau nama perusahaan.  
**Prioritas:** High  
**Ref:** UC-M03

---

**FR-04:** Sistem memiliki fitur *Filter* untuk menyaring lowongan berdasarkan lokasi, kategori pekerjaan, dan tipe pekerjaan (Full-time, Remote, dll).  
**Prioritas:** High  
**Ref:** UC-M04

---

**FR-05:** Sistem menampilkan detail lowongan yang berisi deskripsi pekerjaan, syarat & kualifikasi, gaji, dan informasi profil perusahaan.  
**Prioritas:** High  
**Ref:** UC-M05

---

**FR-06:** Sistem memfasilitasi Job Seeker untuk mengunggah file CV/Resume berformat PDF dari penyimpanan perangkat *mobile*.  
**Prioritas:** High  
**Ref:** UC-M07

---

**FR-07:** Sistem memungkinkan Job Seeker untuk melamar posisi yang dipilih ("Apply Job") dengan melampirkan CV yang telah diunggah.  
**Prioritas:** High  
**Ref:** UC-M06

---

**FR-08:** Sistem menampilkan riwayat lamaran dan melacak (*track*) statusnya secara real-time (Misal: Menunggu, Direview, Diterima, Ditolak).  
**Prioritas:** High  
**Ref:** UC-M08

---

**FR-09:** Sistem memungkinkan Job Seeker untuk menyimpan (*Bookmark*) lowongan ke dalam daftar favorit agar bisa dilihat nanti.  
**Prioritas:** Medium  
**Ref:** UC-M09

---

**FR-10:** Sistem menyediakan halaman Profil untuk mengedit informasi dasar, mengganti foto profil, serta fungsi *Logout*.  
**Prioritas:** Medium  
**Ref:** UC-M10

---

## 4. Kebutuhan Non-Fungsional (Non-Functional Requirements)

**NFR-01 (Performance):** Waktu respon aplikasi saat memuat daftar lowongan dari API maksimal 3 detik pada koneksi internet standar (4G). Aplikasi harus menampilkan indikator loading (*progress bar/spinner*) saat mengambil data.

---

**NFR-02 (Security):** Autentikasi menggunakan standar token JWT (JSON Web Token) yang disimpan dengan aman di perangkat (misalnya melalui Encrypted SharedPreferences).

---

**NFR-03 (Usability):** Antarmuka aplikasi harus *user-friendly* dan mengikuti standar desain *Material Design 3* untuk Android, memastikan kenyamanan navigasi satu tangan.

---

**NFR-04 (Compatibility):** Aplikasi harus mendukung minimum SDK versi Android 8.0 (API Level 26) ke atas agar mencakup sebagian besar perangkat Android di pasaran.

---

## 5. Catatan dan Asumsi

### Catatan
- Kebutuhan ini disesuaikan sepenuhnya berdasarkan *Use Case* (UC-M01 s/d UC-M10) yang dirancang untuk aktor *Job Seeker*.

### Asumsi
- *Backend API* dari sistem JOBHUB Web (Laravel) sudah tersedia dan siap diakses.
- Pengguna memberikan izin (permission) untuk mengakses penyimpanan perangkat saat ingin mengunggah file CV.
