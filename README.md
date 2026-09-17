<div align="center">

# 🎬 TMDB Cinema — Android Movie App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7.5-blue.svg?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Material%203-Dark%20Theme-orange.svg?style=for-the-badge&logo=materialdesign)](https://m3.material.io/)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-green.svg?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Room](https://img.shields.io/badge/Database-Room%202.7-red.svg?style=for-the-badge&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![Koin](https://img.shields.io/badge/DI-Koin%204.0-brightgreen.svg?style=for-the-badge)](https://insert-koin.io/)
[![Android SDK](https://img.shields.io/badge/Target%20SDK-35-success.svg?style=for-the-badge&logo=android)](https://developer.android.com)

<p align="center">
  <b>Aplikasi Android Modern untuk Eksplorasi Film TMDB dengan Desain Tema Gelap Sinematik, Dukungan Offline-First Caching, Server-Side Search, dan Pemutar Trailer YouTube.</b>
</p>

</div>

---

## 📱 Pratinjau Tampilan Aplikasi (App Preview)

Sebagai gambaran visual dari antarmuka modern yang telah dibangun, berikut adalah tangkapan layar utama aplikasi:

<div align="center">
  <table>
    <tr>
      <th align="center">🏠 Home / Discover Movies</th>
      <th align="center">📂 Kategori & Filter Genre</th>
      <th align="center">🎬 Detail Film & Trailer Player</th>
    </tr>
    <tr>
      <td align="center">
        <img src="docs/screenshots/home_screen.png" width="280" alt="Home Screen"/>
        <br/>
        <sub><b>Grid Film, Chip Genre, Shimmer, & Infinite Scroll</b></sub>
      </td>
      <td align="center">
        <img src="docs/screenshots/genres_screen.png" width="280" alt="Genres Screen"/>
        <br/>
        <sub><b>Navigasi Seluruh Kategori Genre TMDB</b></sub>
      </td>
      <td align="center">
        <img src="docs/screenshots/detail_screen.png" width="280" alt="Detail Screen"/>
        <br/>
        <sub><b>Detail Lengkap, Trailer YouTube, & Ulasan Pengguna</b></sub>
      </td>
    </tr>
  </table>
</div>

---

## 📌 Tentang Aplikasi (About The App)

**TMDB Cinema** adalah aplikasi katalog dan penemuan film Android yang dibangun menggunakan standar industri pengembangan Android terkini. Aplikasi ini mengonsumsi RESTful API resmi dari **The Movie Database (TMDB)** untuk menyajikan data film terkini, ulasan, hingga video trailer resmi.

### 🌟 Fitur-Fitur Utama:
1. **Pustaka Film Berdasarkan Genre**: Jelajahi ribuan film dengan navigasi genre yang mulus (*All, Action, Adventure, Animation, Comedy, Crime, Drama, Horror*, dll).
2. **Offline-First Room Caching**: Data film dan genre yang berhasil diunduh akan otomatis tersimpan di database lokal **Room** secara terisolasi per kategori genre. Saat pengguna tidak memiliki koneksi internet, aplikasi tetap dapat menampilkan data dari cache dengan indikator badge offline yang informatif.
3. **Pencarian Film Server-Side (TMDB API Search)**: Kolom pencarian responsif langsung terhubung dengan endpoint `GET /search/movie` TMDB yang dilengkapi dengan **Debounce 400ms** untuk efisiensi panggilan jaringan.
4. **Pemutar Cuplikan Resmi (YouTube Trailer Integration)**: Tonton cuplikan resmi film langsung di dalam aplikasi melalui WebPlayer terintegrasi atau buka secara otomatis di aplikasi YouTube bawaan.
5. **Ulasan Pengguna (User Reviews)**: Menampilkan review pengguna dengan fitur ekspansi teks ulasan (*Read More / Show Less*) dan pagination.
6. **Pull-to-Refresh**: Perbarui data film dan genre kapan saja dengan efek animasi tarikan bertema sinematik.
7. **Fixed System Font Scale (Konsistensi Layout)**: Skala font aplikasi dikunci pada rasio standar (`fontScale = 1.0f`) di tingkat Jetpack Compose dan Android Context, sehingga tata letak UI tetap presisi dan tidak rusak meskipun pengguna mengatur ukuran font di perangkat HP menjadi sangat besar (*Extra Large*).

---

## 🛠️ Teknologi, Arsitektur & Perangkat (Tech Stack)

Aplikasi ini dirancang dengan prinsip modularitas, skalabilitas, dan kemudahan pengujian (*testability*) yang tinggi.

### 1. Bahasa Pemrograman
- **[Kotlin](https://kotlinlang.org/) (v2.0.21)**: Bahasa pemrograman resmi utama Android dengan dukungan fitur modern seperti Kotlin Coroutines, Kotlin Flow, dan Sealed Interfaces.

### 2. Pola Desain & Arsitektur (Design Pattern & Architecture)
Aplikasi menerapkan **Clean Architecture** yang dipadukan dengan pola **MVVM (Model-View-ViewModel)** serta prinsip **UDF (Unidirectional Data Flow)**:

```
┌───────────────────────────────────────────────────────────┐
│                 PRESENTATION LAYER (UI)                   │
│   Jetpack Compose Screens, Reusable Components, M3 Theme  │
│              GenreViewModel, MovieDetailViewModel         │
└─────────────────────────────▲─────────────────────────────┘
                              │
                    StateFlow │ Use Case Invocations
                              │
┌─────────────────────────────┴─────────────────────────────┐
│                    DOMAIN LAYER (Pure)                    │
│   Use Cases: GetDiscoverMovies, SearchMovies, Detail...   │
│         Domain Models (Movie, Genre, Review, Trailer)     │
│             Repository Interfaces (Abstractions)          │
└─────────────────────────────▲─────────────────────────────┘
                              │
                   Repository │ Implementation
                              │
┌─────────────────────────────┴─────────────────────────────┐
│                     DATA LAYER                            │
│   Repository Implementations, Entity/DTO Data Mappers     │
│   Local: Room Database, DAOs, SQLite Storage              │
│   Remote: Retrofit 2, OkHttp 3, TMDB REST API             │
└───────────────────────────────────────────────────────────┘
```

- **Presentation Layer**: Menangani rendering antarmuka pengguna secara deklaratif dengan Jetpack Compose, mengelola state layar melalui `StateFlow`, dan menangani interaksi pengguna.
- **Domain Layer**: Lapisan murni (*Pure Kotlin*) tanpa dependensi framework Android. Berisi kontrak antarmuka repositori, entitas domain model, dan Use Case independen yang dapat diuji secara terisolasi.
- **Data Layer**: Bertanggung jawab atas persistensi data lokal (Room DB) dan komunikasi jaringan (Retrofit). Mengatur strategi *Single Source of Truth* dan pemetaan data (Data Mappers).

### 3. Perpustakaan & Alat Bantu (Libraries & Tools)
| Kategori | Teknologi / Library | Kegunaan |
| :--- | :--- | :--- |
| **UI Toolkit** | Jetpack Compose & Material 3 | Desain antarmuka modern deklaratif bertema sinematik gelap |
| **Dependency Injection** | [Koin](https://insert-koin.io/) (v4.0.0) | DI ringan berbasis Kotlin DSL murni untuk injeksi UseCase, ViewModel, dan Repository |
| **Local Database** | [Room Database](https://developer.android.com/training/data-storage/room) (v2.7.0-alpha11) | Penyimpanan cache offline lokal untuk film, genre, dan detail |
| **Networking** | [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp 3](https://square.github.io/okhttp/) | REST API client dengan Logging Interceptor dan Authentication Interceptor |
| **Asynchronous** | Kotlin Coroutines & Flow | Pemrograman asinkron reaktif non-blocking |
| **Image Loading** | [Coil 3](https://coil-kt.github.io/coil/) (v3.0.4) | Pemuatan dan *caching* poster & backdrop film dari TMDB |
| **Navigation** | Jetpack Navigation Compose | Navigasi halaman antar layar berbasis rute Compose |
| **Media Player** | Android WebKit WebView | Pemutaran video cuplikan YouTube IFrame API secara aman |
| **Testing** | JUnit 4 & Coroutines Test | Pengujian unit test logika domain dan repositori |
| **Build System** | Gradle Kotlin DSL (`build.gradle.kts`) | Konfigurasi otomatis dependensi proyek |

---

## 📂 Struktur Direktori Proyek (Project Structure)

```
com.samsul.moviedb/
├── core/
│   ├── network/                # AuthInterceptor, NetworkMonitor
│   ├── ui/components/          # CinemaMovieCard, Shimmer, EmptyState, OfflineBadge, dll
│   └── util/                   # Resource wrapper (Success, Error, Loading)
├── data/
│   ├── local/                  # AppDatabase, DAOs (Movie, Genre, Detail, Review), Entities
│   ├── mapper/                 # Entity & DTO to Domain Model mappers
│   ├── remote/                 # TmdbApiService, DTOs (Data Transfer Objects)
│   └── repository/             # MovieRepositoryImpl (Strategi Offline-First & Network)
├── di/
│   └── AppModule.kt            # Koin Modules (Network, Database, Repository, UseCase, ViewModel)
├── domain/
│   ├── model/                  # Pure Domain Models (Movie, Genre, MovieDetail, Review, Trailer)
│   ├── repository/             # Kontrak interface MovieRepository
│   └── usecase/                # Single-responsibility business logic (Search, Discover, Detail, dll)
├── presentation/
│   ├── detail/                 # MovieDetailScreen, ViewModel, UIState, YouTubePlayerView
│   ├── genre/                  # GenreScreen (Home), ViewModel, UIState, AllGenresScreen
│   ├── movielist/              # MovieListScreen per genre
│   └── navigation/             # AppNavGraph, Screen destinations
└── ui/theme/                   # Cinema Color Palette, Theme (Locked FontScale), Typography
```

---

## 🔑 Panduan Pembuatan TMDB API Key

Aplikasi ini membutuhkan kunci akses API dari TMDB untuk melakukan *fetching* data film. Ikuti langkah-langkah berikut untuk mendapatkan API Key gratis:

1. **Daftar Akun TMDB**:
   - Buka peramban dan kunjungi [Halaman Pendaftaran TMDB](https://www.themoviedb.org/signup).
   - Isi formulir pendaftaran akun dan lakukan verifikasi email.
2. **Masuk ke Pengaturan Akun**:
   - Setelah masuk (*login*), klik ikon profil Anda di pojok kanan atas, lalu pilih **Settings**.
   - Pada panel menu samping kiri, klik menu **API** atau langsung akses URL: [https://www.themoviedb.org/settings/api](https://www.themoviedb.org/settings/api).
3. **Ajukan Permintaan API Key**:
   - Pada bagian permohonan API, klik tombol **Create** atau **Click here to apply for an API key**.
   - Pilih opsi **Developer** (untuk penggunaan personal/pengembangan).
   - Setujui syarat dan ketentuan penggunaan TMDB API.
4. **Isi Formulir Aplikasi**:
   - **Type of Use**: Personal / Education.
   - **Application Name**: `TMDB Cinema` (atau nama pilihan Anda).
   - **Application URL**: Isi dengan URL repositori GitHub Anda (misal: `https://github.com/Samsul-Arip/TMDB-Cinema`).
   - **Application Summary**: Tulis deskripsi singkat, misalnya: *"Aplikasi Android untuk eksplorasi film dan ulasan menggunakan TMDB API"*.
5. **Salin API Key**:
   - Setelah formulir disetujui, Anda akan langsung melihat bagian **API Key (v3 auth)** berupa 32 karakter alfanumerik (contoh: `c18967177842ccaa32f61701bd85c69b`).
   - Salin kunci ini untuk dimasukkan ke dalam file `local.properties`.

---

## 🚀 Panduan Instalasi & Menjalankan Aplikasi (Setup & Run)

### Prasyarat Sistem (Prerequisites):
- **Android Studio**: Ladybug | Koala | Iguana atau versi lebih baru.
- **Java Development Kit (JDK)**: Versi 17 atau 21.
- **Android SDK**: Compile SDK 35 (Android 15), Min SDK 24 (Android 7.0).
- Koneksi internet aktif untuk sinkronisasi dependensi Gradle awal dan pengujian API.

### Langkah-Langkah:

#### 1. Kloning Repositori
Jalankan perintah berikut di terminal Anda:
```bash
git clone https://github.com/Samsul-Arip/TMDB-Cinema.git
cd TMDB-Cinema
```

#### 2. Konfigurasi `local.properties`
Buka atau buat file bernama `local.properties` di **direktori root proyek** (sejajar dengan `settings.gradle.kts`). Tambahkan konfigurasi berikut dan sesuaikan lokasi Android SDK serta API Key TMDB Anda:

```properties
## Lokasi Android SDK di komputer Anda
sdk.dir=/Users/username/Library/Android/sdk # (macOS)
# sdk.dir=C\:\\Users\\username\\AppData\\Local\\Android\\Sdk # (Windows)
# sdk.dir=/home/username/Android/Sdk # (Linux)

## Konfigurasi Kunci & Endpoint TMDB
tmdb.api.key=MASUKKAN_TMDB_API_KEY_ANDA_DISINI
tmdb.base.url=https://api.themoviedb.org/3/
tmdb.image.base.url=https://image.tmdb.org/t/p/
```

> ⚠️ **Catatan Penting**: Nilai dari `tmdb.api.key`, `tmdb.base.url`, dan `tmdb.image.base.url` akan dibaca oleh Gradle dan dikompilasi secara aman ke dalam `BuildConfig` aplikasi (`BuildConfig.TMDB_API_KEY`).

#### 3. Buka di Android Studio & Sinkronkan Gradle
- Buka Android Studio, pilih **Open** dan arahkan ke folder proyek.
- Tunggu proses Gradle Sync selesai hingga semua dependensi terunduh.

#### 4. Menjalankan Unit Test (Opsional)
Untuk memverifikasi keandalan logika bisnis use case dan pemetaan repository:
```bash
./gradlew testDebugUnitTest
```

#### 5. Menjalankan Aplikasi
- Hubungkan perangkat fisik Android melalui USB Debugging atau jalankan Emulator Android (API level 24 ke atas).
- Tekan tombol hijau **Run 'app'** (`Shift + F10`) di Android Studio, atau jalankan perintah:
```bash
./gradlew installDebug
```

---

## 🧪 Pengujian Kualitas (Testing & Quality)

Proyek ini dilengkapi dengan unit test otomatis pada lapisan domain dan use case:
- `SearchMoviesUseCaseTest`: Memverifikasi pencarian film query cocok, hasil kosong, dan penanganan kegagalan jaringan.
- `GetDiscoverMoviesUseCaseTest`: Memverifikasi pagination dan filter genre.
- `GetMovieDetailUseCaseTest`: Memverifikasi ketersediaan data detail film.
- `GetMovieReviewsUseCaseTest`: Memverifikasi pengambilan ulasan pengguna.
- `GetMovieTrailerUseCaseTest`: Memverifikasi prioritas trailer YouTube resmi.
- `FakeMovieRepository`: Test double untuk isolasi pengetesan tanpa bergantung pada jaringan asli.

---

## 👨‍💻 Kontributor & Lisensi

Dibuat dengan ❤️ oleh **[Samsul Arip](https://github.com/Samsul-Arip)**.

Proyek ini didistribusikan di bawah lisensi [MIT License](LICENSE). Bebas digunakan untuk keperluan edukasi, portofolio, dan pengembangan lanjutan.
