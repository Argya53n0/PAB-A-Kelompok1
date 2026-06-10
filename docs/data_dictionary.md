# Data Dictionary / API Models: JOBHUB Mobile

Karena aplikasi Android berinteraksi dengan sistem Web melalui **REST API**, struktur data di bawah ini merepresentasikan **Model (JSON)** yang dikirim dan diterima oleh aplikasi Mobile. Penamaan propertinya diselaraskan (terintegrasi) dengan skema database utama (ERD) pada aplikasi Web Laravel.

### 1. Model `Auth / User` (Gabungan `users` & `job_seekers`)
Saat Job Seeker login, API web akan mengirimkan data *user* gabungan.
| Properti | Tipe Data | Relasi Database Web | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Int | `users.id` | ID unik pengguna |
| `name` | String | `users.name` | Nama lengkap |
| `email` | String | `users.email` | Email pengguna |
| `token` | String | (Generate by JWT/Sanctum) | Token untuk akses API |
| `phone` | String | `job_seekers.phone` | Nomor telepon |
| `profile_picture`| String | `job_seekers.profile_picture` | URL gambar profil |
| `description` | String | `job_seekers.description` | Deskripsi singkat (Bio) |

### 2. Model `Job` (Menggabungkan `jobs` & `companies`)
Digunakan untuk menampilkan daftar dan detail lowongan.
| Properti | Tipe Data | Relasi Database Web | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Int | `jobs.id` | ID unik lowongan |
| `company_id` | Int | `companies.id` | ID perusahaan |
| `company_name` | String | `companies.name` | Nama perusahaan |
| `logo_path` | String | `companies.logo_path` | URL logo perusahaan |
| `title` | String | `jobs.title` | Judul lowongan |
| `description` | String | `jobs.description` | Deskripsi lowongan |
| `requirements` | String | `jobs.requirements` | Syarat dan kualifikasi |
| `location` | String | `jobs.location` | Lokasi penempatan |
| `employment_type`| String | `jobs.employment_type`| Full-time, Part-time, dll. |
| `work_type` | String | `jobs.work_type` | Remote, On-site, Hybrid |
| `salary_min` | Int | `jobs.salary_min` | Batas bawah gaji |
| `salary_max` | Int | `jobs.salary_max` | Batas atas gaji |
| `is_saved` | Boolean| Pengecekan tabel `bookmarks`| Apakah pelamar membookmark ini? |

### 3. Model `Application` (Lamaran)
Digunakan untuk menampilkan riwayat lamaran pelamar.
| Properti | Tipe Data | Relasi Database Web | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Int | `applications.id` | ID unik lamaran |
| `job_id` | Int | `applications.job_id` | ID lowongan yang dilamar |
| `job_title` | String | `jobs.title` | Judul lowongan |
| `company_name` | String | `companies.name` | Nama perusahaan |
| `status` | String | `applications.status` | Pending, Reviewed, Accepted, Rejected |
| `cover_letter` | String | `applications.cover_letter` | Surat lamaran (opsional) |
| `cv_path` | String | `applications.cv_path` | File CV yang dikirimkan |
| `created_at` | String | `applications.created_at` | Waktu lamaran dikirim |

### 4. Model `CV` (Dokumen Resume)
Digunakan saat Job Seeker mengunggah atau memilih CV.
| Properti | Tipe Data | Relasi Database Web | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Int | `cvs.id` | ID unik file CV |
| `filename` | String | `cvs.filename` | Nama file (misal: "CV_Argya.pdf") |
| `filepath` | String | `cvs.filepath` | URL lokasi file disimpan di server |
| `is_default` | Boolean| `cvs.is_default` | Apakah ini CV utama? |
