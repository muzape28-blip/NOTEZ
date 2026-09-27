# RFC — NOTEZ Home FAB Floating Cleanup

**Tanggal:** 2026-09-27  
**Status:** IMPLEMENTED LOCALLY — static checks passed; menunggu CI/device UAT  
**Repo:** NOTEZ  
**Basis saat ditulis:** `main` @ `0285b3f` (`docs: finalize v0.1.1 release notes`)  
**Jenis perubahan:** UI/layout polish kecil di home screen  
**Prinsip utama:** hilangkan block/bottom bar kosong di bawah list, sisakan FAB `+` yang benar-benar floating, tanpa mengubah flow delete atau behavior catatan.

---

## 1. Ringkasan keputusan yang diusulkan

User melaporkan ada area bawah yang terlihat seperti block/bottom bar di sekitar tombol `+` pada home screen. Berdasarkan layout saat ini, penyebab paling mungkin adalah FAB ditempatkan sebagai child terakhir di `LinearLayout` vertical, sehingga FAB mengambil row layout sendiri di bawah RecyclerView.

Proposal:

```text
Ubah penempatan FAB dari row layout bawah menjadi overlay/floating di atas content area.
```

Target visual:

```text
List catatan mengisi layar sampai bawah.
Tidak ada block/bottom bar kosong.
FAB + tetap berada di kanan bawah sebagai tombol tambah catatan.
```

Perubahan ini **tidak** mengubah:

- delete icon/trash glyph;
- delete-with-undo behavior;
- open note behavior;
- search behavior;
- drawer/music/settings behavior;
- card layout List/Grid;
- data model/backup/import/export.

---

## 2. Problem statement

Di screenshot device user, area bawah home screen terlihat seperti ada strip/block besar di belakang atau sekitar FAB `+`. Secara UX, ini terasa seperti bottom bar palsu, padahal NOTEZ tidak punya bottom navigation.

Masalah visual:

```text
- home terasa kurang clean;
- FAB terasa seperti berada di row/bar sendiri, bukan floating;
- area list terasa berhenti sebelum bawah layar;
- tampilan tidak sesuai ekspektasi FAB Material yang mengambang di atas content.
```

Masalah ini murni visual/layout. Tidak ada indikasi bug data, delete, search, atau markdown renderer.

---

## 3. Evidence/source inspection

File relevan yang sudah diinspeksi:

```text
app/src/main/res/layout/activity_main.xml
app/src/main/java/com/zaba/notez/MainActivity.kt
```

Struktur current `activity_main.xml` secara konsep:

```text
DrawerLayout
  LinearLayout vertical main content
    Search row
    RecyclerView list, height=0dp, weight=1
    TextView empty, height=0dp, weight=1, gone
    FloatingActionButton fab, wrap_content, margin=16dp
  Drawer panel
```

Karena FAB adalah child terakhir dalam `LinearLayout` vertical, layout menyediakan ruang/row tersendiri untuk FAB. Itulah yang secara visual terbaca seperti bottom bar/block bawah.

`MainActivity.kt` memakai ID yang sudah ada:

```text
R.id.list
R.id.empty
R.id.fab
```

Implikasi:

```text
Fix bisa dilakukan terutama di XML layout, tanpa perlu mengubah logic Kotlin utama jika ID tetap sama.
```

---

## 4. Goals

- Hilangkan block/bottom bar kosong di bawah list.
- Pertahankan FAB `+` sebagai primary action tambah catatan.
- Jadikan FAB benar-benar overlay/floating di kanan bawah.
- Pertahankan ID view agar wiring `MainActivity` tetap minimal.
- Pertahankan search/list/empty state/FAB click behavior.
- Pertahankan delete trash glyph + undo flow.
- Tidak menambah bottom navigation, toolbar baru, hint, atau onboarding.
- Tidak mengubah layout card menjadi grid/list setting.

---

## 5. Non-goals

RFC ini tidak bertujuan untuk:

- mengubah delete menjadi long-press;
- menambah dialog confirm delete;
- mengubah text/button delete menjadi `[OK]`/`[Cancel]`;
- menghapus trash icon di card;
- mengubah card layout/spacing secara besar;
- menambah setting List/Grid;
- menambah bottom navigation;
- menyembunyikan FAB saat search aktif, kecuali disetujui terpisah;
- mengubah database, backup, import/export, music drawer, markdown renderer, atau release flow.

---

## 6. Proposed layout direction

Ganti struktur main content menjadi overlay layout:

```text
DrawerLayout
  FrameLayout main_frame
    LinearLayout vertical content
      Search row
      FrameLayout list_area, weight=1
        RecyclerView list
        TextView empty
    FloatingActionButton fab, gravity=bottom|end
  Drawer panel
```

