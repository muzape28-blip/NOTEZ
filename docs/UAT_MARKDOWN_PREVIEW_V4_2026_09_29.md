# UAT — Markdown Preview v4 / Markdown Max Tahap 1

**Tanggal:** 2026-09-29  
**Scope:** final Markdown batch sebelum device UAT satu kali.  
**Status:** Draft UAT — belum CI/device verified.

---

## Cara tes

1. Install APK debug dari CI green.
2. Import JSON UAT:

```text
docs/UAT_MARKDOWN_PREVIEW_V4_NOTEZ_BACKUP.json
```

3. Buka note `UAT — Markdown Preview v4`.
4. Pastikan mode baca/preview aktif.
5. Jalankan checklist di bawah.

---

## Checklist

| Area | Expected | Result |
| --- | --- | --- |
| Highlight | `==penting==` tampil disorot |  |
| Superscript | `x^2^` tampil kecil atas |  |
| Subscript | `H~2~O` tampil kecil bawah |  |
| Safe HTML | `<kbd>`, `<mark>`, `<details>` tampil aman |  |
| Footnote | `[^1]` tampil sebagai ref dan catatan bawah |  |
| Definition list | Istilah + definisi tampil rapi |  |
| Code | Kotlin/JSON code block punya badge/highlight |  |
| Table | Alignment kiri/tengah/kanan tetap jalan |  |
| Callout | WARNING/NOTE tetap jadi card |  |
| Image | Markdown remote image tetap placeholder |  |
| Unsafe HTML | `<script>` tidak execute |  |
| Unsafe HTML | `<iframe>` tidak embed |  |
| Unsafe HTML | raw `<img>` tidak auto-load |  |
| Unsafe attrs | `onclick=`, `style=`, `class=` tidak aktif |  |
| Unsafe link | `javascript:` tidak jalan |  |
| Regression | Edit mode/autosave tetap normal |  |

---

## Sample note source

````md
# UAT — Markdown Preview v4

## Markdown Max syntax

Ini ==bagian penting== yang harus tersorot.

Rumus ringan: x^2^ + y^2^ dan H~2~O.

Kalimat ini punya footnote.[^1]

[^1]: Ini isi footnote. Harus muncul di bagian bawah preview.

API
: Application Programming Interface

Offline-first
: Aplikasi tetap nyaman dipakai tanpa internet.

## Safe raw HTML

Baris satu<br>Baris dua.

Tekan <kbd>Ctrl</kbd> + <kbd>S</kbd>.

<details open>
<summary>Detail aman</summary>
Isi detail bisa dibuka/tutup tanpa script.
</details>

<abbr title="HyperText Markup Language">HTML</abbr> dan <cite>NOTEZ Guide</cite>.

## Code highlight

```kotlin
fun helloNotez() {
    val safe = true
    println("NOTEZ Markdown Preview v4: $safe")
}
```

```json
{
  "app": "NOTEZ",
  "markdown": "v4",
  "internet": false
}
```

## Regression samples

| Kiri | Tengah | Kanan |
| :--- | :---: | ---: |
| A | B | C |

> [!WARNING]
> Callout harus tetap tampil rapi.

![Remote image placeholder](https://example.com/a.png)

## Unsafe raw HTML should not run

<script>alert('NO SCRIPT')</script>
<style>body { color: red }</style>
<iframe src="https://example.com"></iframe>
<div onclick="alert('NO CLICK')" style="color:red" class="evil">raw div unsafe</div>
<img src="https://example.com/a.png">
[unsafe javascript link](javascript:alert(1))
````
