# User Stories — JOBHUB Mobile

## Aktor / Role Pengguna

### 1. Pelamar (Job Seeker)
Satu-satunya pengguna utama pada aplikasi versi *Mobile* yang menggunakan *smartphone* Android untuk mencari, melamar pekerjaan, dan mengelola dokumen lamaran. (Perusahaan dan Admin hanya menggunakan versi Web).

---

## User Stories

### Pelamar

---

**US-M01 — Registrasi akun pelamar dari HP**

> Sebagai pelamar, saya ingin mendaftar akun baru melalui HP agar saya dapat mulai menggunakan aplikasi JOBHUB Mobile.

**Acceptance Criteria:**

- **Given** saya belum memiliki akun dan mengunduh aplikasi JOBHUB Mobile,
- **When** saya mengisi form registrasi dengan nama, email, dan password lalu klik "Daftar",
- **Then** akun saya berhasil dibuat di database dan saya diarahkan ke halaman login.

---

**US-M02 — Login ke akun dari HP**

> Sebagai pelamar, saya ingin masuk ke aplikasi dengan email dan password agar data profil dan lamaran saya tersinkronisasi.

**Acceptance Criteria:**

- **Given** saya sudah mendaftar dan berada di halaman login,
- **When** saya memasukkan email dan password yang valid lalu menekan "Login",
- **Then** sistem memberikan token JWT, saya berhasil masuk, dan diarahkan ke halaman *Home*.

---

**US-M03 — Mencari lowongan kerja**

> Sebagai pelamar, saya ingin mencari lowongan berdasarkan kata kunci (posisi/perusahaan) agar saya menemukan posisi yang relevan.

**Acceptance Criteria:**

- **Given** saya berada di halaman *Home*,
- **When** saya mengetikkan kata kunci pada kolom pencarian dan menekan enter/cari,
- **Then** sistem menampilkan daftar *card* lowongan yang sesuai dengan kata kunci tersebut.

---

**US-M04 — Memfilter lowongan kerja**

> Sebagai pelamar, saya ingin menggunakan filter lokasi dan kategori agar pencarian lowongan lebih akurat.

**Acceptance Criteria:**

- **Given** saya sedang melihat daftar lowongan,
- **When** saya memilih opsi filter (misal: "Jakarta" dan "IT"),
- **Then** daftar lowongan otomatis diperbarui hanya menampilkan kriteria yang dipilih.

---

**US-M05 — Melihat detail lowongan**

> Sebagai pelamar, saya ingin membaca detail spesifik suatu lowongan agar saya tahu kecocokannya dengan kemampuan saya.

**Acceptance Criteria:**

- **Given** saya melihat daftar lowongan di halaman *Home* atau pencarian,
- **When** saya menekan salah satu *card* lowongan,
- **Then** saya diarahkan ke halaman Detail Lowongan yang berisi syarat, deskripsi, gaji, dan profil perusahaan.

---

**US-M06 — Mengunggah CV dari HP**

> Sebagai pelamar, saya ingin mengunggah file CV berformat PDF dari penyimpanan HP saya agar bisa digunakan saat melamar.

**Acceptance Criteria:**

- **Given** saya berada di menu Profil atau saat proses melamar,
- **When** saya menekan tombol "Upload CV" dan memilih file PDF dari *storage* Android,
- **Then** file berhasil diunggah ke server dan muncul dalam daftar dokumen CV saya.

---

**US-M07 — Mengirim lamaran pekerjaan**

> Sebagai pelamar, saya ingin mengirim lamaran kerja dengan satu ketukan tombol agar prosesnya cepat.

**Acceptance Criteria:**

- **Given** saya sedang membuka halaman Detail Lowongan,
- **When** saya menekan tombol "Apply", memilih CV, dan mengonfirmasi pengiriman,
- **Then** data lamaran terkirim ke *backend* dan statusnya tercatat sebagai "Menunggu".

---

**US-M08 — Melacak riwayat & status lamaran**

> Sebagai pelamar, saya ingin melihat riwayat dan melacak status lamaran saya agar tidak penasaran dengan hasil rekrutmen.

**Acceptance Criteria:**

- **Given** saya pernah melamar minimal satu pekerjaan,
- **When** saya membuka tab "Lamaran Saya",
- **Then** saya melihat daftar lowongan yang saya lamar beserta status terkini (Pending / Reviewed / Accepted / Rejected).

---

**US-M09 — Menyimpan (Bookmark) lowongan favorit**

> Sebagai pelamar, saya ingin menyimpan lowongan yang menarik agar bisa saya lamar di lain waktu.

**Acceptance Criteria:**

- **Given** saya sedang melihat detail suatu lowongan,
- **When** saya menekan tombol *icon* "Bookmark / Save",
- **Then** lowongan tersebut tersimpan di *Local Database* HP dan muncul di tab "Tersimpan".

---

**US-M10 — Mengelola profil dan Logout**

> Sebagai pelamar, saya ingin bisa keluar (logout) dari aplikasi agar akun saya aman jika HP dipinjam orang lain.

**Acceptance Criteria:**

- **Given** saya sedang berada di tab "Profil",
- **When** saya menekan tombol "Logout" dan mengonfirmasi peringatan,
- **Then** token sesi saya dihapus dari *SharedPreferences* dan saya dikembalikan ke halaman *Login*.