Rationale:

```text
FrameLayout memungkinkan FAB mengambang di atas content.
RecyclerView/list tetap mengisi area sampai bawah.
FAB tidak lagi membuat row sendiri.
```

### 6.1 RecyclerView padding policy

Untuk menghindari miskom:

```text
Tujuan utama adalah menghapus visible bottom bar/block.
```

MVP dapat mempertahankan padding list yang sudah ada (`8dp`) tanpa menambahkan spacer besar. Jika setelah UAT card terakhir terasa terlalu tertutup FAB, padding bawah transparan/minimal bisa dipertimbangkan sebagai follow-up kecil, tetapi tidak boleh membuat bottom bar visible lagi.

Recommended starting point:

```text
- no dedicated bottom row;
- no visible bottom spacer;
- keep FAB margin 16dp;
- keep existing RecyclerView padding unless device UAT menunjukkan overlap yang mengganggu.
```

---

## 7. Acceptance criteria

### 7.1 Product acceptance

- [ ] Tidak ada block/bottom bar kosong di bawah list.
- [ ] FAB `+` tetap terlihat di kanan bawah.
- [ ] Tap FAB tetap membuat catatan baru.
- [ ] List catatan tetap bisa discroll normal.
- [ ] Empty state tetap muncul saat tidak ada catatan.
- [ ] Search open/close tetap normal.
- [ ] Delete icon/trash tetap visible dan behavior tidak berubah.
- [ ] Delete tetap memakai current delete-with-undo flow.
- [ ] Drawer/settings/music tidak berubah behavior.

### 7.2 Technical acceptance

- [ ] `activity_main.xml` parse valid.
- [ ] ID `list`, `empty`, dan `fab` tetap ada.
- [ ] `MainActivity.kt` tidak perlu perubahan logic besar.
- [ ] Tidak ada permission baru.
- [ ] Tidak ada dependency baru.
- [ ] No generated cache/dependency files committed.
- [ ] Debug CI green sebelum minta device UAT.

---

## 8. UAT plan

Manual UAT setelah implementation:

1. Install debug APK dari CI green.
2. Buka NOTEZ home.
3. Pastikan tidak ada block/bottom bar kosong di bawah list.
4. Pastikan FAB `+` tetap berada di kanan bawah.
5. Tap FAB dan pastikan catatan baru terbuka.
6. Kembali ke home.
7. Scroll list sampai bawah.
8. Tap delete/trash di beberapa card dan pastikan undo snackbar tetap muncul.
9. Buka search, ketik query, tutup search.
10. Pastikan layout tidak kembali menampilkan bottom block saat keyboard muncul/tutup.
11. Swipe drawer, buka/tutup settings/music drawer, pastikan tidak terganggu.
12. Cek empty state jika memungkinkan.

Device UAT evidence hanya boleh disebut `DEVICE VERIFIED` jika user benar-benar melaporkan pass di perangkat.

---

## 9. Risks and mitigations

### Risk 1 — FAB menutupi sebagian konten/card terakhir

Mitigasi:

- mulai dari layout overlay tanpa visible bottom bar;
- UAT scroll sampai card terakhir;
- jika overlap terasa mengganggu, tambah padding bawah transparan/minimal sebagai follow-up tanpa membuat bottom bar visible.

### Risk 2 — Empty state layout berubah

Mitigasi:

- tempatkan `TextView empty` di `list_area` yang sama dengan RecyclerView;
- visibility existing tetap dikontrol oleh `MainActivity.observe()`;
- UAT empty state.

### Risk 3 — Keyboard/search mengubah layout aneh

Mitigasi:

- UAT search open/close dengan keyboard;
- jangan ubah search focus/keyboard logic di patch ini kecuali perlu.

### Risk 4 — Scope creep ke delete flow

Mitigasi:

- no long-press delete;
- no confirm delete dialog;
- keep trash icon + undo flow exactly as current.

---

## 10. Rollback plan

Jika layout bermasalah:

- revert `activity_main.xml` ke struktur sebelumnya;
- tidak perlu migrasi data;
- tidak perlu rollback database/renderer;
- behavior app tetap aman karena patch bersifat layout-only.

---

## 11. Recommendation final

Tambahkan sebagai companion polish kecil untuk kandidat update berikutnya:

```text
v0.1.2 candidate:
- Panduan Markdown lokal/offline
- Clickable daftar isi
- Home FAB floating cleanup
```

Implementasi disarankan tetap kecil:

```text
activity_main.xml only if possible
no delete flow change
no home card redesign
no List/Grid setting yet
```

Status RFC ini: **IMPLEMENTED LOCALLY**. Static checks sudah PASS, tetapi belum CI/device verified.
