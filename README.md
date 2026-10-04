### Ngoding di Word & Notepad, Ramen Kuah Tanah Liat, dan Mencatat Ide di Atas Batu

> [!NOTE]
> Dokumen ini **absurd secara sengaja**, tapi fakta-fakta pendukungnya **beneran ada sumbernya** (lihat catatan kaki `[^n]` dan daftar pustaka di bawah). Kalau ada klaim tanpa catatan kaki, anggap itu 🧠 *murni karangan penuh percaya diri*.

## 📑 Daftar Isi

1. [10 Keunggulan Ngoding di MS Word & Notepad vs IDE Apapun](#1--10-keunggulan-ngoding-di-ms-word--notepad-vs-ide-apapun)
2. [Resep Ramen Kuah Tanah Liat](#2--resep-ramen-kuah-tanah-liat)
3. [10 Keunggulan Mencatat Ide di Atas Batu](#3--10-keunggulan-mencatat-ide-di-atas-batu)
4. [Daftar Pustaka](#-daftar-pustaka)

---

# 1. 💻 10 Keunggulan Ngoding di MS Word & Notepad vs IDE Apapun

> [!IMPORTANT]
> **Disclaimer silsilah:** *Notepad* ≠ *Notepad++*. Yang pertama bawaan Windows, yang kedua editor open-source pihak ketiga. Di bagian statistik, yang dihitung survei developer adalah **Notepad++**, jadi anggap dia "sepupu jauh yang lagi viral".

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
Notepad pertama muncul di MS-DOS tahun 1983, ikut hadir di Windows 1 dua tahun setelahnya, dan sejak itu nongol di setiap versi Windows.[^everand][^wiki-notepad] Sementara IDE minta kamu download berGB-GB dulu baru boleh bikin `Hello World`.

```bash
# Memasang Notepad
$ (tidak ada langkah)

# Memasang IDE
$ download 2.4GB ... 38% ... "Pilih workload" ... "Restart komputer" ... "Sign in"
```

### 2) Tidak ada *dependency hell* karena tidak ada dependency
Notepad hanya menawarkan manipulasi teks paling dasar, misalnya cari dan ganti.[^wiki-notepad] Satu-satunya "ekosistem plugin"-nya adalah menu *Format → Font*, yang menurut Xataka kira-kira fitur paling canggih di aplikasi paling dasar Windows.[^xataka] `package.json` kamu isinya:

```json
{}
```

### 3) Satu keluarga: Word dan Notepad itu kakak-adik
Tahun 1983, Microsoft memperkenalkan *Multi-Tool Notepad* dan *Multi-Tool Word*; namanya kemudian dipangkas jadi Notepad dan Microsoft Word.[^wiki-notepad] Notepad bahkan awalnya dibuat sebagai versi ringkas dari Word.[^verge] Jadi ngoding di keduanya itu **reuni keluarga**, bukan sekadar kerja.

| Tahun | Peristiwa | Sumber |
|:---:|---|:---:|
| 1983 | Multi-Tool Notepad & Multi-Tool Word lahir | [^wiki-notepad] |
| 1985 | Word (waktu itu masih Multi-Tool Word) dapat spellcheck pertamanya | [^verge] |
| 2024 | Notepad akhirnya dapat spellcheck & autocorrect, 40+ tahun setelah rilis | [^verge] |

### 4) *Syntax highlighting* manual pakai stabilo Word
Notepad dulu dicabut kemampuan bold/underline/miringnya, jadi murni teks polos.[^wiki-notepad] Word masih punya semuanya. Maka di Word kamu bisa membuat tema warna sendiri **tanpa instal extension**: *keyword* tebal, string digarisbawahi, komentar disorot stabilo kuning.

```python
# Contoh highlight manual ala Word (bayangkan ini ada warnanya)
**def** halo():          # <- "def" ditebalkan dengan Ctrl+B
    print(__"halo dunia"__)  # <- string digarisbawahi dengan Ctrl+U
```

> [!WARNING]
> **Jebakan Smart Quotes.** Word suka mengubah tanda kutip lurus `"` jadi kutip melengkung `“ ”`. Interpreter Python akan menanggapinya dengan `SyntaxError` dan tatapan dingin. Matikan *AutoFormat As You Type*, atau terima nasib sebagai pendekar *debugging* tingkat dewa.

### 5) Autocorrect sebagai "AI pair programmer" versi retro
Notepad Windows 11 kini punya spellcheck dan autocorrect, dan typo otomatis diperbaiki kalau fiturnya aktif.[^verge][^vnreview] Kamu bisa mengklaim ke atasan bahwa kamu **"pakai AI-assisted coding"**. Teknisnya tidak bohong-bohong amat.

```text
Ilustrasi (bukan hasil uji, murni khayalan):
ketik   : fucntion
koreksi : function   ✅  "AI pair programming"
```

### 6) Tema gelap, tab, dan hitung karakter, tanpa langganan
Notepad versi baru Windows 11 punya penghitung karakter, mode gelap, dan tab.[^vnreview] Desainnya mulai digulirkan ke semua pengguna Windows 11 sejak 16 Februari 2022.[^wiki-notepad] Artinya kamu bisa ngoding di mode gelap **sambil tetap merasa minimalis**.

### 7) Penguasa diam-diam statistik "Other Coders"
Di survei Stack Overflow 2021, Notepad++ dipakai 29,1% responden dan menempati peringkat tiga, di belakang VS Code dan Visual Studio.[^register] Di survei 2024, untuk kategori "Other Coders", Notepad++ bahkan menyalip Visual Studio ke posisi dua.[^vsmag] Satu laporan sekunder menyebut Notepad++ masih ada di peringkat tiga (27,4%) untuk 2026.[^secondtalent] Editor sederhana ternyata tidak punah, **dia hanya sabar**.

### 8) *Mouse-driven* sejak 1983 (hormat pada leluhur)
Notepad lahir sebagai editor teks berbasis mouse yang diperkenalkan Microsoft di COMDEX Mei 1983.[^wiki-notepad] Versi DOS-nya (`NOTEPAD.COM`) adalah editor ASCII sederhana yang bisa copy-paste dan pilih menu pakai mouse.[^toasty] Ngoding di Notepad berarti kamu meneruskan tradisi kuno, **tidak perlu hafal 400 shortcut Vim**.

### 9) Full-stack Microsoft: *frontend* di Word, *backend* di Notepad
🧠 *Murni karangan.* Tulis dokumentasi dan UI di Word, tulis logika di Notepad, lalu kabari tim bahwa kamu menerapkan **arsitektur dua-lapis monolit keluarga** (lihat poin 3).

### 10) *Track Changes* adalah Git versi ramah manusia
🧠 *Murni karangan.* Mau lihat siapa yang merusak fungsi `hitung_gaji()`? Fitur *Track Changes* menandai perubahan dengan warna berbeda per penulis. Lebih estetik dari `git blame`, dan tidak butuh `git rebase -i` yang bikin keringat dingin.

> [!TIP]
> **Rangkuman untuk rapat tim:** "Kami memilih stack minimalis: *Notepad + Word + keyakinan*."

---

# 2. 🍜 Resep Ramen Kuah Tanah Liat

> [!WARNING]
> **Baca dulu, ini serius.** Resep ini **tidak memakai tanah beneran**. "Tanah liat" di sini berasal dari (a) **wadah masak keramik/kuali tanah liat food-grade** dan (b) warna, tekstur, serta aroma kuahnya yang meniru lempung hujan. Memakan tanah/lempung sembarangan itu **berisiko**: tinjauan ilmiah mencatat bahwa tanah liat bisa terkontaminasi logam berat seperti timbal, kadmium, dan merkuri.[^geo] Tinjauan lain menyebut risiko terbesar geofagi adalah keracunan logam berat dan lempung yang mengikat nutrisi atau obat di usus.[^geo2]

## 🏺 Latar Belakang (Biar Terlihat Berbudaya)

Resep tertulis tertua yang diketahui ada di tablet tanah liat cuneiform dalam Yale Babylonian Collection, diperkirakan berasal dari sekitar 1750 SM.[^guinness] Isinya kebanyakan rebusan dan kuah.[^guinness] Resep-resepnya pendek dan samar, tampaknya sebagai pengingat bagi juru masak berpengalaman, bukan panduan untuk pemula.[^guinness][^yalenews] Jadi, **menuliskan resep kuah di atas tanah liat adalah tradisi 3.700 tahun**. Resep ini cuma meneruskannya.

## 📋 Spesifikasi Hidangan

| Parameter | Nilai |
|---|---|
| Nama | **Ramen Lempung Petrichor** |
| Porsi | 2 mangkuk besar |
| Waktu | ± 1 jam 30 menit |
| Tingkat kesulitan | 🔥🔥🔥 (butuh kesabaran dan kuali tanah liat) |
| Warna kuah | Cokelat abu keruh, mirip lumpur sehabis hujan |
| Rasa | Gurih-umami, earthy, sedikit pahit-manis, asap lembut |

## 🛒 Bahan-Bahan

### Kuah "Lempung" (untuk ± 1,2 liter)

| Bahan | Takaran | Peran dalam "ilusi tanah" |
|---|---|---|
| Air | 1,5 liter | Samudra purba |
| Jamur shiitake kering | 25 g | Sumber umami dan aroma hutan |
| Kombu (rumput laut kering) | 10 g | Umami dasar, "lumut" |
| Ubi ungu | 200 g (kupas, potong dadu) | Pengental alami dan warna keruh |
| Akar gobo (bardan) atau singkong | 120 g | Aroma akar tanah (kalau gobo susah, pakai singkong) |
| Bawang putih panggang | 6 siung | Manis-karamel |
| Bawang hitam (*black garlic*) | 3 siung | Pahit-manis legam |
| Miso merah (atau miso hitam) | 3 sdm | Tubuh kuah, rasa fermentasi dalam |
| Tahini / pasta wijen hitam | 2 sdm | Kekentalan lumpur, aroma wijen |
| Teh hojicha (teh panggang) | 1 sdm | Aroma asap-tanah, "petrichor" |
| Kecap asin | 2 sdm | Asin |
| Kecap manis | 1 sdm | Warna dan manis |
| Minyak wijen | 1 sdm | Aroma penutup |

### Mie dan Topping ("Fosil & Flora")

| Bahan | Takaran | Nama Absurd |
|---|---|---|
| Mie ramen kering/segar | 200 g | "Akar Serabut" |
| Telur rebus 6,5 menit, rendam kecap | 2 butir | "Fosil Telur Dinosaurus" |
| Jamur king oyster, bakar | 2 batang | "Stalagmit" |
| Jamur enoki | 1 genggam | "Akar Halus" |
| Nori | 2 lembar | "Lumut Gua" |
| Tahu goreng potong dadu | 100 g | "Kerikil Kali" |
| Wijen hitam sangrai | 1 sdt | "Pasir Vulkanik" |
| Minyak bawang putih goreng | 1 sdm | "Humus" |
| Daun bawang iris | secukupnya | "Rumput Perdana" |

## 👨‍🍳 Cara Membuat

| # | Langkah | Waktu |
|:-:|---|:-:|
| 1 | **Temper kuali.** Kuali tanah liat dipanaskan bertahap dari api sangat kecil. Jangan langsung api besar karena risiko retak akibat kejut suhu. | 5 mnt |
| 2 | Panggang bawang putih dan gobo/singkong sampai pinggirannya gosong tipis, lalu masukkan ke kuali berisi air. | 10 mnt |
| 3 | Masukkan shiitake dan kombu. **Angkat kombu sebelum air mendidih kencang** supaya tidak lendir. | 20 mnt |
| 4 | Masukkan ubi ungu, rebus sampai sangat empuk, lalu haluskan separuhnya langsung di kuali sampai kuah mengental keruh. | 20 mnt |
| 5 | Larutkan miso, tahini, kecap asin, kecap manis dalam sedikit kuah panas, lalu tuang kembali. **Jangan didihkan setelah miso masuk** supaya aromanya tidak hilang. | 5 mnt |
| 6 | Seduh hojicha 1 sdm dalam sedikit kuah panas selama 2 menit, saring, tuang. Ini rahasia aroma tanah sehabis hujan. | 5 mnt |
| 7 | Rebus mie terpisah sesuai petunjuk kemasan, tiriskan. | 4 mnt |
| 8 | Rakit di mangkuk keramik: mie → kuah → topping "fosil & flora" → siram minyak bawang dan minyak wijen. | 5 mnt |
| 9 | **Seremoni.** Cap nama kamu di tepi mangkuk ala tablet cuneiform (opsional, pakai tusuk sate di tanah liat air-dry **sebagai hiasan di luar mangkuk, bukan di dalam makanan**). | ∞ |

### 📜 Resep dalam Format Tablet (Gaya Bahasa Resep Mesopotamia: Singkat dan Misterius)

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
catatan: "Juru masak berpengalaman paham sisanya."
```

> [!TIP]
> **Rahasia kuah lebih "tanah":** diamkan kuah (tanpa miso) 30 menit dengan api paling kecil. Semakin lama, semakin pekat warna dan aromanya. Tambahkan miso di akhir saja.

> [!NOTE]
> **Kuali baru?** Rendam dulu dalam air (banyak orang memakai air cucian beras) lalu keringkan sebelum dipakai, agar pori-porinya siap dan tidak mudah retak. Ikuti petunjuk produsen kualimu.

## ⭐ Review Fiktif

| Kritikus | Komentar | Bintang |
|---|---|:---:|
| Arkeolog Dapur | "Rasanya seperti menggali lapisan peradaban." | ⭐⭐⭐⭐⭐ |
| Cacing Tanah | "Kok aroma rumahku jadi enak?" | ⭐⭐⭐⭐ |
| Notepad | "Tidak ada fitur format, tapi saya terharu." | ⭐⭐⭐⭐⭐ |

---

# 3. 🪨 10 Keunggulan Mencatat Ide di Atas Batu (Dibanding Kertas & Aplikasi Apapun)

> [!NOTE]
> Bukti-bukti di bawah ini diambil dari dua bintang arkeologi: **Batu Rosetta** (dekret tahun 196 SM, granodiorit)[^rosetta-wiki] dan **lukisan gua Leang Karampuang, Sulawesi Selatan** (minimal 51.200 tahun).[^nbc]

## Tabel Perbandingan

| Fitur | Batu | Kertas | Aplikasi Catatan |
|---|:---:|:---:|:---:|
| Umur simpan terbukti | ≥ 51.000 tahun[^nbc] | puluhan–ratusan tahun | sampai server dimatikan |
| Butuh baterai | ❌ | ❌ | ✅ |
| Biaya langganan | Rp0 | Rp0 | Rp49.000/bln |
| Tahan banjir | ✅ | ❌ | ❌ (HP-nya) |
| Portabilitas | 💀 (1.680 pon)[^rosetta-weight] | ✅ | ✅ |
| Fitur *undo* | ❌ | ✂️ (tipp-ex) | ✅ |
| Bisa jadi pondasi benteng | ✅[^rosetta-collector] | ❌ | ❌ |

## Sepuluh Keunggulannya

### 1) Daya tahan: *uptime* 51.200 tahun
Lukisan dinding di gua Leang Karampuang, Maros-Pangkep, berusia minimal 51.200 tahun dan digambar dengan pigmen merah.[^cnn] Itu lebih tua dari seni gua Eropa yang paling awal di El Castillo, Spanyol, sekitar 40.800 tahun.[^canberra] Bandingkan dengan aplikasi catatanmu yang *error 500* kemarin sore.

### 2) Enkripsi alami gratis
Peneliti memperkirakan umur lukisan itu dengan mendatangkan laser untuk menganalisis kristal kalsium karbonat yang terbentuk alami di atas lukisan.[^canberra] Jadi catatanmu otomatis **dilapisi kerak mineral** tanpa langganan keamanan premium.

### 3) Redundansi tiga bahasa (*multi-region backup*)
Batu Rosetta memuat satu dekret dalam tiga versi: hieroglif, demotik, dan Yunani Kuno.[^rosetta-rds] Itulah *backup* lintas wilayah yang tidak bisa ditandingi cloud manapun.

### 4) *Merge conflict* sudah dikenal sejak 196 SM
Versi Yunani dan hieroglif Rosetta mencantumkan tanggal peringatan penobatan yang setara dengan 27 November 197 SM, sedangkan teks demotik mencantumkan hari-hari berturut-turut di bulan Maret. Alasan perbedaannya tidak pasti.[^rosetta-wiki] Artinya, **tim penulis batu pun mengalami *conflict* antarbranch**. Kamu tidak sendirian.

```diff
<<<<<<< HEAD (Yunani & Hieroglif)
27 November 197 SM
=======
hari-hari berturut-turut di bulan Maret (Demotik)
>>>>>>> demotic-branch
# TODO: resolve (status: belum selesai sejak 196 SM)
```

### 5) *Engagement* tertinggi: objek paling banyak dikunjungi
Sejak 1802, Rosetta dipamerkan di British Museum hampir tanpa putus dan menjadi objek yang paling banyak dikunjungi di sana.[^rosetta-rds] Berapa *views* catatan Notion-mu kemarin?

### 6) Anti-hapus tidak sengaja
Teks Rosetta dipahat (diukir) pada permukaan yang dipoles, bukan sekadar ditulis.[^rosetta-weight] Tidak ada tombol *Backspace*, tidak ada *auto-save* yang menimpa ide brilianmu dengan draf kosong.

### 7) Fitur *reuse*: catatan jadi pondasi benteng
Batu Rosetta dulu dipindahkan dan dipakai sebagai bahan bangunan Fort Julien di dekat kota Rashid (Rosetta) di Delta Nil, lalu ditemukan di sana tahun 1799.[^rosetta-collector] Catatanmu di kertas paling banter jadi alas gorengan. Catatan di batu **naik jabatan jadi struktur militer**.

### 8) Kompatibilitas ke depan (*forward compatibility*)
Rosetta menjadi kunci bagi Champollion dan Thomas Young untuk memecahkan hieroglif, membuka tiga milenium sejarah Mesir yang sempat bisu.[^rosetta-collector] Format batu dapat dibaca ulang setelah ribuan tahun, bahkan oleh orang yang tidak tahu bahasanya. File `.docx` dari 2003 saja kadang sudah ngambek.

### 9) Berat sebagai fitur keamanan
Batu Rosetta beratnya sekitar 1.680 pon (± 760 kg).[^rosetta-weight] Tidak ada hacker yang bisa mencurinya dari kafe sambil kabur naik motor. Ini bukan masalah portabilitas, ini **proteksi fisik tingkat militer**.

> [!CAUTION]
> Fitur "berat" ini juga berarti kamu **tidak boleh** mencatat idemu *saat naik KRL*.

### 10) Format bercerita yang sudah terbukti
Peneliti menafsirkan lukisan Leang Karampuang sebagai adegan naratif, yang jika benar adalah bukti tertua seni bercerita.[^nbc] Jadi, kalau kamu menulis ide di batu, kamu sedang melanjutkan **tradisi bercerita tertua umat manusia**. Bahkan resep kuliner tertua pun ditulis di tanah liat (lihat bagian 2).[^guinness]

> [!NOTE]
> **Catatan skeptis:** arkeolog Paul Pettitt dari Durham University menyebut penafsiran lukisan itu sebagai narasi sebagai semacam "lompatan keyakinan", sekalipun penanggalannya sendiri dinilai kuat.[^cnn] Jadi mari kita bilang: *umurnya valid, tafsir ceritanya masih diperdebatkan*.

## 🔧 Panduan Praktis (Opsional, Sangat Tidak Disarankan)

```bash
# Aplikasi "StoneNote" versi CLI (fiktif)
$ pahat --tulis "ide: ramen kuah tanah liat" \
        --media granodiorit \
        --undo=false \
        --sync=tidak-ada \
        --backup=anak-cucu

[OK] Catatan tersimpan. Estimasi umur: ≥ 2.000 tahun. Berat: 3 kg. Punggung: nyeri.
```

> [!WARNING]
> **Jangan memahat atau mencoret situs gua, prasasti, atau batu cagar budaya.** Pakai batu kali milikmu sendiri di halaman rumah. Gua dan batu bersejarah itu warisan bersama, dan menggoresnya bisa merusak data yang dicari para peneliti.

---

# 📚 Daftar Pustaka

**Bagian 1 (Notepad, Word, dan IDE)**

[^wiki-notepad]: Wikipedia, "Windows Notepad". https://en.wikipedia.org/wiki/Windows_Notepad
[^everand]: Everand, "Notepad" (5 Jan 2022). https://everand.com/article/551938289/Notepad
[^verge]: Ringkasan artikel The Verge via APH Networks, "Microsoft's Notepad gets spellcheck and autocorrect 40 years after launch". https://aphnetworks.com/news/28933-microsofts-notepad-gets-spellcheck-and-autocorrect-40-years-after-launch
[^xataka]: Xataka Colombia, "Microsoft y las deudas pendientes: por qué el bloc de notas es el gran olvidado de Windows". https://www.xataka.com.co/historia-tecnologica/microsoft-y-las-deudas-pendientes-por-que-el-bloc-de-notas-es-el-gran-olvidado-de-windows
[^vnreview]: VnReview, "40 năm kể từ ngày ra mắt, Notepad được cập nhật tính năng kiểm tra và tự sửa lỗi chính tả". https://vnreview.vn/threads/40-nam-ke-tu-ngay-ra-mat-notepad-duoc-cap-nhat-tinh-nang-kiem-tra-va-tu-sua-loi-chinh-ta.44204/
[^toasty]: Toasty Tech, "Microsoft Mouse – 1983". https://toastytech.com/guis/msmouse.html
[^register]: The Register, "Stack Overflow survey: Microsoft IDEs dominate…" (3 Agu 2021). https://www.theregister.com/2021/08/03/stackoverflow_developer_survey/
[^vsmag]: Visual Studio Magazine, "Stack Overflow Dev Survey: VS Code, Visual Studio and .NET Shine" (26 Jul 2024). https://visualstudiomagazine.com/articles/2024/07/26/so-dev-survey.aspx
[^secondtalent]: Second Talent, "Most used IDEs" (sumber sekunder, angka 2026). https://www.secondtalent.com/resources/most-used-ides/

**Bagian 2 (Ramen Tanah Liat)**

[^guinness]: Guinness World Records, "Oldest written recipes". https://www.guinnessworldrecords.com/world-records/630174-oldest-written-recipes
[^yalenews]: Yale News, "What did ancient Babylonians eat? A Yale-Harvard team tested their recipes" (14 Jun 2018). https://news.yale.edu/2018/06/14/what-did-ancient-babylonians-eat-yale-harvard-team-tested-their-recipes
[^geo]: Bonglaisin dkk., "Geophagia: Benefits and potential toxicity to human—A review", *Frontiers in Public Health* 10:893831 (2022). https://pmc.ncbi.nlm.nih.gov/articles/PMC9360771/
[^geo2]: "Medicine Beneath Your Feet: A Biocultural Examination of the Risks and Benefits of Geophagy", *Clays and Clay Minerals*. https://researchconnect.buffalo.edu/en/publications/medicine-beneath-your-feet-a-biocultural-examination-of-the-risks/

**Bagian 3 (Catatan di Batu)**

[^rosetta-wiki]: Wikipedia, "Rosetta Stone". https://en.wikipedia.org/wiki/Rosetta_Stone
[^rosetta-collector]: TheCollector, "Rosetta Stone – 196 BC" (rekaman objek, merujuk koleksi British Museum). https://www.thecollector.com/gallery/rosetta-stone/
[^rosetta-rds]: RDS Digital Archive, "Inscription on the Rosetta Stone". https://digitalarchive.rds.ie/items/show/6018
[^rosetta-weight]: "Calendar: July 15" (data dimensi dan berat Rosetta, sumber sekunder berbentuk blog; konversi ke kg adalah perkiraan). https://ultrawolvesunderthefullmoon.blog/tag/rosetta-stone/
[^nbc]: NBC News, "World's oldest cave painting is at least 51,200 years old, scientists say" (3 Jul 2024). https://www.nbcnews.com/news/rcna160304
[^cnn]: CNN, "A cave drawing of human figures and a pig is the world's oldest known narrative art" (4 Jul 2024). https://amp.cnn.com/cnn/2024/07/04/science/indonesia-oldest-narrative-cave-art-scn-intl-hnk
[^canberra]: Canberra Times, "Oldest known painting discovered in Indonesian cave". https://www.canberratimes.com.au/story/8684781/oldest-known-painting-discovered-in-indonesian-cave/

---

> [!TIP]
> *Dibuat dengan penuh dedikasi, sedikit akal sehat, dan banyak referensi. Selamat bereksperimen, tapi tolong, jangan makan tanahnya.* 🍜🪨
