### Ngoding di Word & Notepad, Ramen Kuah Tanah Liat, dan Mencatat Ide di Atas Batu

> [!NOTE]
> Dokumen ini memang sengaja dibuat absurd. Bukan karena kita tidak mampu membuatnya normal, tetapi karena manusia sudah punya terlalu banyak dokumen normal.
>
> Fakta yang punya sumber tetap punya sumber. Yang tidak punya catatan kaki? Anggap saja **otak penulis sedang freestyle** dan jangan dibawa ke pengadilan.[^n]

## 📑 Daftar Isi

1. [10 Keunggulan Ngoding di MS Word & Notepad vs IDE Apapun](#1--10-keunggulan-ngoding-di-ms-word--notepad-vs-ide-apapun)
2. [Resep Ramen Kuah Tanah Liat](#2--resep-ramen-kuah-tanah-liat)
3. [10 Keunggulan Mencatat Ide di Atas Batu](#3--10-keunggulan-mencatat-ide-di-atas-batu)
4. [Daftar Pustaka](#-daftar-pustaka)

---

# 1. 💻 10 Keunggulan Ngoding di MS Word & Notepad vs IDE Apapun

> [!IMPORTANT]
> **Sebelum ada yang ngamuk:** Notepad dan Notepad++ itu bukan saudara kembar. Notepad adalah bawaan Windows, sedangkan Notepad++ adalah editor open-source pihak ketiga.
>
> Jadi ketika nanti kita bahas statistik developer, yang muncul adalah **Notepad++**. Anggap saja Notepad punya sepupu yang lebih terkenal dan kebetulan sering masuk survei.

## Tabel Perbandingan Singkat (Skor Absurd 0–10)

| Kriteria | Notepad | MS Word | IDE Apapun |
|---|:---:|:---:|:---:|
| Waktu nyala | ⚡ 10 | 🐢 6 | 🪦 2 ("Indexing…") |
| Jumlah plugin yang bisa konflik | 0 | 0 (tapi ada *Add-ins* misterius) | 247 |
| Fitur hiburan non-coding | 1 | 9 (WordArt!) | 5 |
| Rasa nostalgia | 10 | 8 | 3 |
| Risiko diceramahi *linter* | 0 | 0 | ∞ |
| Bisa dicetak jadi skripsi | 3 | 10 | 4 |

## Sepuluh Keunggulannya

### 1) Instalasi nol detik, nol gigabyte

Notepad pertama muncul di MS-DOS tahun 1983, lalu ikut hadir di Windows 1 dua tahun kemudian dan terus nongol di berbagai versi Windows.[^everand][^wiki-notepad]

IDE, di sisi lain, kadang meminta beberapa gigabyte ruang penyimpanan hanya untuk akhirnya berkata:

> "Indexing..."

Terima kasih. Aku cuma mau bikin `Hello World`.

```bash
# Memasang Notepad
$ (tidak ada langkah)

# Memasang IDE
$ download 2.4GB ... 38% ... "Pilih workload" ... "Restart komputer" ... "Sign in"
```

Notepad tidak bertanya siapa kamu. Tidak meminta akun. Tidak meminta restart.

Dia cuma duduk.

Menunggu.

Seperti pegawai yang sudah menerima kenyataan.

---

### 2) Tidak ada *dependency hell* karena memang tidak punya dependency

Notepad pada dasarnya adalah editor teks sederhana dengan fitur seperti cari dan ganti.[^wiki-notepad]

Ekosistem pluginnya juga sangat damai. Tidak ada.

Menurut Xataka, salah satu hal paling canggih dari Notepad justru adalah betapa sederhananya dia.[^xataka]

`package.json` kamu kira-kira begini:

```json
{}
```

Kosong.

Indah.

Tidak ada `node_modules` sebesar dosa masa lalu.

Tidak ada:

```text
npm ERR!
peer dependency conflict
```

Tidak ada pula seseorang di Stack Overflow berkata:

> "Coba hapus node_modules dan install ulang."

Hidup begitu sederhana sebelum kita menemukan JavaScript.

---

### 3) Satu keluarga: Word dan Notepad ternyata kakak-adik

Tahun 1983, Microsoft memperkenalkan *Multi-Tool Notepad* dan *Multi-Tool Word*. Nama mereka kemudian dipendekkan menjadi Notepad dan Microsoft Word.[^wiki-notepad]

Notepad bahkan pada awalnya dibuat sebagai versi ringkas dari Word.[^verge]

Jadi kalau kamu ngoding di Word lalu pindah ke Notepad, itu bukan downgrade.

Itu **reuni keluarga**.

| Tahun | Peristiwa | Sumber |
|:---:|---|:---:|
| 1983 | Multi-Tool Notepad & Multi-Tool Word lahir | [^wiki-notepad] |
| 1985 | Word, yang saat itu masih Multi-Tool Word, mendapat spellcheck pertamanya | [^verge] |
| 2024 | Notepad akhirnya mendapat spellcheck & autocorrect, 40+ tahun setelah rilis | [^verge] |

Empat puluh tahun untuk mendapatkan autocorrect.

Manusia memang butuh waktu untuk berkembang.

---

### 4) *Syntax highlighting* manual pakai stabilo Word

Notepad dulu memang sangat polos.[^wiki-notepad]

Word, untungnya, punya format teks.

Maka lahirlah teknologi mutakhir:

**syntax highlighting berbasis Ctrl+B.**

Misalnya:

```python
# Contoh highlight manual ala Word
**def** halo():          # "def" ditebalkan dengan Ctrl+B
    print(__"halo dunia"__)  # string digarisbawahi dengan Ctrl+U
```

Komentar?

Stabilo kuning.

Nama fungsi?

Bold.

String?

Garis bawah.

Error?

Itu bagian dari pengalaman spiritual.

> [!WARNING]
> **Waspada Smart Quotes.**
>
> Word suka mengubah tanda kutip lurus `"` menjadi `“ ”`. Python kemudian melihatnya dan berkata:
>
> `SyntaxError`
>
> Jadi kalau mau ngoding di Word, matikan *AutoFormat As You Type*. Kalau tidak, kamu akan belajar debugging dengan cara paling tradisional: marah kepada Microsoft.

---

### 5) Autocorrect sebagai "AI pair programmer" versi nenek moyang

Notepad Windows 11 sekarang punya spellcheck dan autocorrect, dan typo dapat diperbaiki otomatis kalau fiturnya aktif.[^verge][^vnreview]

Secara teknis, kamu bisa bilang:

> "Saya menggunakan AI-assisted coding."

Kalimat itu tidak sepenuhnya bohong.

Cuma AI-nya mungkin tidak tahu Python.

Contoh:

```text
Kamu mengetik:
fucntion

Notepad:
function ✅

Kamu:
"AI pair programmer."

Notepad:
"Jangan berlebihan."
```

---

### 6) Tema gelap, tab, dan penghitung karakter. Gratis pula.

Notepad versi baru Windows 11 punya penghitung karakter, mode gelap, dan tab.[^vnreview]

Fitur-fitur itu mulai digulirkan kepada pengguna Windows 11 sejak 16 Februari 2022.[^wiki-notepad]

Artinya sekarang kamu bisa ngoding dalam gelap sambil merasa seperti hacker.

Padahal yang kamu buka:

```text
catatan.txt
```

Dan isinya:

```text
TODO:
- belajar Python
- beli telur
- kenapa kode gw error
```

Minimalis.

Profesional.

Sangat mencurigakan.

---

### 7) Penguasa diam-diam statistik "Other Coders"

Dalam survei Stack Overflow 2021, Notepad++ dipakai 29,1% responden dan berada di posisi ketiga, di belakang VS Code dan Visual Studio.[^register]

Pada survei 2024, untuk kategori "Other Coders", Notepad++ bahkan menyalip Visual Studio ke posisi dua.[^vsmag]

Satu laporan sekunder menyebut Notepad++ masih berada di posisi ketiga dengan 27,4% pada 2026.[^secondtalent]

Editor sederhana ternyata tidak mati.

Dia cuma duduk di pojokan sambil menunggu IDE berikutnya meminta RAM 8 GB.

---

### 8) *Mouse-driven* sejak 1983. Hormati leluhur.

Notepad diperkenalkan Microsoft di COMDEX pada Mei 1983 sebagai editor teks berbasis mouse.[^wiki-notepad]

Versi DOS-nya, `NOTEPAD.COM`, merupakan editor ASCII sederhana yang bisa melakukan copy-paste dan memilih menu menggunakan mouse.[^toasty]

Jadi ketika kamu memakai mouse untuk ngoding, jangan merasa tertinggal.

Kamu sedang meneruskan warisan.

Kamu tidak perlu menghafal 400 shortcut Vim hanya untuk mengganti satu huruf.

Mouse:

> "Aku juga berjuang."

---

### 9) Full-stack Microsoft: frontend di Word, backend di Notepad

🧠 **Murni karangan.**

Tulis dokumentasi dan UI di Word.

Tulis logika program di Notepad.

Lalu masuk meeting dan katakan:

> "Kami menggunakan arsitektur dua-lapis monolit keluarga."

Kalau ada yang bertanya maksudnya apa, jawab:

> "Itu keputusan strategis."

Jangan elaborasi.

Semakin lama kamu menjelaskan, semakin mudah ketahuan bahwa semuanya dimulai karena malas membuka IDE.

---

### 10) *Track Changes* adalah Git yang memakai pakaian kantor

🧠 **Murni karangan.**

Mau tahu siapa yang merusak fungsi `hitung_gaji()`?

Pakai *Track Changes*.

Perubahannya kelihatan.

Warnanya ada.

Nama penulisnya ada.

Drama juga ada.

Lebih manusiawi daripada:

```bash
git blame
git rebase -i
```

yang kadang membuat seseorang menatap layar selama tujuh menit sambil mempertanyakan pilihan hidup.

> [!TIP]
> **Kalimat untuk rapat tim:**
>
> "Kami memilih stack minimalis: **Notepad + Word + keyakinan**."
>
> Kalau ditanya soal testing:
>
> "Keyakinan kami cukup kuat."

---

# 2. 🍜 Resep Ramen Kuah Tanah Liat

> [!WARNING]
> **Berhenti. Jangan benar-benar masukkan tanah ke ramen.**
>
> "Tanah liat" di sini adalah konsep rasa, warna, tekstur, serta penggunaan wadah keramik/kuali tanah liat food-grade. Tanah atau lempung sembarangan bisa terkontaminasi logam berat seperti timbal, kadmium, dan merkuri.[^geo]
>
> Tinjauan lain juga mencatat bahwa risiko geofagi dapat mencakup keracunan logam berat serta lempung yang mengikat nutrisi atau obat di dalam usus.[^geo2]
>
> Jadi yang kita makan adalah **ramen yang kelihatannya seperti tanah**.
>
> Tanahnya sendiri silakan tetap menjadi tanah. Dia sudah punya pekerjaan.

## 🏺 Latar Belakang (Biar Terlihat Berbudaya)

Resep tertulis tertua yang diketahui ditemukan pada tablet tanah liat bertuliskan aksara paku dalam Yale Babylonian Collection dan diperkirakan berasal dari sekitar 1750 SM.[^guinness]

Isinya kebanyakan berupa rebusan dan kuah.[^guinness]

Resep-resep itu pendek dan cukup samar. Kemungkinan besar resep tersebut berfungsi sebagai pengingat untuk juru masak berpengalaman, bukan tutorial "Cara Memasak untuk Pemula yang Bahkan Tidak Tahu Bawang Ada Ujungnya".[^yalenews]

Dengan kata lain, menulis resep di tanah liat sudah dilakukan manusia selama ribuan tahun.

Kita hanya mengganti rebusan kuno dengan ramen dan memberi nama **Petrichor** supaya terdengar mahal.

---

## 📋 Spesifikasi Hidangan

| Parameter | Nilai |
|---|---|
| Nama | **Ramen Lempung Petrichor** |
| Porsi | 2 mangkuk besar |
| Waktu | ± 1 jam 30 menit |
| Tingkat kesulitan | 🔥🔥🔥 (butuh kesabaran dan kuali tanah liat) |
| Warna kuah | Cokelat abu keruh, seperti lumpur habis hujan |
| Rasa | Gurih-umami, earthy, sedikit pahit-manis, asap lembut |

## 🛒 Bahan-Bahan

### Kuah "Lempung" (untuk ± 1,2 liter)

| Bahan | Takaran | Peran dalam "ilusi tanah" |
|---|---:|---|
| Air | 1,5 liter | Samudra purba |
| Jamur shiitake kering | 25 g | Umami + aroma hutan |
| Kombu | 10 g | Umami dasar, "lumut" |
| Ubi ungu | 200 g | Pengental alami + warna keruh |
| Akar gobo atau singkong | 120 g | Aroma akar tanah |
| Bawang putih panggang | 6 siung | Manis-karamel |
| Bawang hitam (*black garlic*) | 3 siung | Pahit-manis legam |
| Miso merah/hitam | 3 sdm | Tubuh kuah + fermentasi |
| Tahini/pasta wijen hitam | 2 sdm | Kekentalan lumpur |
| Teh hojicha | 1 sdm | Aroma asap-tanah |
| Kecap asin | 2 sdm | Asin |
| Kecap manis | 1 sdm | Warna + manis |
| Minyak wijen | 1 sdm | Aroma penutup |

### Mie dan Topping: "Fosil & Flora"

| Bahan | Takaran | Nama Absurd |
|---|---:|---|
| Mie ramen kering/segar | 200 g | "Akar Serabut" |
| Telur rebus 6,5 menit + kecap | 2 butir | "Fosil Telur Dinosaurus" |
| Jamur king oyster, bakar | 2 batang | "Stalagmit" |
| Jamur enoki | 1 genggam | "Akar Halus" |
| Nori | 2 lembar | "Lumut Gua" |
| Tahu goreng dadu | 100 g | "Kerikil Kali" |
| Wijen hitam sangrai | 1 sdt | "Pasir Vulkanik" |
| Minyak bawang putih goreng | 1 sdm | "Humus" |
| Daun bawang iris | secukupnya | "Rumput Perdana" |

## 👨‍🍳 Cara Membuat

| # | Langkah | Waktu |
|:-:|---|:---:|
| 1 | **Bangunkan kuali.** Panaskan kuali tanah liat perlahan dari api kecil. Jangan langsung dihajar api besar. Kuali bukan boss fight. | 5 mnt |
| 2 | Panggang bawang putih dan gobo/singkong sampai pinggirnya sedikit gosong. Masukkan ke kuali berisi air. | 10 mnt |
| 3 | Masukkan shiitake dan kombu. Angkat kombu sebelum air mendidih brutal agar kuah tidak berlendir. | 20 mnt |
| 4 | Masukkan ubi ungu. Rebus sampai sangat empuk, lalu haluskan separuhnya di dalam kuali sampai kuah berubah keruh. | 20 mnt |
| 5 | Larutkan miso, tahini, kecap asin, dan kecap manis dengan sedikit kuah panas. Tuang kembali. Jangan didihkan setelah miso masuk. | 5 mnt |
| 6 | Seduh hojicha dalam sedikit kuah panas selama 2 menit. Saring, lalu tuang. Ini bagian ketika ramen mulai berpura-pura pernah turun hujan. | 5 mnt |
| 7 | Rebus mie secara terpisah sesuai petunjuk kemasan. Tiriskan. | 4 mnt |
| 8 | Susun mie → kuah → fosil & flora → minyak bawang → minyak wijen. | 5 mnt |
| 9 | **Seremoni.** Kalau mau, buat cap nama di luar mangkuk menggunakan tanah liat air-dry. Jangan masukkan benda non-food-grade ke makanan. | ∞ |

### 📜 Resep dalam Format Tablet

Gaya resep Mesopotamia: pendek, misterius, dan membuat juru masak modern bertanya apakah ini resep atau pesan dari peradaban yang sudah punah.

```yaml
# tablet: YBC-RAMEN-0001
nama: ramen-lempung-petrichor
kuali: tanah-liat
api: kecil, lalu agak-kecil

instruksi:
  - "Panggang akar. Masukkan air."
  - "Jamur dan rumput laut, lalu tunggu."
  - "Ubi hancurkan. Kuah menjadi lumpur."
  - "Miso masuk setelah api mati."
  - "Mie terpisah. Jangan campur di kuali."

catatan:
  "Juru masak berpengalaman paham sisanya."
```

Tidak ada suhu.

Tidak ada ukuran panci.

Tidak ada foto.

Hanya keyakinan dan ribuan tahun tradisi.

> [!TIP]
> **Mau kuah terasa makin "tanah"?**
>
> Diamkan kuah tanpa miso sekitar 30 menit dengan api paling kecil. Tambahkan miso di akhir.
>
> Jangan menambahkan tanah. Kita sudah membahas ini.

> [!NOTE]
> **Kuali baru?**
>
> Ikuti petunjuk produsennya. Banyak orang merendam kuali tanah liat terlebih dahulu, termasuk dengan air cucian beras, lalu mengeringkannya. Tujuannya agar wadah lebih siap digunakan dan risiko retak karena perubahan suhu dapat dikurangi.
>
> Karena bahkan kuali pun butuh pemanasan emosional sebelum diajak kerja.

## ⭐ Review Fiktif

| Kritikus | Komentar | Bintang |
|---|---|:---:|
| Arkeolog Dapur | "Rasanya seperti menggali lapisan peradaban." | ⭐⭐⭐⭐⭐ |
| Cacing Tanah | "Kok aroma rumahku jadi enak?" | ⭐⭐⭐⭐ |
| Notepad | "Tidak ada fitur format, tapi saya terharu." | ⭐⭐⭐⭐⭐ |

---

# 3. 🪨 10 Keunggulan Mencatat Ide di Atas Batu (Dibanding Kertas & Aplikasi Apapun)

> [!NOTE]
> Bukti utama kita kali ini datang dari dua bintang arkeologi: **Batu Rosetta**, sebuah dekret tahun 196 SM yang dipahat pada granodiorit,[^rosetta-wiki] dan lukisan gua Leang Karampuang di Sulawesi Selatan yang berusia minimal 51.200 tahun.[^nbc]
>
> Jadi kalau catatan di HP hilang karena aplikasi crash, jangan panik.
>
> Leluhur kita dulu punya solusi:
>
> **ambil batu.**

## Tabel Perbandingan

| Fitur | Batu | Kertas | Aplikasi Catatan |
|---|:---:|:---:|:---:|
| Umur simpan terbukti | ≥ 51.000 tahun[^nbc] | puluhan–ratusan tahun | sampai server dimatikan |
| Butuh baterai | ❌ | ❌ | ✅ |
| Biaya langganan | Rp0 | Rp0 | Rp49.000/bln |
| Tahan banjir | ✅ | ❌ | ❌ (HP-nya) |
| Portabilitas | 💀 (1.680 pon)[^rosetta-weight] | ✅ | ✅ |
| Fitur *undo* | ❌ | ✂️ (tip-ex) | ✅ |
| Bisa jadi pondasi benteng | ✅[^rosetta-collector] | ❌ | ❌ |

## Sepuluh Keunggulannya

### 1) Daya tahan: *uptime* 51.200 tahun

Lukisan di gua Leang Karampuang, Maros-Pangkep, berusia minimal 51.200 tahun dan dibuat menggunakan pigmen merah.[^cnn]

Lebih tua daripada seni gua Eropa paling awal di El Castillo, Spanyol, yang berusia sekitar 40.800 tahun.[^canberra]

Bandingkan dengan aplikasi catatan:

```text
Kemarin:
"Tenang, ide gw sudah disimpan."

Hari ini:
Error 500.

Batu:
"Skill issue."
```

Batu tidak mengenal *server maintenance*.

Batu tidak punya *downtime*.

Batu bahkan mungkin lebih stabil daripada Wi-Fi kos.

---

### 2) Enkripsi alami gratis

Peneliti memperkirakan umur lukisan Leang Karampuang dengan menganalisis kristal kalsium karbonat yang terbentuk secara alami di atas lukisan menggunakan laser.[^canberra]

Dengan kata lain, catatan batu punya sistem keamanan yang sangat alami:

**kerak mineral.**

Tidak perlu paket Pro.

Tidak perlu OTP.

Tidak ada email:

> "Kami mendeteksi login mencurigakan."

Karena siapa yang mau login ke batu?

---

### 3) Redundansi tiga bahasa alias *multi-region backup*

Batu Rosetta memuat satu dekret dalam tiga versi: hieroglif, demotik, dan Yunani Kuno.[^rosetta-rds]

Cloud modern:

> "Data kamu sudah di-backup."

Batu Rosetta:

> "Data kamu sudah di-backup dalam tiga bahasa."

Cloud:

> "...fair."

Ini *backup strategy* yang tidak membutuhkan server kedua.

Hanya membutuhkan batu yang beratnya mengganggu punggung.

---

### 4) *Merge conflict* ternyata bukan penyakit Git

Batu Rosetta memiliki perbedaan pada penanggalan di antara versi teksnya. Versi Yunani dan hieroglif mencantumkan tanggal yang setara dengan 27 November 197 SM, sedangkan teks demotik mencantumkan hari-hari berturut-turut di bulan Maret. Alasan perbedaannya tidak pasti.[^rosetta-wiki]

Jadi bahkan penulis ribuan tahun lalu bisa mengalami:

```diff
<<<<<<< HEAD (Yunani & Hieroglif)
27 November 197 SM
=======
hari-hari berturut-turut di bulan Maret (Demotik)
>>>>>>> demotic-branch

# TODO: resolve
# status: belum selesai sejak 196 SM
```

Git melihat ini dan merasa punya teman.

---

### 5) *Engagement* tinggi: orang benar-benar datang melihatnya

Sejak 1802, Batu Rosetta dipamerkan di British Museum hampir tanpa putus dan menjadi salah satu objek yang paling banyak dikunjungi di sana.[^rosetta-rds]

Catatan Notion kamu kemarin?

```text
Views: 1
Viewer: kamu
Waktu membaca: 4 detik
Alasan membuka: lupa mau nyari apa
```

Batu Rosetta menang.

---

### 6) Anti-hapus tidak sengaja

Teks Batu Rosetta dipahat pada permukaan batu yang dipoles.[^rosetta-weight]

Tidak ada:

- `Ctrl + Z`
- tombol Backspace
- autosave yang menimpa file
- "Are you sure?"
- pemulihan versi

Sekali salah ukir, selamat.

Kamu baru saja membuat *commit* yang sangat permanen.

---

### 7) Fitur *reuse*: catatan bisa naik pangkat menjadi benteng

Batu Rosetta pernah dipindahkan dan digunakan sebagai bahan bangunan Fort Julien di dekat Rashid/Rosetta, sebelum ditemukan kembali pada 1799.[^rosetta-collector]

Catatan di kertas:

> "Mungkin nanti berguna."

Catatan di batu:

> "Saya bisa menjadi bagian dari benteng."

Itulah *career progression*.

---

### 8) *Forward compatibility* absurdly bagus

Batu Rosetta membantu Champollion dan Thomas Young dalam memahami hieroglif, membuka akses ke sejarah Mesir kuno yang sebelumnya sulit dibaca.[^rosetta-collector]

Batu bisa dibaca ribuan tahun kemudian.

Sementara file `.docx` dari tahun 2003 kadang dibuka dan langsung berkata:

> "Format ini mungkin tidak didukung."

Batu:

> "Silakan."

---

### 9) Berat sebagai fitur keamanan

Batu Rosetta memiliki berat sekitar 1.680 pon atau sekitar 760 kg.[^rosetta-weight]

Hacker:

> "Saya akan mencuri datanya."

Juga hacker:

> "Tapi batunya 760 kilo."

Ini bukan *portable storage*.

Ini **physical security**.

> [!CAUTION]
> Jangan mencatat ide di batu saat naik KRL.
>
> Bukan karena tidak keren.
>
> Karena kemungkinan besar lututmu akan mengajukan surat pengunduran diri.

---

### 10) Format bercerita yang sudah bertahan sangat lama

Peneliti menafsirkan lukisan Leang Karampuang sebagai adegan naratif. Jika interpretasi tersebut benar, lukisan itu merupakan bukti sangat awal dari seni bercerita.[^nbc]

Jadi kalau kamu menulis ide cerita di batu, kamu secara harfiah ikut meneruskan tradisi yang sangat tua.

Kalau idenya jelek?

Tidak masalah.

Sekarang idenya jelek **secara permanen**.

> [!NOTE]
> **Sedikit rem pada kereta absurd:**
>
> Arkeolog Paul Pettitt dari Durham University menyebut penafsiran lukisan tersebut sebagai narasi sebagai semacam "lompatan keyakinan", walaupun penanggalannya sendiri dinilai kuat.[^cnn]
>
> Jadi:
>
> **Umurnya kuat. Tafsir ceritanya masih diperdebatkan.**
>
> Kita boleh bercanda, tapi arkeologi tetap bukan fanfiction.

---

## 🔧 Panduan Praktis (Opsional, Sangat Tidak Disarankan)

Kalau benar-benar ingin membuat aplikasi catatan berbasis batu, berikut API fiktifnya:

```bash
# StoneNote v0.0.1-alpha
$ pahat --tulis "ide: ramen kuah tanah liat" \
        --media granodiorit \
        --undo=false \
        --sync=tidak-ada \
        --backup=anak-cucu

[OK] Catatan tersimpan.
[OK] Estimasi umur: ≥ 2.000 tahun.
[OK] Cloud sync: tidak tersedia.
[OK] Undo: tidak tersedia.
[WARN] Berat: 3 kg.
[WARN] Punggung: mulai mempertanyakan keputusanmu.
```

Kalau lupa password?

Tidak ada.

Kalau lupa isi catatan?

Ada.

Bacalah batu itu.

> [!WARNING]
> **Jangan memahat situs gua, prasasti, batu cagar budaya, atau benda bersejarah.**
>
> Pakai batu milik sendiri kalau memang ingin bereksperimen. Gua dan artefak bersejarah adalah warisan bersama, dan goresan baru bisa merusak informasi yang sedang diteliti para arkeolog.
>
> Kita sedang bercanda tentang teknologi batu, bukan mengajukan diri sebagai musuh arkeologi.

---

# 📚 Daftar Pustaka

**Bagian 1 — Notepad, Word, dan IDE**

[^wiki-notepad]: Wikipedia, "Windows Notepad". https://en.wikipedia.org/wiki/Windows_Notepad
[^everand]: Everand, "Notepad" (5 Jan 2022). https://everand.com/article/551938289/Notepad
[^verge]: Ringkasan artikel The Verge via APH Networks, "Microsoft's Notepad gets spellcheck and autocorrect 40 years after launch". https://aphnetworks.com/news/28933-microsofts-notepad-gets-spellcheck-and-autocorrect-40-years-after-launch
[^xataka]: Xataka Colombia, "Microsoft y las deudas pendientes: por qué el bloc de notas es el gran olvidado de Windows". https://www.xataka.com.co/historia-tecnologica/microsoft-y-las-deudas-pendientes-por-que-el-bloc-de-notas-es-el-gran-olvidado-de-windows
[^vnreview]: VnReview, "40 năm kể từ ngày ra mắt, Notepad được cập nhật tính năng kiểm tra và tự sửa lỗi chính tả". https://vnreview.vn/threads/40-nam-ke-tu-ngay-ra-mat-notepad-duoc-cap-nhat-tinh-nang-kiem-tra-va-tu-sua-loi-chinh-ta.44204/
[^toasty]: Toasty Tech, "Microsoft Mouse – 1983". https://toastytech.com/guis/msmouse.html
[^register]: The Register, "Stack Overflow survey: Microsoft IDEs dominate…" (3 Agu 2021). https://www.theregister.com/2021/08/03/stackoverflow_developer_survey/
[^vsmag]: Visual Studio Magazine, "Stack Overflow Dev Survey: VS Code, Visual Studio and .NET Shine" (26 Jul 2024). https://visualstudiomagazine.com/articles/2024/07/26/so-dev-survey.aspx
[^secondtalent]: Second Talent, "Most used IDEs" (sumber sekunder, angka 2026). https://www.secondtalent.com/resources/most-used-ides/

**Bagian 2 — Ramen Tanah Liat**

[^guinness]: Guinness World Records, "Oldest written recipes". https://www.guinnessworldrecords.com/world-records/630174-oldest-written-recipes
[^yalenews]: Yale News, "What did ancient Babylonians eat? A Yale-Harvard team tested their recipes" (14 Jun 2018). https://news.yale.edu/2018/06/14/what-did-ancient-babylonians-eat-yale-harvard-team-tested-their-recipes
[^geo]: Bonglaisin dkk., "Geophagia: Benefits and potential toxicity to human—A review", *Frontiers in Public Health* 10:893831 (2022). https://pmc.ncbi.nlm.nih.gov/articles/PMC9360771/
[^geo2]: "Medicine Beneath Your Feet: A Biocultural Examination of the Risks and Benefits of Geophagy", *Clays and Clay Minerals*. https://researchconnect.buffalo.edu/en/publications/medicine-beneath-your-feet-a-biocultural-examination-of-the-risks/

**Bagian 3 — Catatan di Batu**

[^rosetta-wiki]: Wikipedia, "Rosetta Stone". https://en.wikipedia.org/wiki/Rosetta_Stone
[^rosetta-collector]: TheCollector, "Rosetta Stone – 196 BC" (rekaman objek, merujuk koleksi British Museum). https://www.thecollector.com/gallery/rosetta-stone/
[^rosetta-rds]: RDS Digital Archive, "Inscription on the Rosetta Stone". https://digitalarchive.rds.ie/items/show/6018
[^rosetta-weight]: "Calendar: July 15" (data dimensi dan berat Rosetta, sumber sekunder berbentuk blog; konversi ke kg adalah perkiraan). https://ultrawolvesunderthefullmoon.blog/tag/rosetta-stone/
[^nbc]: NBC News, "World's oldest cave painting is at least 51,200 years old, scientists say" (3 Jul 2024). https://www.nbcnews.com/news/rcna160304
[^cnn]: CNN, "A cave drawing of human figures and a pig is the world's oldest known narrative art" (4 Jul 2024). https://amp.cnn.com/cnn/2024/07/04/science/indonesia-oldest-narrative-cave-art-scn-intl-hnk
[^canberra]: Canberra Times, "Oldest known painting discovered in Indonesian cave". https://www.canberratimes.com.au/story/8684781/oldest-known-painting-discovered-in-indonesian-cave/

---

> [!TIP]
> Dibuat dengan penuh dedikasi, sedikit akal sehat, banyak referensi, dan keputusan hidup yang questionable.
>
> **Selamat bereksperimen. Jangan makan tanah. Jangan pahat cagar budaya. Jangan install IDE 17 GB hanya untuk mencetak "Hello World".** 🍜🪨
