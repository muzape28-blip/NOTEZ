# Panduan Markdown NOTEZ

Markdown itu cara memberi sedikit "petunjuk" pada teks biasa. Tiga tanda pagar bisa menjadi judul, sepasang bintang bisa menebalkan kata, dan tanda `-` bisa mengubah kalimat menjadi daftar. Kamu tetap menulis dengan keyboard seperti biasa; nanti NOTEZ yang merapikan hasilnya di mode baca (Reading View) lewat Markdown Preview v4.

Kalau baru mulai, tidak perlu menghafal semuanya. Coba tulis catatan kecil dulu, masuk ke mode baca, lalu kembali ke bagian panduan yang hasilnya belum sesuai harapan. Setiap bagian di bawah berusaha menjawab tiga hal: kapan syntax itu berguna, apa yang perlu diketik, dan jebakan kecil apa yang biasanya membuat preview terlihat berbeda.

Panduan ini dibuat untuk pemakaian sehari-hari di layar ponsel, bukan untuk membuatmu menjadi ahli spesifikasi Markdown. Contohnya sengaja memakai situasi yang dekat dengan NOTEZ: rencana kerja, catatan meeting, belajar, jurnal, snippet, dan daftar belanja. Kalau kamu sedang buru-buru, mulai dari **Cheat sheet cepat**, lalu baca bagian detailnya saat memang dibutuhkan.

**Pola belajar yang paling enak:**

1. Tulis isi catatan dulu tanpa memikirkan format.
2. Tandai struktur besarnya dengan heading, paragraf, dan list.
3. Tambahkan penekanan, link, atau code block hanya di bagian yang membantu pembaca.
4. Buka mode baca untuk mengecek jarak, panjang baris, dan apakah syntax-nya benar-benar terbaca.

Di beberapa bagian, contoh dibuat lebih panjang dari yang akan kamu tulis sehari-hari supaya hubungan antara syntax dan hasilnya mudah terlihat. Kamu bebas mengganti semua isinya dengan konteksmu sendiri.

## Daftar isi

