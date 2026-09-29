# UAT — Home Empty State + Tentang NOTEZ v1

**Tanggal:** 2026-09-29  
**Status:** DEVICE VERIFIED / PASS
**Scope:** empty state home saat NOTEZ kosong dan halaman Tentang NOTEZ lokal/offline.

---

## 1. Persiapan

Gunakan APK debug dari CI green. Device PASS dicatat dari konfirmasi user pada 2026-09-29.

Karena fitur ini muncul hanya saat daftar catatan kosong, UAT paling aman dilakukan dengan salah satu cara:

1. install fresh app / clear data; atau
2. backup catatan dulu, hapus semua catatan sementara, lalu restore setelah test.

Device UAT sudah dikonfirmasi PASS oleh user.

---

## 2. Checklist

| Area | Expected | Result |
| --- | --- | --- |
| Empty home | Saat tidak ada catatan dan search tidak aktif, muncul welcome NOTEZ |  |
| Icon | Icon kuda NOTEZ terlihat di atas |  |
| Copy | Teks `Catat ide, rencana, dan lainnya sebelum ditelan waktu.` tampil rapi |  |
| Hint | Teks `Ketuk + untuk mulai.` tampil |  |
| Link | `Panduan NOTEZ` bisa ditap |  |
| About page | Tap `Panduan NOTEZ` membuka halaman `Tentang NOTEZ` |  |
| About content | Halaman menjelaskan NOTEZ, mulai cepat, Markdown, backup, privacy, batasan |  |
| Back | Tombol back kembali ke home |  |
| FAB | Tap `+` tetap membuat catatan baru |  |
| Hide empty | Setelah ada catatan, welcome tidak muncul |  |
| Search empty | Search no-result menampilkan `Tidak ada hasil`, bukan welcome NOTEZ |  |
| Settings | Settings → `Tentang NOTEZ` membuka halaman yang sama |  |
| Theme | Empty/About tetap readable pada tema aktif |  |
| Regression | Drawer, Panduan Markdown, Settings, Music drawer tidak berubah |  |

---

## 3. Copy yang harus terlihat di empty state

```text
NOTEZ

Catat ide, rencana, dan lainnya
sebelum ditelan waktu.

Ketuk + untuk mulai.
Panduan NOTEZ
```

---

## 4. Security/privacy checks expected

Historical feature build did not add INTERNET. Current integrated build intentionally includes `android.permission.INTERNET` only for user-triggered image cache; Home Empty State and Tentang NOTEZ still do not auto-load remote content.

```text
No CDN
No remote scripts/styles/fonts/iframes
No native WebView bridge
About content local asset only
```

---

## 5. Verdict

```text
Home Empty State + Tentang NOTEZ v1: PASS
```

Reported by user on 2026-09-29: "Semuanya sudah pass koq".