- [Mulai dari sini](#panduan-markdown-notez)
- [1. Cheat sheet cepat](#1-cheat-sheet-cepat)
- [2. Teks tebal](#2-teks-tebal)
- [3. Teks miring](#3-teks-miring)
- [4. Tebal + miring](#4-tebal-miring)
- [5. Coret (strikethrough)](#5-coret-strikethrough)
- [5a. Highlight, superscript, dan subscript](#5a-highlight-superscript-dan-subscript)
- [6. Heading / judul](#6-heading-judul)
- [7. Paragraf dan baris baru](#7-paragraf-dan-baris-baru)
- [8. Garis pemisah (horizontal rule)](#8-garis-pemisah-horizontal-rule)
- [9. Bullet list dan numbered list](#9-bullet-list-dan-numbered-list)
- [10. Checklist / task list](#10-checklist-task-list)
- [11. Quote](#11-quote)
- [12. Callout](#12-callout)
- [13. Link dan bare URL](#13-link-dan-bare-url)
- [14. Inline code](#14-inline-code)
- [15. Code block](#15-code-block)
- [15a. Code block untuk berbagai bahasa pemrograman](#15a-code-block-untuk-berbagai-bahasa-pemrograman)
- [15b. Tips menulis catatan yang enak dibaca di layar kecil](#15b-tips-menulis-catatan-yang-enak-dibaca-di-layar-kecil)
- [16. Table](#16-table)
- [16a. Footnote](#16a-footnote)
- [16b. Definition list](#16b-definition-list)
- [17. Image](#17-image)
- [18. Raw HTML](#18-raw-html)
- [19. Escape karakter Markdown](#19-escape-karakter-markdown)
- [20. Fitur Markdown umum yang TIDAK didukung NOTEZ](#20-fitur-markdown-umum-yang-tidak-didukung-notez)
- [21. Kesalahan umum dan cara memperbaikinya](#21-kesalahan-umum-dan-cara-memperbaikinya)
- [22. Pertanyaan yang sering muncul (FAQ)](#22-pertanyaan-yang-sering-muncul-faq)
- [23. Glosarium istilah](#23-glosarium-istilah)
- [24. Contoh catatan lengkap (berbagai kebutuhan)](#24-contoh-catatan-lengkap-berbagai-kebutuhan)
- [25. Latihan mandiri](#25-latihan-mandiri)
- [26. Batasan Markdown di NOTEZ (ringkasan)](#26-batasan-markdown-di-notez-ringkasan)
- [27. Sumber referensi](#27-sumber-referensi)

---

## 1. Cheat sheet cepat

| Mau bikin | Tulis |
| --- | --- |
| Tebal | `**teks**` |
| Miring | `*teks*` |
| Tebal + miring | `***teks***` |
| Coret | `~~teks~~` |
| Highlight/sorot | `==teks==` atau `<mark>teks</mark>` |
| Superscript | `x^2^` atau `x<sup>2</sup>` |
| Subscript | `H~2~O` atau `H<sub>2</sub>O` |
| Judul level 1–6 | `#` sampai `######` |
| Garis pemisah | `---` |
| Bullet list | `- item` |
| Numbered list | `1. item` |
| Checklist | `- [ ] tugas` / `- [x] selesai` |
| Quote | `> kutipan` |
| Callout | `> [!NOTE]` dst. |
| Link | `[label](https://contoh.com)` |
| Bare URL | `https://contoh.com` |
| Inline code | `` `kode` `` |
| Code block | tiga backtick, lihat bagian 15 |
| Table | `\| kolom \| kolom \|` |
| Footnote | `teks[^1]` + `[^1]: catatan` |
| Definition list | `Istilah` lalu `: definisi` |
| Alignment tabel | `:---` kiri, `:---:` tengah, `---:` kanan |
| Image | `![alt](url)` |
| Safe raw HTML kecil | `<kbd>Ctrl</kbd>`, `<mark>penting</mark>` |
| Escape | `\*teks\*` |

---

## 2. Teks tebal

**Fungsi:** menonjolkan satu kata atau frasa supaya pembaca langsung menangkapnya sebagai poin penting.

**Cara menulis:** apit teks dengan dua bintang di kedua sisi.

```md
Ini **penting** banget.
```

Yang terjadi di balik layar:

- kata `penting` diapit sepasang `**`;
- hanya bagian di dalam pasangan `**...**` yang berubah jadi tebal;
- teks di luar pasangan itu tetap teks biasa, tidak ikut terpengaruh.

Bisa langsung ditaruh di tengah kalimat, tidak harus berdiri sendiri di barisnya sendiri:

```md
Jangan lupa **backup catatan** sebelum update.
```

atau menandai beberapa bagian sekaligus dalam satu kalimat yang sama:

```md
Hari ini fokus ke **Markdown Preview v4** dan **UI polish**.
```

Bisa juga dipakai untuk menonjolkan angka atau label:

```md
Deadline rilis: **Jumat, 3 Oktober**.
Status: **Blocked** menunggu review.
```

**Kesalahan umum:**

Spasi yang menempel ke bintang membuat sebagian renderer (termasuk NOTEZ) tidak menganggapnya sebagai tebal:

```md
Salah : ** tebal **
Benar : **tebal**
```

Lupa menutup pasangan bintang membuat sisa baris ikut jadi tebal secara tidak sengaja:

```md
Salah : Ini **jadi tebal terus sampai baris habis
Benar : Ini **jadi tebal** lalu normal lagi
```

Menaruh tanda baca tepat menempel bintang penutup umumnya masih aman, tapi kalau hasilnya terlihat aneh di preview, coba pisahkan dulu untuk mengecek:

```md
Sudah **selesai**, tinggal review.
```

---

## 3. Teks miring

**Fungsi:** penekanan ringan, menandai istilah asing, judul karya, atau catatan sampingan yang nadanya lebih pelan dibanding teks tebal.

**Cara menulis:** apit dengan satu bintang di kedua sisi. Garis bawah tunggal `_teks_` biasanya juga berfungsi sama.

```md
Ini *miring*.
```

Contoh dalam kalimat:

```md
Jadwalnya masih *tentative*, belum fix.
Istilah *offline-first* berarti aplikasi tetap jalan penuh tanpa internet.
```

**Kesalahan umum:** sama seperti bold, spasi menempel ke bintang bikin miring tidak terbaca:

```md
Salah : * miring *
Benar : *miring*
```

Kalau kalimatmu memang mengandung tanda bintang literal (misalnya rumus atau simbol perkalian), pakai escape supaya tidak dianggap syntax — lihat bagian 19.

---

## 4. Tebal + miring

**Fungsi:** menggabungkan dua penekanan sekaligus untuk sesuatu yang benar-benar butuh perhatian ekstra.

**Cara menulis:** apit dengan tiga bintang di kedua sisi.

```md
Ini ***sangat penting***.
```

Bisa juga dicampur manual kalau ingin kontrol lebih rinci, misalnya cuma sebagian kata yang miring di dalam frasa tebal:

```md
**Perhatian: *jangan* hapus file ini.**
```

---

## 5. Coret (strikethrough)

**Fungsi:** menandai sesuatu yang sudah tidak berlaku atau dibatalkan, tanpa menghapusnya dari riwayat catatan — berguna untuk melacak perubahan keputusan.

**Cara menulis:** apit dengan dua tilde di kedua sisi.

```md
~~Rencana lama~~ jadi rencana baru.
```

Contoh dalam catatan revisi:

```md
Harga awal ~~Rp150.000~~ turun jadi **Rp120.000**.
Meeting ~~Senin~~ dipindah ke Rabu.
```

**Kesalahan umum:** hanya satu tilde (`~teks~`) tidak akan dianggap coret di Markdown standar — harus dua tilde di setiap sisi.

---

## 5a. Highlight, superscript, dan subscript

Markdown Preview v4 menambah syntax kecil yang berguna untuk catatan belajar, teknis, dan revisi.

### Highlight / sorot

Gunakan dua tanda sama dengan di kiri dan kanan teks:

```md
Ini ==bagian penting== yang perlu diingat.
```

Alternatif HTML aman:

```md
Ini <mark>bagian penting</mark> yang perlu diingat.
```

### Superscript

Cocok untuk pangkat kecil atau notasi sederhana:

```md
x^2^ + y^2^
```

Alternatif HTML aman:

```md
x<sup>2</sup>
```

### Subscript

Cocok untuk rumus ringan seperti air:

```md
H~2~O
```

Alternatif HTML aman:

```md
H<sub>2</sub>O
```

Catatan: fitur ini untuk notasi ringan, bukan pengganti LaTeX/math engine penuh.

---

## 6. Heading / judul

**Fungsi:** memberi struktur pada catatan panjang, supaya bisa dipindai sekilas dan dinavigasi.

**Cara menulis:** tanda pagar `#` di awal baris. Makin banyak `#`, makin kecil levelnya, dari level 1 sampai level 6.

```md
# Judul utama
## Bagian besar
### Bagian kecil
#### Sub-bagian
##### Detail kecil
###### Catatan paling kecil
```

**Aturan penting:**

- wajib ada satu spasi setelah tanda `#` terakhir sebelum teks judul — `#Judul` tanpa spasi umumnya tidak terbaca sebagai heading;
- heading harus berada di baris sendiri, bukan di tengah paragraf;
- idealnya satu catatan cukup punya satu heading level 1 sebagai judul utama, lalu level 2 dan 3 untuk sub-bagian, supaya strukturnya tidak berantakan;
- jangan memilih level heading hanya karena ukuran hurufnya terlihat menarik. Level heading adalah peta catatan: pembaca dan daftar isi manual akan lebih mudah mengikutinya kalau urutannya masuk akal;
- kalau catatanmu pendek, judul `#` bahkan boleh dilewati. Satu atau dua heading `##` sudah cukup untuk memisahkan topik.

Di layar ponsel, struktur yang sederhana biasanya terasa paling nyaman: satu judul utama, beberapa bagian besar, lalu sub-bagian seperlunya. Kalau setiap kalimat diberi heading, catatan justru terasa seperti daftar menu dan susah dibaca sebagai cerita utuh.

Contoh struktur catatan bertingkat:

```md
# Rencana Rilis v0.1.2

## Fitur baru
### Panduan Markdown
### Perbaikan bug kecil

## Jadwal
```

---

## 7. Paragraf dan baris baru

**Fungsi:** memisahkan gagasan supaya catatan tidak jadi satu blok teks raksasa yang melelahkan dibaca.

**Cara menulis:** satu baris kosong di antara dua baris teks membuat keduanya jadi dua paragraf terpisah.

```md
Ini paragraf pertama.

Ini paragraf kedua.
```

**Kesalahan umum:** menekan Enter sekali saja (tanpa baris kosong) sering kali tidak menghasilkan baris baru yang terlihat, karena renderer menggabungkan dua baris berurutan jadi satu paragraf:

```md
Ini baris satu.
Ini baris dua.
```

Kalau ditulis seperti di atas, hasilnya kemungkinan tampil sebagai satu paragraf menyatu: "Ini baris satu. Ini baris dua." Kalau memang ingin baris baru yang terlihat jelas, selalu kasih satu baris kosong di antaranya, atau — kalau engine-nya mendukung hard break gaya CommonMark — akhiri baris dengan dua spasi sebelum Enter.

Di NOTEZ, baris kosong juga membantu saat kamu menggabungkan paragraf dengan syntax lain. Misalnya, beri jarak sebelum checklist atau code block agar pembaca langsung melihat bahwa itu bagian baru, bukan kelanjutan kalimat sebelumnya. Ini bukan sekadar aturan kosmetik: whitespace yang rapi membuat catatan lebih mudah dipindai ketika kamu sedang membaca cepat.

Kalau kamu ingin menulis alamat, daftar, atau kalimat pendek yang memang masing-masing harus berada di baris terpisah, pertimbangkan memakai list. Memaksa banyak hard break di dalam satu paragraf biasanya lebih rapuh saat isi catatan diedit lagi.

---

## 8. Garis pemisah (horizontal rule)

**Fungsi:** memisahkan bagian catatan secara visual, misalnya antara ringkasan dan detail, atau antar-topik yang tidak berhubungan langsung.

**Cara menulis:** tiga tanda hubung di baris sendiri, dikelilingi baris kosong di atas dan bawahnya.

```md
Bagian atas.

---

Bagian bawah.
```

Tiga tanda bintang (`***`) atau tiga garis bawah (`___`) di baris sendiri umumnya berfungsi sama. Yang penting tandanya konsisten dan tidak tercampur karakter lain di baris yang sama.

Garis pemisah paling berguna ketika dua bagian masih berada di catatan yang sama, tetapi tidak perlu dibaca sebagai satu alur — misalnya antara ringkasan dan log perubahan. Jangan memakainya setiap beberapa baris; heading atau satu baris kosong biasanya sudah cukup dan terasa lebih ringan. Kalau garis tiba-tiba berubah menjadi heading atau format lain, pastikan ia berdiri sendiri dan tidak ditempelkan ke teks di baris yang sama.

---

## 9. Bullet list dan numbered list

**Fungsi:** menyusun beberapa poin sejajar yang lebih mudah dipindai dibanding paragraf panjang.

**Bullet list** pakai tanda `-` di awal baris (tanda `*` juga berfungsi sama, tapi sebaiknya konsisten pakai satu jenis saja dalam satu catatan):

```md
- Ide pertama
- Ide kedua
- Ide ketiga
```

**Numbered list** pakai angka diikuti titik. Nomornya otomatis berurutan waktu ditampilkan, jadi menulis semua baris dengan `1.` pun tetap aman dan hasilnya tetap urut 1, 2, 3:

```md
1. Buka NOTEZ
2. Tulis catatan
3. Masuk mode baca
```

**List bersarang (nested):** tambahkan indentasi (biasanya dua atau empat spasi) sebelum tanda list untuk membuat sub-poin:

```md
- Tugas utama
  - Sub-tugas pertama
  - Sub-tugas kedua
- Tugas lain
```

**Kesalahan umum:** lupa baris kosong sebelum list yang dimulai tepat setelah paragraf kadang membuat list tidak terbaca sebagai list, melainkan menyatu dengan paragraf di atasnya. Kalau list-mu tidak tampil rapi, coba tambahkan satu baris kosong sebelum baris list pertama.

Pilih jenis list berdasarkan cara pembaca akan memakai catatan itu. Bullet cocok untuk kumpulan ide yang tidak punya urutan, misalnya bahan masakan atau poin hasil brainstorming. Numbered list lebih cocok untuk langkah yang harus diikuti dari atas ke bawah. Kalau urutan tidak penting, jangan memaksa angka hanya karena tampilannya terlihat lebih formal.

Untuk list bersarang, pakai indentasi yang konsisten. Dua spasi sering cukup, tetapi empat spasi lebih aman ketika sub-poin berisi paragraf atau list lain. Saat ada satu item yang tampak "meloncat" ke level yang salah, periksa spasi di awal barisnya sebelum mengubah syntax lain.

---

## 10. Checklist / task list

**Fungsi:** melacak progres tugas langsung di dalam catatan, tanpa aplikasi to-do terpisah.

**Cara menulis:**

```md
- [ ] Tulis draft
- [x] Review
- [ ] Release
```

Kotak kosong `[ ]` berarti belum selesai, `[x]` (huruf x kecil) berarti sudah selesai.

**Di NOTEZ:** checkbox di mode baca hanya tampilan status, bukan tombol yang bisa ditap langsung. Untuk mencentang atau membatalkan centang, edit teks Markdown-nya di mode edit — ubah `[ ]` jadi `[x]`, atau sebaliknya.

Contoh checklist bertingkat untuk memecah tugas besar jadi sub-tugas:

```md
- [ ] Siapkan rilis v0.1.2
  - [x] Selesaikan Panduan Markdown
  - [ ] Uji di perangkat
  - [ ] Tulis release notes
```

Perhatikan jarak di dalam kotak: gunakan `[ ]`, bukan `[]` atau `[ - ]`. Untuk status selesai, bentuk yang paling aman adalah `[x]`. Kalau ingin menandai tugas yang sedang dikerjakan, tulis statusnya di teks item, misalnya `- [ ] Review draft — sedang berjalan`; NOTEZ belum punya status ketiga atau checkbox interaktif.

Checklist bekerja baik ketika satu item berisi satu tindakan yang bisa diverifikasi. `- [ ] Bereskan project` terlalu luas; memecahnya menjadi `- [ ] Tulis draft`, `- [ ] Cek preview`, dan `- [ ] Kirim hasil review` membuat catatan lebih mudah dipakai.

---

## 11. Quote

**Fungsi:** menandai kutipan dari sumber lain, atau menonjolkan satu potongan catatan sebagai sesuatu yang terpisah dari alur normal.

**Cara menulis:** tanda `>` di awal baris.

```md
> Ini kutipan atau catatan penting.
```

Quote beberapa baris tetap diawali `>` di tiap barisnya:

```md
> Baris pertama kutipan.
> Baris kedua kutipan, masih bagian yang sama.
```

Quote bisa berisi format lain di dalamnya, misalnya tebal atau link:

```md
> Menurut dokumentasi resmi, **fitur ini masih eksperimental**.
```

Quote bukan hanya untuk ucapan orang lain. Di catatan pribadi, ia juga cocok untuk menyimpan kalimat yang ingin kamu ingat, kutipan buku, atau konteks singkat dari keputusan. Namun, jangan menaruh seluruh paragraf biasa di dalam quote hanya agar tampil menjorok; gunakan paragraf normal kalau memang itu isi utama catatan.

Quote bertingkat bisa dibuat dengan menambah tanda `>`:

```md
> Pendapat pertama.
>> Balasan atau kutipan di dalam kutipan.
```

Untuk percakapan panjang, biasanya lebih mudah dibaca kalau setiap pembicara dibuat sebagai paragraf atau bullet terpisah. Quote paling enak dipakai sebagai aksen singkat, bukan sebagai pengganti struktur catatan.

---

## 12. Callout

**Fungsi:** callout adalah "quote versi lebih spesifik" — dipakai untuk menandai jenis informasi tertentu supaya pembaca langsung tahu bobotnya sebelum membaca isinya (informasi biasa, saran, hal penting, peringatan, atau risiko).

**Cara menulis:** baris pertama berisi `[!JENIS]` di dalam tanda quote, lalu isi callout di baris-baris berikutnya, masih diawali `>`.

NOTEZ mendukung lima jenis callout:

```md
> [!NOTE]
> Informasi biasa, sekadar catatan tambahan.

> [!TIP]
> Saran yang membantu, sifatnya opsional.

> [!IMPORTANT]
> Hal penting yang perlu diingat, jangan sampai terlewat.

> [!WARNING]
> Peringatan sebelum melakukan sesuatu yang berisiko.

> [!CAUTION]
> Risiko atau tindakan yang perlu ekstra hati-hati, dampaknya bisa besar.
```

**Cara memilih jenis callout yang tepat:**

- pakai `NOTE` untuk info netral yang sekadar pelengkap;
- pakai `TIP` untuk saran yang mempermudah, bukan keharusan;
- pakai `IMPORTANT` untuk hal yang harus diperhatikan tapi belum tentu berbahaya kalau terlewat;
- pakai `WARNING` untuk hal yang berpotensi menyebabkan masalah kalau diabaikan;
- pakai `CAUTION` untuk risiko paling serius, misalnya tindakan yang tidak bisa dibatalkan.

Contoh pemakaian nyata di catatan kerja:

```md
> [!WARNING]
> Jangan hapus folder backup sebelum rilis dikonfirmasi stabil.
```

---

## 13. Link dan bare URL

**Fungsi:** menghubungkan catatan ke sumber luar (dokumentasi, artikel, repo) tanpa menampilkan alamat mentah yang panjang.

**Cara menulis:**

```md
[GitHub](https://github.com)
```

- teks di dalam `[]` adalah label yang tampil ke pembaca;
- alamat di dalam `()` adalah tujuan link yang sesungguhnya.

Contoh dalam kalimat:

```md
Lihat detailnya di [dokumentasi resmi](https://docs.github.com).
```

**Bare URL** (alamat polos tanpa dibungkus `[]()`) juga otomatis jadi link aktif di preview NOTEZ:

```md
https://github.com
```

**Link internal ke heading** juga bisa dipakai untuk daftar isi manual. Contohnya, link berikut akan lompat ke heading yang anchor-nya `16-table`:

```md
[Table](#16-table)
```

**Kesalahan umum:** lupa `https://` di depan alamat kadang membuat link tidak terdeteksi sebagai URL yang valid. Selalu sertakan skema lengkap (`https://` atau `http://`).

Saat link eksternal diketuk, NOTEZ menyerahkannya ke browser atau aplikasi yang sesuai di perangkat. Link tidak dibuka di dalam Reading View. Ini sengaja: catatan tetap menjadi ruang baca lokal, dan kamu punya kontrol yang jelas kapan harus keluar ke internet.

Untuk link yang sering kamu pakai, label pendek biasanya lebih nyaman di layar ponsel daripada URL panjang. Simpan alamat lengkap di balik label yang menjelaskan tujuannya, seperti `[dokumentasi API](https://contoh.com/docs)`, bukan sekadar `[klik di sini](...)`. Kalau alamatnya memang bagian dari informasi yang perlu disalin, bare URL tetap pilihan yang masuk akal.

---

## 14. Inline code

**Fungsi:** menandai potongan teks sebagai istilah teknis di tengah kalimat — nama file, nama variabel, command singkat, atau nilai literal — supaya jelas dibedakan dari teks biasa.

**Cara menulis:** apit dengan satu backtick di kedua sisi.

```md
File `README.md` berisi dokumentasi project.
```

Contoh lain:

```md
Jalankan `gradle assembleDebug` untuk build APK.
Setting `Tema` ada di drawer bagian Settings.
Variabel `appName` menyimpan nama aplikasi.
```

**Kalau isi kode itu sendiri mengandung backtick**, apit dengan dua backtick supaya backtick di dalamnya tampil sebagai teks biasa:

```md
Gunakan `` `kode` `` untuk menandai inline code.
```

Inline code paling pas untuk sesuatu yang pembaca perlu kenali persis: nama file, command pendek, key konfigurasi, atau nilai yang harus disalin. Jangan membungkus satu paragraf penuh dengan backtick; teks panjang akan sulit dibaca dan tidak memberi informasi tambahan.

Kalau sebuah command memiliki beberapa flag atau lebih dari satu baris, pindahkan ke code block. Dengan begitu, pembaca bisa menyalin potongan itu sebagai satu unit dan spasi di dalamnya tidak membingungkan.

---

## 15. Code block

**Fungsi:** menampilkan snippet kode lebih dari satu baris dengan spasi dan baris baru yang dijaga persis seperti aslinya — cocok untuk contoh kode, log, atau output command.

**Cara menulis:** bungkus dengan tiga backtick di baris sendiri, satu buka dan satu tutup. Nama bahasa opsional bisa ditambahkan setelah backtick pembuka sebagai label.

````md
```kotlin
val appName = "NOTEZ"
println(appName)
```
````

Beberapa contoh dengan bahasa berbeda, untuk menunjukkan bagaimana label bahasa dipakai:

````md
```python
def greet(name):
    return f"Halo, {name}"
```
````

````md
```bash
gradle assembleDebug
```
````

````md
```json
{
  "nama": "NOTEZ",
  "versi": "0.1.2"
}
```
````

**Catatan teknis:** nama bahasa setelah backtick pembuka (`kotlin`, `python`, `bash`, `json`, `html`, `css`, `markdown`, dan sebagainya) sekarang dipakai NOTEZ sebagai label dan syntax highlighting lokal ringan. Kalau tidak yakin bahasanya apa, boleh dikosongkan dan blok kode tetap tampil dengan format monospace.

**Kesalahan umum:** lupa menutup tiga backtick di akhir membuat sisa catatan setelahnya ikut tampil sebagai kode. Selalu pastikan jumlah baris pembuka dan penutup backtick seimbang.

Ada dua kebiasaan yang membuat code block nyaman dibaca. Pertama, beri label bahasa hanya kalau memang tahu bahasanya; label itu membantu NOTEZ memilih warna syntax, tetapi tidak mengubah isi kode. Kedua, jangan menambahkan spasi ekstra di setiap baris hanya karena code block ditulis di dalam list atau quote. Indentasi yang tidak sengaja akan ikut masuk ke hasil preview.

Code block di NOTEZ ditujukan untuk membaca snippet, command, dan log secara offline. Syntax highlighting-nya ringan dan lokal, jadi ia membantu mata menemukan pola tetapi bukan pemeriksa error dan bukan compiler. Kalau warna tidak muncul untuk bahasa tertentu, kodenya tetap aman ditampilkan sebagai teks monospace.

---

## 15a. Code block untuk berbagai bahasa pemrograman

Label bahasa setelah backtick pembuka membantu NOTEZ memberi badge bahasa dan warna syntax lokal ringan. Berikut referensi label yang umum dipakai, masing-masing dengan contoh singkat:

````md
```kotlin
fun main() {
    println("Halo dari Kotlin")
}
```
````

````md
```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Halo dari Java");
    }
}
```
````

````md
```python
def halo():
    print("Halo dari Python")
```
````

````md
```javascript
function halo() {
  console.log("Halo dari JavaScript");
}
```
````

````md
```typescript
function halo(nama: string): string {
  return `Halo, ${nama}`;
}
```
````

````md
```swift
func halo() {
    print("Halo dari Swift")
}
```
````

````md
```c
#include <stdio.h>
int main() {
    printf("Halo dari C\n");
    return 0;
}
```
````

````md
```cpp
#include <iostream>
int main() {
    std::cout << "Halo dari C++" << std::endl;
}
```
````

````md
```csharp
Console.WriteLine("Halo dari C#");
```
````

````md
```go
package main
import "fmt"
func main() {
    fmt.Println("Halo dari Go")
}
```
````

````md
```rust
fn main() {
    println!("Halo dari Rust");
}
```
````

````md
```php
<?php
echo "Halo dari PHP";
?>
```
````

````md
```ruby
puts "Halo dari Ruby"
```
````

````md
```sql
SELECT nama FROM pengguna WHERE aktif = 1;
```
````

````md
```yaml
nama: NOTEZ
versi: 0.1.2
offline: true
```
````

````md
```xml
<config>
  <nama>NOTEZ</nama>
</config>
```
````

````md
```html
<p>Contoh potongan HTML sebagai teks referensi.</p>
```
````

````md
```css
.judul {
  font-weight: bold;
}
```
````

````md
```json
{ "nama": "NOTEZ", "versi": "0.1.2" }
```
````

````md
```dockerfile
FROM alpine:latest
CMD ["echo", "Halo dari Docker"]
```
````

````md
```text
Diagram alur sederhana:
Editor -> Run -> Terminal
```
````

Kalau bahasanya tidak ada di daftar ini atau kamu tidak yakin labelnya apa, boleh dikosongkan (langsung tiga backtick tanpa label) — kode tetap tampil rapi dalam format monospace, hanya saja tanpa hint bahasa.

---

## 15b. Tips menulis catatan yang enak dibaca di layar kecil

Karena NOTEZ dipakai di HP, layar lebih sempit dibanding desktop. Beberapa kebiasaan yang membantu:

- **Paragraf pendek.** Tiga sampai lima baris per paragraf lebih nyaman di-scroll dibanding satu blok teks panjang.
- **Heading secukupnya.** Untuk catatan pendek, satu heading saja sudah cukup; heading berlebihan di catatan singkat justru bikin berantakan.
- **Tabel ringkas.** Batasi jumlah kolom di tabel supaya tidak perlu scroll horizontal terus-menerus; kalau datanya banyak, pertimbangkan pecah jadi beberapa tabel kecil per topik.
- **Bullet list dibanding koma panjang.** Daftar dengan bullet lebih gampang dipindai dibanding satu kalimat berisi banyak koma.
- **Satu ide per baris di list.** Hindari satu item bullet yang isinya sepanjang paragraf; pecah jadi sub-bullet kalau perlu.
- **Callout untuk hal yang benar-benar penting saja.** Kalau semua baris ditandai `[!IMPORTANT]`, tidak ada lagi yang terasa "penting" secara relatif.

Satu patokan sederhana: setelah menulis satu layar penuh, coba baca ulang hanya judul, kalimat pertama tiap paragraf, dan bullet-nya. Kalau alurnya masih bisa dipahami, struktur catatanmu sudah bekerja. Kalau tidak, tambahkan heading atau pecah paragraf di tempat pembaca biasanya perlu berhenti.

Tabel lebar dan code block memang bisa membutuhkan scroll horizontal. Itu bukan selalu masalah — data yang benar lebih penting daripada memaksa semuanya masuk satu layar — tetapi letakkan kolom paling penting di sebelah kiri agar informasi utama tetap cepat ditemukan.

---

## 16. Table

**Fungsi:** menyusun data dalam baris dan kolom yang rapi, lebih mudah dibandingkan menulis paragraf berulang.

**Cara menulis:**

```md
| Nama | Status | Catatan |
| --- | --- | --- |
| Markdown | Done | Preview v4 |
| UI polish | Plan | Future |
```

Aturan strukturnya:

- baris pertama = header kolom;
- baris kedua (`---`) = pemisah wajib, menandai baris di atasnya sebagai header — tanpa baris ini, tabel tidak akan terbaca sebagai tabel;
- baris berikutnya = isi tabel, satu baris tabel per baris teks;
- tanda `|` memisahkan tiap kolom; boleh ada spasi di sekitar `|` untuk kerapian penulisan, tidak memengaruhi hasil.

**Alignment kolom** diatur lewat titik dua di baris pemisah:

```md
| Kiri | Tengah | Kanan |
| :--- | :---: | ---: |
| A | B | C |
```

- `:---` (titik dua di kiri) → rata kiri;
- `:---:` (titik dua di kedua sisi) → rata tengah;
- `---:` (titik dua di kanan) → rata kanan;
- tanpa titik dua sama sekali → default rata kiri.

**Kalau isi selnya mengandung tanda `|`**, escape dengan backslash supaya tidak dianggap pemisah kolom baru:

```md
| Contoh | Keterangan |
| --- | --- |
| A \| B | tanda pipe di dalam sel |
```

**Kesalahan umum:** jumlah kolom di baris pemisah (`---`) harus sama dengan jumlah kolom di baris header — kalau jumlahnya beda, tabel bisa tampil tidak sesuai harapan.

Untuk catatan di HP, anggap tabel sebagai ringkasan, bukan tempat menyimpan paragraf panjang. Pakai header yang pendek, taruh informasi terpenting di kiri, dan pecah tabel kalau satu baris sudah terlalu padat. Reading View akan menjaga tabel tetap bisa digeser secara horizontal, tetapi pembaca tetap harus bekerja lebih keras kalau setiap sel berisi banyak kalimat.

Kalau tabel terlihat seperti teks biasa, cek tiga hal secara berurutan: ada tidak baris pemisah di bawah header, jumlah separator-nya seimbang, dan tidak ada baris kosong yang memutus tabel. Setelah itu, lihat apakah karakter `|` di dalam isi sel sudah di-escape.

---

## 16a. Footnote

Footnote berguna untuk catatan tambahan tanpa memutus alur paragraf utama.

Cara menulis:

```md
Kalimat utama tetap enak dibaca.[^1]

[^1]: Ini catatan kaki yang muncul di bagian bawah preview.
```

Keterangan:

- `[^1]` di paragraf utama adalah penanda referensi;
- `[^1]: ...` adalah isi catatan kaki;
- label tidak harus angka, misalnya `[^sumber]`, tapi angka paling mudah dibaca;
- footnote cocok untuk sumber, catatan kecil, atau penjelasan tambahan.

Footnote sebaiknya menyimpan konteks yang membantu, bukan informasi utama yang harus dilihat semua orang. Kalau pembaca perlu mengetahui hal itu untuk memahami kalimatnya, tulis langsung di paragraf. Kamu juga bisa memakai label yang lebih deskriptif seperti `[^docs]` saat catatan punya banyak referensi; yang penting label pada pemanggil dan definisinya sama persis.

Kalau angka footnote terlihat tidak berurutan atau catatannya muncul di tempat yang tidak kamu duga, periksa apakah ada label yang sama dipakai dua kali atau definisinya tertulis tanpa format `[^label]:`.

---

## 16b. Definition list

Definition list cocok untuk glosarium kecil: istilah diikuti definisi.

Cara menulis:

```md
API
: Application Programming Interface

Offline-first
: Aplikasi tetap bisa dipakai tanpa internet.
```

Di preview, istilah tampil lebih tegas dan definisinya tampil sebagai penjelasan di bawahnya.

Format ini enak untuk glosarium project, daftar singkatan, atau catatan belajar yang berisi banyak istilah. Satu istilah bisa memiliki penjelasan lebih dari satu paragraf, tetapi tetap beri jarak antar-entri supaya batasnya jelas. Kalau isinya sudah berubah menjadi tabel perbandingan dengan banyak atribut, tabel biasa akan lebih mudah dipindai.

Pastikan tanda titik dua berada di awal baris definisi. Spasi sebelum istilah dan indentasi yang tidak konsisten adalah penyebab paling umum definition list jatuh menjadi paragraf biasa. Bila hasilnya belum sesuai, coba mulai dari contoh dua baris di atas lalu tambahkan isi sedikit demi sedikit.

---

## 17. Image

**Fungsi:** menyisipkan referensi gambar ke dalam catatan.

**Cara menulis:**

```md
![Alt text](https://example.com/image.png)
```

- teks di dalam `[]` adalah *alt text* — deskripsi singkat gambar, berguna untuk aksesibilitas dan tetap muncul kalau gambarnya gagal dimuat;
- alamat di dalam `()` adalah sumber gambarnya.

**Di NOTEZ:** gambar dari internet (remote) tidak otomatis dimuat. Preview menampilkan placeholder/link lebih dulu. Kalau kamu tap `Load & cache`, NOTEZ meminta konfirmasi, mengambil gambar sekali, menyimpannya lokal, lalu bisa menampilkannya lagi saat offline. Ini menjaga NOTEZ tetap offline-first dan mencegah catatan mengambil resource jaringan diam-diam.

Contoh penulisan yang tetap berguna meski gambarnya tidak dimuat otomatis:

```md
![Screenshot halaman login](https://contoh.com/login.png)
```

Alt text `Screenshot halaman login` tetap membantu pembaca tahu gambar itu tentang apa, walau gambarnya sendiri tidak tampil.

Ada dua hal yang perlu diingat sebelum menambahkan banyak gambar. Pertama, remote image baru mengambil jaringan setelah kamu sendiri memilih `Load & cache`; membuka catatan saja tidak melakukan download diam-diam. Kedua, cache gambar adalah cache aplikasi, bukan lampiran yang ikut masuk ke backup catatan JSON. Kalau gambar penting untuk jangka panjang, simpan sumber aslinya di tempat terpisah dan tulis keterangannya di catatan.

Untuk sekarang, path file lokal dan data URL tidak dijadikan gambar bebas di preview. Kalau placeholder muncul, lihat judul sumber dan alt text-nya: sering kali itu sudah cukup untuk menemukan gambar yang dimaksud tanpa membuat Reading View mengambil resource yang tidak kamu kenal.

---

## 18. Raw HTML

Markdown di beberapa tempat, seperti GitHub atau Obsidian, bisa menerima HTML mentah. Di NOTEZ, raw HTML **tidak bebas**, tapi ada allowlist aman supaya catatan hasil copy dari README GitHub tetap lebih kebaca tanpa membuka script/style berbahaya.

### 18.1 Tag kecil yang didukung

```md
Baris satu<br>Baris dua
H<sub>2</sub>O dan x<sup>2</sup>
Tekan <kbd>Ctrl</kbd> + <kbd>S</kbd>
<mark>bagian penting</mark>
<u>garis bawah</u> dan <s>teks dicoret</s>
<small>catatan kecil</small>
<details>
<summary>Ringkasan</summary>
Isi detail yang bisa dibuka.
</details>
<abbr title="HyperText Markup Language">HTML</abbr>
<cite>Judul Referensi</cite>
<dl><dt>Istilah</dt><dd>Definisi</dd></dl>
```

### 18.2 Subset HTML ala README GitHub

NOTEZ juga mendukung subset struktur yang sering muncul di README:

```html
<div align="center">
  <img src="https://example.com/logo.png" alt="Logo" width="112">
  <h1>Judul Project</h1>
  <p><strong>Tagline tebal.</strong></p>
  <p><em>Kalimat miring.</em></p>
</div>
<p>
  <a href="https://example.com"><img src="https://example.com/badge.svg" alt="Badge"></a>
</p>
```

Yang dilakukan NOTEZ:

- `<div align="center">`, `left`, `right`, dan `justify` didukung sebagai alignment aman;
- `<h1>` sampai `<h6>`, `<p>`, `<strong>/<b>`, dan `<em>/<i>` dirender sebagai struktur teks;
- `<table>`, `<tr>`, `<td>`, `<th>`, `<tbody>`, dan `<thead>` didukung untuk tabel README sederhana;
- `<td align="center">` / `<th align="center">` ikut dirender sebagai alignment aman;
- `<a href="...">` boleh untuk `https:`, `http:`, `mailto:`, `tel:`, dan anchor lokal `#bagian`;
- `<img>` tidak diunduh otomatis, tapi berubah menjadi placeholder image;
- atribut ukuran `<img width="112" height="112">` dipakai untuk ukuran placeholder dengan batas aman.

Contoh hasilnya: logo/badge remote tidak langsung muncul sebagai gambar asli, tapi tidak lagi bocor menjadi teks HTML mentah panjang. Ia menjadi kartu/chip placeholder yang menampilkan alt text seperti `Logo` atau `Badge`.

### 18.3 Yang tetap tidak diizinkan

```md
<script>alert("hi")</script>
<style>body { color: red }</style>
<iframe src="https://contoh.com"></iframe>
<div style="position:fixed; inset:0">overlay</div>
<div onclick="alert('nope')">klik</div>
<a href="javascript:alert(1)">bahaya</a>
```

Tag/atribut di atas tetap diblok, dihapus, atau dibuat tidak aktif. `style=`, `class=`, `id=`, dan event handler seperti `onclick=` tidak dibuka bebas. Untuk alignment sederhana, pakai `align="center"`, bukan `style="text-align:center"`.

### 18.4 Kenapa `<img>` jadi placeholder?

NOTEZ tidak auto-load remote image. Kalau catatan berisi:

```html
<img src="https://img.shields.io/badge/License-GPLv3-blue.svg" alt="GNU GPLv3">
```

NOTEZ menampilkan placeholder/chip berdasarkan `alt`, bukan mengambil gambar dari internet secara otomatis. Ini menjaga prinsip offline-first: tidak ada request jaringan hanya karena kamu membuka catatan.

Kalau placeholder menampilkan tombol `Load & cache`, kamu bisa tap tombol itu — atau tap area placeholder-nya — untuk mengambil gambar secara manual. NOTEZ akan menampilkan dialog konfirmasi berisi domain sumber. Setelah berhasil, gambar disimpan lokal dan render berikutnya memakai cache. Untuk README GitHub yang punya link repo di dalam catatan, path relatif seperti `docs/screenshots/a.png` bisa diarahkan ke `raw.githubusercontent.com` secara user-triggered. Untuk keamanan MVP, tipe utama yang didukung cache adalah PNG, JPG/JPEG, WebP, dan GIF; SVG badge remote bisa tetap menjadi placeholder kalau belum lolos policy render aman.

Kalau kamu ingin menulis contoh HTML sebagai dokumentasi, bungkus dengan inline code atau code block:

```md
Contoh tag: `<br>` dipakai untuk baris baru di HTML.
```

Atau pakai code block kalau contohnya lebih panjang:

````md
```html
<br>
<p>Contoh paragraf HTML</p>
```
````

Intinya: gunakan Markdown bawaan NOTEZ untuk struktur utama. Raw HTML didukung hanya sebagai compatibility aman untuk kasus kecil dan README-style yang umum, bukan sebagai browser/CSS bebas.

---

## 19. Escape karakter Markdown

**Fungsi:** menampilkan karakter yang biasanya punya arti khusus di Markdown (seperti `*`, `_`, `` ` ``, `#`, `|`) supaya tampil apa adanya sebagai teks, bukan diproses sebagai format.

**Cara menulis:** taruh backslash `\` tepat di depan karakter yang ingin ditampilkan literal.

```md
\*\*ini tidak jadi tebal\*\*
```

Hasilnya tampil sebagai teks biasa `**ini tidak jadi tebal**`, bukan sebagai teks tebal.

Karakter lain yang umum perlu di-escape:

```md
\# Ini bukan heading, cuma tanda pagar biasa
\- Ini bukan bullet list
1\. Ini bukan numbered list
```

Kapan perlu escape? Biasanya saat kamu membahas Markdown itu sendiri (seperti di panduan ini), menulis rumus matematika dengan tanda bintang sebagai perkalian, atau menyalin teks yang kebetulan mengandung karakter-karakter tersebut secara harfiah.

Tidak perlu meng-escape semua tanda baca secara membabi buta. Tambahkan backslash hanya di depan karakter yang memang sedang kamu tampilkan sebagai teks biasa. Terlalu banyak backslash membuat mode edit terlihat ramai dan bisa membuat pembaca bingung ketika catatan disalin ke aplikasi lain.

Kalau karakter literal berada di dalam code span atau code block, biasanya kamu tidak perlu escape lagi karena isi kode memang sudah diperlakukan sebagai teks. Contohnya, `` `**bukan bold**` `` akan tetap menampilkan bintang tanpa perlu `\\` tambahan.

---

## 20. Fitur Markdown umum yang TIDAK didukung NOTEZ

Markdown di luar NOTEZ (misalnya di GitHub atau Obsidian) punya beberapa fitur tambahan yang **belum** didukung di NOTEZ. Ini dicatat secara jujur supaya kamu tidak berharap fitur yang belum ada, dan tidak bingung kalau syntax berikut tidak berubah tampilannya:

| Fitur | Contoh syntax umum | Status di NOTEZ |
| --- | --- | --- |
| Front matter | `---` blok metadata di awal file | Tidak diproses sebagai metadata |
| Rumus matematika/LaTeX | `$x^2$` atau `$$...$$` | Tidak dirender, tampil sebagai teks |
| Diagram (mis. Mermaid) | ```` ```mermaid ```` blok | Tidak dirender sebagai diagram |
| Embed HTML aktif (iframe, video, script) | `<iframe>`, `<video>`, `<script>` | Dinonaktifkan/escaped (lihat bagian 18) |
| Emoji shortcode | `:smile:` | Tidak dikonversi otomatis jadi emoji |
| Reference-style link | `[label][ref]` + `[ref]: url` | Belum tentu dikenali, gunakan link biasa (bagian 13) |
| Table of contents otomatis | `[[TOC]]` atau serupa | Tidak ada otomatis; daftar isi manual dengan link internal bisa dibuat kalau perlu |

Kalau kamu terbiasa menulis Markdown di aplikasi lain dan salah satu fitur di atas tidak tampil seperti biasanya di NOTEZ, itu bukan bug — memang belum didukung. Gunakan alternatif yang sudah didukung: misalnya untuk rumus sederhana, tulis sebagai inline code atau pakai superscript/subscript ringan jika cukup.

Batasan ini adalah pilihan produk, bukan undangan untuk menempelkan HTML atau JavaScript agar hasilnya "dipaksa" muncul. NOTEZ membaca catatan lokal dengan renderer yang sama di setiap perangkat, sehingga konten yang bisa menjalankan script, memuat iframe, atau mengubah CSS bebas sengaja tidak diaktifkan.

Kalau kamu membawa teks dari GitHub, Obsidian, atau editor lain, lakukan pengecekan singkat setelah paste: buka mode baca, cari bagian yang masih tampil sebagai teks mentah, lalu pilih padanan paling sederhana. Sering kali heading, list, quote, table, callout, dan code block sudah cukup untuk mempertahankan maksud catatannya tanpa fitur tambahan.

---

## 21. Kesalahan umum dan cara memperbaikinya

Ringkasan cepat kesalahan yang paling sering terjadi, dikumpulkan dari penjelasan di setiap bagian sebelumnya:

| Gejala | Penyebab | Perbaikan |
| --- | --- | --- |
| `**teks**` tidak jadi tebal | Ada spasi menempel ke bintang | Hapus spasi: `**teks**` bukan `** teks **` |
| List tidak tampil sebagai list | Tidak ada baris kosong sebelum list | Tambahkan satu baris kosong sebelum baris list pertama |
| Tabel berantakan | Jumlah kolom di baris pemisah `---` tidak sama dengan header | Samakan jumlah kolom di semua baris tabel |
| Baris baru tidak muncul | Cuma menekan Enter sekali, tanpa baris kosong | Tambahkan baris kosong, atau dua spasi di akhir baris |
| Callout tidak berwarna/tidak terdeteksi | Baris pertama bukan format `[!JENIS]` yang tepat | Cek ejaan jenisnya: NOTE, TIP, IMPORTANT, WARNING, CAUTION |
| Checkbox tidak bisa ditap | Memang bukan tombol di mode baca | Edit teks Markdown-nya langsung di mode edit |
| Gambar tidak muncul | Gambar remote memang tidak auto-load | Ini perilaku normal NOTEZ, bukan error |
| Tag HTML tertentu tampil sebagai teks | Tag itu tidak masuk safe allowlist | Ini perilaku normal NOTEZ, bukan error |
| Code block "bocor" ke teks setelahnya | Lupa menutup tiga backtick | Pastikan ada backtick pembuka dan penutup yang seimbang |
| Karakter Markdown tampil sebagai format padahal maunya literal | Belum di-escape | Tambahkan backslash `\` di depan karakter tersebut |

---

## 22. Pertanyaan yang sering muncul (FAQ)

**Apa itu Markdown, sebenarnya?**
Cara menulis teks biasa dengan tanda-tanda sederhana (seperti `*`, `#`, `-`) yang kemudian diterjemahkan jadi tampilan rapi — tebal, judul, list, dan seterusnya — tanpa perlu tombol format terpisah.

**Kenapa `**teks**` bisa jadi tebal?**
Karena dua bintang di kedua sisi adalah tanda standar Markdown untuk "tebalkan bagian ini". NOTEZ membaca pasangan `**...**` lalu menampilkannya dalam huruf tebal di mode baca.

**Bagaimana cara menggabungkan `** **` dengan kata atau kalimat biasa?**
Cukup taruh pasangan `**` langsung menempel ke kata yang mau ditebalkan, di tengah kalimat sekalipun: `Jangan lupa **backup** dulu.` Bagian di luar `**...**` tetap teks biasa.

**Kenapa table butuh baris `---`?**
Baris `---` adalah penanda wajib yang memberi tahu renderer "baris di atas saya adalah header tabel". Tanpa baris ini, teks yang berisi tanda `|` tidak akan dikenali sebagai tabel sama sekali.

**Kenapa `---:` bikin kolom rata kanan?**
Titik dua di sisi kanan tanda hubung adalah kode alignment. Posisi titik dua (kiri, kanan, atau dua-duanya) menentukan arah rata kolom tersebut.

**Kenapa raw HTML di NOTEZ dibatasi?**
Untuk keamanan. NOTEZ mengizinkan subset aman seperti `<br>`, `<sub>`, `<sup>`, `<kbd>`, `<mark>`, `<details>`, serta struktur README umum seperti `<div align="center">`, `<h1>`, `<p>`, `<strong>`, `<em>`, `<a>`, dan `<img>` placeholder. HTML berbahaya seperti `<script>`, `<iframe>`, `<style>`, `onclick=`, `style=`, atau `javascript:` tetap dibuat tidak aktif/escaped.

**Kenapa remote image tidak langsung tampil?**
NOTEZ dirancang offline-first dan menghormati privasi — aplikasi tidak mengakses jaringan diam-diam hanya karena ada `![...](url)` atau `<img src="https://...">` di catatanmu. Online hanya terjadi saat kamu tap `Load & cache`, lalu hasilnya disimpan lokal.

**Bagaimana bikin catatan rapi tanpa format ribet?**
Cukup pakai heading untuk judul bagian, bullet list untuk poin-poin, dan tebal untuk kata kunci penting. Tiga elemen itu saja sudah membuat catatan panjang jauh lebih mudah dibaca ulang.

**Apakah saya wajib menghafal semua syntax ini?**
Tidak. Cheat sheet di bagian 1 cukup untuk kebutuhan sehari-hari. Bagian-bagian detail di panduan ini ada untuk dicari saat dibutuhkan, bukan untuk dihafal semua sekaligus.

**Apakah Markdown di NOTEZ sama persis dengan Markdown di GitHub?**
Mirip untuk fitur dasar (tebal, miring, heading, list, table, code block, link), tapi tidak identik. NOTEZ tidak mendukung sebagian fitur lanjutan GitHub/Obsidian — lihat daftar lengkapnya di bagian 20.

**Kenapa checklist saya tidak bisa dicentang langsung di mode baca?**
Karena mode baca di NOTEZ bersifat tampilan, bukan editor interaktif. Untuk mengubah status checklist, kembali ke mode edit dan ubah `[ ]` menjadi `[x]` (atau sebaliknya) secara langsung di teksnya.

**Bagaimana cara membuat sub-poin di dalam bullet list?**
Tambahkan indentasi (spasi) sebelum tanda `-` pada baris sub-poin, seperti dicontohkan di bagian 9.

**Apakah boleh mencampur bullet list dan numbered list dalam satu catatan?**
Boleh, keduanya bisa dipakai bersebelahan dalam catatan yang sama, bahkan bersarang satu sama lain, selama masing-masing tetap konsisten dalam kelompoknya sendiri.

**Apa bedanya inline code dan code block?**
Inline code (`` `teks` ``) untuk potongan pendek di tengah kalimat. Code block (tiga backtick) untuk kode atau teks lebih dari satu baris yang formatnya (spasi, baris baru) perlu dijaga persis.

**Kenapa kadang paragraf saya menyatu jadi satu baris panjang padahal saya sudah pindah baris?**
Karena hanya menekan Enter sekali tidak selalu dianggap baris baru oleh Markdown. Tambahkan satu baris kosong di antara dua baris supaya benar-benar terpisah jadi dua paragraf.

**Bagaimana cara menulis tanda bintang atau pagar sebagai teks biasa, bukan format?**
Pakai backslash di depannya, misalnya `\*` atau `\#` — lihat bagian 19 tentang escape.

**Apakah link ke website lain aman untuk saya tap?**
Link akan membuka aplikasi lain (browser) di perangkatmu, bukan dibuka di dalam WebView NOTEZ. NOTEZ sendiri tidak menavigasi bebas ke alamat manapun secara otomatis.

**Bisakah saya menyisipkan gambar dari galeri HP langsung ke catatan?**
Panduan ini membahas syntax Markdown untuk gambar (`![alt](url)`); untuk cara menyisipkan gambar dari galeri secara langsung, tergantung fitur lain di NOTEZ di luar cakupan Markdown ini.

**Apakah callout bisa diberi judul kustom selain NOTE/TIP/IMPORTANT/WARNING/CAUTION?**
NOTEZ mendukung lima jenis baku tersebut. Jenis di luar lima itu kemungkinan tidak dikenali sebagai callout dan hanya tampil sebagai quote biasa.

**Kenapa tabel saya terlihat terlalu lebar di layar HP?**
Tabel dengan banyak kolom memang bisa melebihi lebar layar kecil. Coba ringkas isi tiap sel, kurangi jumlah kolom, atau siapkan diri untuk scroll horizontal saat membaca tabel lebar di preview.

**Apakah saya bisa menulis emoji biasa di catatan?**
Emoji Unicode biasa (😀, ✅, 🚀) yang diketik langsung dari keyboard tetap tampil apa adanya, karena itu karakter teks biasa, bukan syntax Markdown. Yang tidak didukung adalah kode pintasan seperti `:smile:` yang otomatis dikonversi jadi emoji.

**Bagaimana urutan yang benar untuk callout: `>` dulu atau `[!JENIS]` dulu?**
Tanda `>` selalu di depan, diikuti `[!JENIS]` di baris pertama, lalu isi callout di baris-baris berikutnya juga tetap diawali `>`.

**Apakah spasi di awal baris memengaruhi tampilan?**
Untuk paragraf biasa, umumnya tidak. Tapi untuk list bersarang, indentasi/spasi di awal baris justru menentukan level sub-poinnya — lihat bagian 9.

**Kenapa saya tidak boleh mengandalkan warna teks langsung (misalnya tag `<span style="color:red">`)?**
Karena `span`, `style=`, `class=`, dan CSS bebas tidak masuk safe allowlist NOTEZ. Kalau perlu menandai teks, pakai `**tebal**` atau `<mark>highlight aman</mark>`.

**Apakah ada batas panjang untuk satu catatan?**
Panduan ini tidak membahas batas teknis penyimpanan; yang dibahas di sini murni soal syntax Markdown-nya.

**Bagaimana cara terbaik menstrukturkan catatan meeting panjang?**
Kombinasikan heading untuk tiap agenda, bullet list untuk poin diskusi, checklist untuk action item, dan callout `[!IMPORTANT]` untuk keputusan yang harus diingat. Contoh lengkapnya ada di bagian 24.

**Apakah semua fitur di panduan ini pasti tampil sama di semua tema (terang/gelap) NOTEZ?**
Tema memengaruhi warna latar dan teks, tapi struktur formatnya (tebal, list, tabel, dan sebagainya) tetap sama secara logis di kedua tema.

**Saya menyalin teks dari Word/Google Docs ke NOTEZ, kenapa formatnya berantakan?**
Word dan Google Docs pakai format kaya (rich text), bukan Markdown murni. Setelah ditempel ke NOTEZ, teks tersebut perlu ditulis ulang memakai syntax Markdown supaya formatnya sesuai harapan.

**Apakah backtick tunggal dan tiga backtick bisa dicampur dalam satu catatan?**
Bisa. Backtick tunggal untuk potongan pendek di tengah kalimat (inline code), tiga backtick untuk blok kode berdiri sendiri — keduanya independen dan boleh dipakai bersamaan.

**Kalau saya salah ketik syntax, apakah catatan saya rusak atau hilang?**
Tidak. Syntax yang salah ketik paling banter hanya membuat format tidak tampil seperti yang diharapkan (misalnya `**teks` tanpa penutup tetap tampil sebagai teks, hanya saja tidak tebal); isi catatannya sendiri tetap aman dan bisa diedit lagi.

**Apakah ada cara membuat kolom teks berdampingan tanpa tabel?**
Belum ada syntax Markdown standar untuk itu di NOTEZ; tabel adalah cara paling dekat untuk menyusun konten berdampingan.

**Bagaimana kalau saya ingin daftar isi otomatis untuk catatan panjang?**
Belum didukung otomatis (lihat bagian 20). Alternatifnya, buat daftar isi manual di awal catatan menggunakan bullet list biasa yang mencantumkan judul tiap bagian.

**Bagaimana cara menulis persentase seperti "50%" tanpa masalah?**
Angka persen biasa aman ditulis apa adanya, `50%` tidak bertabrakan dengan syntax Markdown manapun.

**Apakah saya bisa menulis tanda pagar `#` untuk nomor urut (seperti "item #3") tanpa jadi heading?**
Aman selama `#` tidak berada persis di awal baris. Heading hanya terdeteksi kalau `#` ada di posisi paling depan baris.

**Kenapa dua bullet list saya menyatu jadi satu list padahal saya kasih baris kosong di antaranya?**
Baris kosong tunggal biasanya tetap dianggap satu list yang sama (list "renggang"). Kalau memang ingin dua list terpisah secara visual, tambahkan heading atau teks pemisah, bukan sekadar baris kosong.

**Apakah boleh menaruh code block di dalam bullet list?**
Secara syntax umum Markdown mengizinkan, dengan indentasi yang konsisten; tapi ini kombinasi yang gampang salah ketik, jadi kalau hasilnya terlihat aneh, coba pisahkan code block dari list-nya.

**Bagaimana menulis judul yang mengandung karakter khusus seperti tanda tanya?**
Aman ditulis langsung, contoh: `## Kenapa ini terjadi?` — tanda tanya bukan karakter khusus Markdown.

**Apakah nomor di numbered list harus mulai dari 1?**
Tidak harus, tapi paling aman dan paling mudah dibaca kalau mulai dari 1 dan berurutan secara alami saat ditulis, walau nomor tampilannya nanti tetap otomatis berurutan.

**Kenapa tabel saya jadi berantakan saat salah satu selnya kosong?**
Sel kosong tetap harus ditandai dengan `|` di posisinya, jangan dihilangkan barisnya — kalau perlu, biarkan selnya kosong di antara dua tanda `|`.

**Apakah saya bisa membuat tautan yang membuka bagian lain di catatan yang sama (anchor link)?**
Bisa untuk heading yang punya anchor otomatis di preview. Bentuknya seperti `[Table](#16-table)`. Panduan ini memakai cara tersebut di daftar isi. Kalau link tidak lompat ke bagian yang tepat, cek lagi ejaan anchor-nya.

**Bagaimana menulis alamat email supaya jadi link?**
Bare URL yang dikonfirmasi otomatis jadi link adalah alamat web (`https://...`); untuk alamat email, cara paling aman adalah tetap menuliskannya sebagai teks biasa kecuali kamu sudah memverifikasi sendiri bahwa autolink email juga berfungsi di NOTEZ.

**Apakah saya bisa menyusun beberapa callout berurutan tanpa jarak?**
Bisa, tapi sebaiknya beri satu baris kosong di antara dua callout supaya masing-masing terbaca sebagai blok terpisah, bukan menyatu.

**Kenapa saya harus konsisten pakai `-` atau `*` untuk bullet, tidak boleh dicampur?**
Boleh secara teknis, tapi mencampur keduanya dalam satu list kadang membuat sebagian renderer memecahnya jadi dua list terpisah tanpa sengaja. Konsisten satu jenis tanda lebih aman.

**Apakah heading level 1 (`#`) boleh dipakai lebih dari sekali dalam satu catatan?**
Boleh secara teknis, tapi biasanya satu catatan cukup satu heading level 1 sebagai judul utama, supaya strukturnya tetap jelas siapa "induk" dari siapa.

**Bagaimana cara terbaik menulis catatan yang isinya campuran bahasa Indonesia dan Inggris?**
Tidak ada aturan Markdown khusus untuk ini — campur bahasa aman-aman saja, syntax formatnya tetap berlaku sama di kedua bahasa.

**Apakah ukuran huruf bisa diatur langsung dari Markdown (misalnya bikin teks lebih besar dari heading biasa)?**
Tidak ada syntax Markdown standar untuk ukuran huruf kustom; heading (`#` sampai `######`) adalah cara terdekat untuk mengatur hierarki ukuran teks.

**Kenapa saya sebaiknya tidak terlalu sering pakai tebal untuk seluruh kalimat?**
Kalau semua kalimat ditebalkan, tidak ada lagi yang terasa "menonjol" secara relatif — tebal paling efektif dipakai untuk kata kunci saja, bukan kalimat penuh.

---

## 23. Glosarium istilah

**Markdown** — bahasa markup ringan berbasis teks biasa, dipakai untuk menulis format (tebal, judul, list, dan seterusnya) tanpa editor visual.

**Syntax** — aturan penulisan tanda-tanda tertentu (seperti `**`, `#`, `-`) yang diterjemahkan jadi format saat ditampilkan.

**Render / merender** — proses mengubah teks Markdown mentah menjadi tampilan rapi yang terlihat di mode baca.

**Renderer** — komponen software yang melakukan proses render. Di NOTEZ, renderer-nya bernama `MarkdownPreviewRenderer`.

**Inline** — berada di tengah baris teks, bercampur dengan kalimat biasa (contoh: inline code, tebal, miring di tengah kalimat).

**Block** — berdiri sendiri sebagai satu unit terpisah dari paragraf lain (contoh: code block, table, quote, heading).

**Fenced code block** — istilah teknis untuk code block yang "dipagari" tiga backtick di atas dan bawahnya.

**Alt text** — teks alternatif yang menjelaskan isi gambar, dituliskan di dalam `[]` pada syntax gambar.

**Callout** — blok quote khusus dengan label jenis (`[!NOTE]`, `[!TIP]`, dan seterusnya) yang menandai bobot informasi di dalamnya.

**Escape / meng-escape** — menambahkan backslash `\` di depan karakter Markdown supaya karakter itu tampil literal, bukan diproses sebagai format.

**Task list / checklist** — daftar bullet dengan kotak centang (`[ ]` atau `[x]`) untuk melacak status selesai/belum.

**Hard break** — cara memaksa baris baru terlihat di dalam satu paragraf, biasanya dengan menambahkan dua spasi di akhir baris sebelum Enter.

**Front matter** — blok metadata (biasanya format YAML) di paling awal file, sebelum konten utama; tidak diproses sebagai metadata di NOTEZ.

**Offline-first** — prinsip desain aplikasi yang mengutamakan semua fitur inti tetap berfungsi penuh tanpa koneksi internet.

**WebView** — komponen Android untuk menampilkan konten mirip halaman web di dalam aplikasi native; NOTEZ memakainya untuk menampilkan hasil render Markdown.

---

## 24. Contoh catatan lengkap (berbagai kebutuhan)

Bagian ini mengumpulkan contoh catatan nyata untuk beberapa kebutuhan umum, menggabungkan berbagai syntax yang sudah dijelaskan di atas. Salin, sesuaikan isinya, langsung pakai.

### 24.1 Rencana proyek

````md
# Rencana v0.1.2

> [!IMPORTANT]
> Fokus ke polish kecil, jangan ubah behavior besar.

## Todo

- [x] Publish v0.1.1
- [ ] Bikin Panduan Markdown
- [ ] Review card layout setting

## Catatan

Gunakan **List mode** sebagai default.
Grid mode bisa jadi opsi tambahan di Settings nanti.
````

### 24.2 Catatan meeting

````md
# Meeting Rilis — 26 September

**Peserta:** Tim inti
**Agenda:** Review fitur Panduan Markdown

## Poin diskusi

- Placement menu: drawer top-level dipilih, bukan submenu Settings.
- Konten harus offline penuh, tanpa gambar remote.

## Keputusan

> [!IMPORTANT]
> Panduan ditulis ulang dari sumber resmi, bukan copy-paste mentah.

## Action item

- [ ] Siti — siapkan draft konten
- [ ] Budi — review teknis renderer
- [x] Ani — approve placement drawer
````

### 24.3 Jurnal harian

````md
# Jurnal — 26 September 2026

## Yang dikerjakan hari ini

- Menyelesaikan draft Panduan Markdown.
- Review ulang bagian callout dan table.

## Kendala

*Belum sempat tes di device fisik.*

## Rencana besok

1. Build debug APK.
2. UAT manual sesuai checklist RFC.
````

### 24.4 Resep masakan

````md
# Nasi Goreng Sederhana

**Porsi:** 2 orang
**Waktu:** ~15 menit

## Bahan

- 2 piring nasi putih (sebaiknya nasi dingin/kemarin)
- 2 butir telur
- 2 siung bawang putih, cincang
- Kecap manis secukupnya
- Garam dan merica secukupnya

## Langkah

1. Panaskan minyak, tumis bawang putih sampai harum.
2. Masukkan telur, orak-arik sampai matang.
3. Masukkan nasi, aduk rata.
4. Tambahkan kecap, garam, dan merica.
5. Aduk sampai warna merata, sajikan hangat.

> [!TIP]
> Nasi dingin dari kulkas menghasilkan tekstur nasi goreng yang tidak lembek.
````

### 24.5 Checklist belanja

````md
# Belanja Mingguan

## Dapur

- [ ] Beras
- [ ] Minyak goreng
- [ ] Telur
- [x] Garam

## Kebersihan

- [ ] Sabun cuci piring
- [ ] Tisu

> [!NOTE]
> Cek stok minyak dulu sebelum beli, kemarin kelihatannya masih banyak.
````

### 24.6 Laporan bug

````md
# Bug Report — Checklist tidak tersimpan

**Severity:** Medium
**Ditemukan:** 26 September 2026

## Langkah reproduksi

1. Buka catatan dengan checklist.
2. Centang salah satu item di mode edit.
3. Keluar dari catatan tanpa menunggu autosave.
4. Buka lagi catatan tersebut.

## Hasil aktual

Centang kembali ke status semula (`[ ]`).

## Hasil yang diharapkan

Status centang (`[x]`) tetap tersimpan.

> [!WARNING]
> Kemungkinan race condition antara autosave dan navigasi keluar.

## Catatan tambahan

`MarkdownPreviewRenderer` tidak terlibat di bug ini — masalah di layer penyimpanan.
````

### 24.7 Catatan buku/bacaan

````md
# Catatan Buku: *Judul Buku Contoh*

**Penulis:** Nama Penulis
**Status baca:** Sedang dibaca

## Ringkasan bab 1

Poin-poin penting:

- Konsep utama yang dibahas.
- Contoh kasus yang relevan.

## Kutipan favorit

> "Contoh kutipan singkat dari buku."

## Pendapat pribadi

Bagian yang *paling menarik* sejauh ini adalah bab tentang kebiasaan kecil.
````

### 24.8 Log latihan olahraga

````md
# Latihan — 26 September

| Gerakan | Set | Repetisi |
| --- | :---: | :---: |
| Push up | 3 | 15 |
| Squat | 3 | 20 |
| Plank | 3 | 45 detik |

**Catatan:** badan masih pegal dari sesi kemarin, intensitas diturunkan sedikit.
````

### 24.9 Itinerary perjalanan

````md
# Itinerary Bandung — 2 Hari

## Hari 1

- [ ] Check-in penginapan
- [ ] Makan siang di daerah Dago
- [ ] Sore ke Kawah Putih

## Hari 2

- [ ] Sarapan
- [ ] Belanja oleh-oleh
- [ ] Check-out & pulang

> [!TIP]
> Berangkat pagi-pagi untuk menghindari macet di jalur Dago.
````

### 24.10 Catatan belajar / kuliah

````md
# Catatan: Struktur Data — Linked List

## Definisi

Linked list adalah struktur data yang menyimpan elemen dalam node, di mana setiap node menunjuk ke node berikutnya.

## Kode contoh

```kotlin
class Node(val value: Int, var next: Node? = null)
```

## Poin penting

- Insert di awal: **O(1)**.
- Insert di akhir tanpa tail pointer: **O(n)**.
- Berbeda dengan array, ukurannya dinamis.

> [!IMPORTANT]
> Jangan lupa null check saat traversal, node terakhir selalu menunjuk ke `null`.
````

### 24.11 Changelog / catatan rilis

````md
# Changelog

## v0.1.2 (belum rilis)

### Ditambahkan
- Menu Panduan Markdown di drawer.

### Diperbaiki
- ~~Checklist tidak tersimpan setelah keluar cepat~~ — sudah diperbaiki.

## v0.1.1

### Ditambahkan
- Markdown Preview lokal.
- Callout: NOTE, TIP, IMPORTANT, WARNING, CAUTION.
````

### 24.12 README singkat untuk project pribadi

````md
# Project Kecil Saya

Deskripsi singkat tentang apa yang project ini lakukan.

## Cara pakai

1. Clone repo.
2. Jalankan `./setup.sh`.
3. Buka `index.html`.

## Struktur folder

```text
project/
├── src/
├── assets/
└── README.md
```

## Lisensi

MIT.
````

### 24.13 Catatan rasa syukur / refleksi harian

````md
# Refleksi — 26 September

## Tiga hal baik hari ini

1. Selesai menulis draft panduan.
2. Sempat istirahat cukup.
3. Ngobrol santai sama teman.

## Yang mau diperbaiki besok

*Mulai kerja lebih pagi, biar tidak buru-buru sore hari.*
````

### 24.14 Wishlist / ide masa depan

````md
# Ide untuk NOTEZ

- [ ] Search dalam panduan
- [ ] Export catatan ke PDF
- [ ] Tag/label untuk catatan

> [!NOTE]
> Ini semua masih ide mentah, belum tentu masuk roadmap resmi.
````

### 24.15 Perbandingan opsi (decision note)

````md
# Keputusan: Placement Menu Panduan Markdown

| Opsi | Kelebihan | Kekurangan |
| --- | --- | --- |
| Top-level drawer | Mudah ditemukan | Drawer sedikit lebih ramai |
| Submenu Settings | Drawer tetap minimal | Agak tersembunyi |

## Keputusan akhir

**Top-level drawer** dipilih karena panduan adalah *help/documentation*, bukan konfigurasi — jadi wajar kalau lebih mudah ditemukan.
````

---

### 24.16 Dokumentasi API sederhana

````md
# API: Ambil Daftar Package

**Endpoint:** `GET /packages`
**Auth:** tidak perlu

## Parameter

| Nama | Tipe | Wajib | Keterangan |
| --- | --- | :---: | --- |
| `status` | string | Tidak | Filter: `tested`, `experimental` |
| `limit` | number | Tidak | Default 20 |

## Contoh response

```json
{
  "total": 345,
  "items": [
    { "nama": "requests", "status": "tested" }
  ]
}
```

> [!NOTE]
> Endpoint ini contoh ilustrasi, bukan API sungguhan.
````

### 24.17 Panduan setup lingkungan development

````md
# Setup Dev Environment

## Prasyarat

- [ ] JDK 17 terpasang
- [ ] Android SDK 34 terpasang
- [ ] Gradle 8.5

## Langkah

1. Clone repo: `git clone <url-repo>`
2. Masuk folder project.
3. Jalankan `gradle assembleDebug`.

> [!WARNING]
> Pastikan `JAVA_HOME` mengarah ke JDK 17, bukan versi lain — build bisa gagal kalau salah versi.
````

### 24.18 Catatan postmortem insiden

````md
# Postmortem — Disk Internal Hilang Deteksi

**Tanggal insiden:** 11 September 2026
**Dampak:** Disk 0 (sda) sempat tidak terdeteksi Windows

## Kronologi

1. Aplikasi di-force-close karena *not responding*.
2. Disk internal hilang dari deteksi.
3. Restart perangkat, disk kembali terdeteksi.

## Akar masalah

*Belum dipastikan — dugaan sementara terkait driver storage.*

## Tindak lanjut

- [ ] Pantau apakah kejadian berulang.
- [x] Catat kondisi ini sebagai referensi.

> [!CAUTION]
> Kalau kejadian berulang, backup data penting lebih sering sebagai langkah pencegahan.
````

### 24.19 Checklist onboarding anggota tim baru

````md
# Onboarding — Anggota Baru

## Hari pertama

- [ ] Akses repo GitHub
- [ ] Install tools yang dibutuhkan
- [ ] Baca `README.md` dan `AGENTS.md`

## Minggu pertama

- [ ] Pairing dengan anggota tim lain
- [ ] Selesaikan satu task kecil

> [!TIP]
> Jangan ragu bertanya di awal — lebih baik tanya daripada salah asumsi.
````

### 24.20 Glosarium istilah pribadi untuk sebuah project

````md
# Istilah — Project ZCODE

**Package Engine** — komponen yang menangani install/uninstall package Python secara transaksional.

**Smoke test** — pengecekan cepat setelah package diaktifkan, memastikan tidak langsung error.

**Rebirth** — proses relaunch otomatis ke process Python baru setelah package native berubah.

> [!NOTE]
> Istilah ini spesifik untuk project ini, belum tentu berlaku sama di project lain.
````

---

## 25. Latihan mandiri

Coba tulis syntax Markdown untuk masing-masing soal di bawah, baru cocokkan dengan jawabannya. Kunci jawaban ada setelah semua soal.

**Soal**

1. Buat kalimat: "Rapat **dipindah** ke hari Rabu." dengan kata "dipindah" tebal.
2. Buat kalimat dengan kata "opsional" dicetak miring.
3. Buat judul level 2 bertuliskan "Catatan Tambahan".
4. Buat checklist dua item: "Beli tiket" (belum selesai) dan "Pesan hotel" (sudah selesai).
5. Buat callout jenis WARNING berisi "Jangan submit sebelum di-review."
6. Buat link dengan label "Situs resmi" menuju `https://contoh.com`.
7. Buat tabel dua kolom (`Nama`, `Umur`) dengan satu baris data: Budi, 25.
8. Buat tabel yang sama seperti soal 7, tapi kolom `Umur` rata kanan.
9. Tandai kata "API_KEY" sebagai inline code di tengah kalimat "Simpan API_KEY di file .env".
10. Buat code block berbahasa `bash` berisi perintah `npm install`.
11. Buat kalimat yang menampilkan tanda bintang literal `*` tanpa membuatnya jadi miring.
12. Buat bullet list tiga level: item utama, satu sub-item, satu sub-sub-item.
13. Buat kalimat dengan kata "final" dicoret karena sudah tidak berlaku, diikuti kata "draft" yang baru.
14. Buat quote dua baris berisi kutipan dari rekan kerja.
15. Buat garis pemisah antara dua bagian catatan.

**Kunci jawaban**

````md
1. Rapat **dipindah** ke hari Rabu.

2. Ini bersifat *opsional*.

3. ## Catatan Tambahan

4. - [ ] Beli tiket
   - [x] Pesan hotel

5. > [!WARNING]
   > Jangan submit sebelum di-review.

6. [Situs resmi](https://contoh.com)

7. | Nama | Umur |
   | --- | --- |
   | Budi | 25 |

8. | Nama | Umur |
   | --- | ---: |
   | Budi | 25 |

9. Simpan `API_KEY` di file .env.

10. ```bash
    npm install
    ```

11. Simbol \* dipakai untuk perkalian di rumus ini.

12. - Item utama
      - Sub-item
        - Sub-sub-item

13. ~~final~~ draft

14. > Katanya progresnya sudah 80 persen.
    > Tinggal testing terakhir.

15. Bagian atas.

    ---

    Bagian bawah.
````

---

## 26. Batasan Markdown di NOTEZ (ringkasan)

NOTEZ tidak menjalankan semua kemungkinan Markdown yang ada di luar sana. Ini batasan yang disengaja, bukan bug:

- **Raw HTML bebas** tidak aktif — hanya safe allowlist yang didukung; subset README-style umum tersedia, tapi tag/atribut berbahaya tetap escaped (bagian 18).
- **Gambar remote** tidak otomatis dimuat — Markdown image dan raw `<img>` menjadi placeholder/link dulu; `Load & cache` bersifat manual dan user-triggered (bagian 17–18).
- **Checkbox di preview** cuma tampilan status baca, bukan tombol yang bisa ditap (bagian 10).
- **Link eksternal** yang ditap dibuka lewat aplikasi lain (browser), bukan di dalam NOTEZ (bagian 13).
- **Fitur lanjutan berat** seperti rumus matematika/LaTeX penuh, diagram, embed aktif, dan emoji shortcode belum didukung (bagian 20).

Pilihan-pilihan ini menjaga NOTEZ tetap ringan, offline, dan aman dari konten yang tidak terduga — bukan keterbatasan teknis semata, tapi bagian dari prinsip desain aplikasinya.

---

## 27. Sumber referensi

Panduan ini ditulis ulang dengan bahasa NOTEZ sendiri, berdasarkan konsep dari dokumentasi resmi berikut — bukan hasil salin-tempel langsung — dan hanya memuat fitur yang benar-benar didukung NOTEZ:

- CommonMark Help — <https://commonmark.org/help/>
- GitHub Flavored Markdown Spec — <https://github.github.com/gfm/>
- GitHub Docs — Basic writing and formatting syntax — <https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax>
- GitHub Docs — Organizing information with tables — <https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/organizing-information-with-tables>
- Obsidian Help — Basic formatting syntax — <https://obsidian.md/help/syntax>
- Obsidian Help — Advanced formatting syntax — <https://obsidian.md/help/advanced-syntax>
- Obsidian Help — Callouts — <https://obsidian.md/help/callouts>
- Obsidian Help — HTML content — <https://obsidian.md/help/html>
