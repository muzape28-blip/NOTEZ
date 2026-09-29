# UAT — Markdown Preview v3 Safe HTML + Syntax Highlighting

**Tanggal:** 2026-09-29  
**Scope:** Markdown Preview v3 di Editor view mode.  
**Artifact:** isi setelah CI green.  
**Status:** Draft UAT — belum device verified.

---

## Cara tes cepat

1. Install APK debug dari CI green.
2. Buka NOTEZ.
3. Import JSON UAT jika perlu:

```text
docs/UAT_MARKDOWN_PREVIEW_V3_NOTEZ_BACKUP.json
```

4. Buka note `UAT — Markdown Preview v3`.
5. Pastikan mode baca / preview aktif.
6. Cek checklist di bawah.

---

## Checklist

| Area | Expected | Result |
| --- | --- | --- |
| Safe HTML | `<br>` bikin baris baru |  |
| Safe HTML | `<sub>` dan `<sup>` tampil kecil bawah/atas |  |
| Safe HTML | `<kbd>` tampil seperti tombol keyboard |  |
| Safe HTML | `<mark>` tampil highlight aman |  |
| Safe HTML | `<details>` bisa dibuka/tutup |  |
| Sanitizer | `<script>` tidak execute |  |
| Sanitizer | `<iframe>` tidak embed web |  |
| Sanitizer | raw `<img>` tidak auto-load |  |
| Sanitizer | `onclick=`, `style=`, `class=` tidak aktif sebagai raw styling bebas |  |
| Link safety | `javascript:` link tidak jalan |  |
| Code | Kotlin code block punya badge/highlight |  |
| Code | JSON code block punya badge/highlight |  |
| Regression | Table alignment tetap rapi |  |
| Regression | Callout tetap tampil sebagai card |  |
| Regression | Remote Markdown image tetap placeholder/link |  |
| Privacy | Tidak ada prompt/network load otomatis |  |

---

## Sample note source

````md
# UAT — Markdown Preview v3

## Safe raw HTML allowlist

Baris satu<br>Baris dua

Rumus kecil: H<sub>2</sub>O dan x<sup>2</sup>.

Shortcut: <kbd>Ctrl</kbd> + <kbd>S</kbd>.

<mark>Highlight aman</mark>, <u>underline</u>, <s>coret HTML aman</s>, dan <small>catatan kecil</small>.

<details open>
<summary>Detail aman</summary>
Isi detail ini harus bisa dibuka/tutup tanpa script.
</details>

<abbr title="HyperText Markup Language">HTML</abbr> dan <cite>NOTEZ Guide</cite>.

## Code highlight

```kotlin
fun helloNotez() {
    val safe = true
    println("NOTEZ Markdown Preview v3: $safe")
}
```

```json
{
  "app": "NOTEZ",
  "safeHtml": true,
  "internet": false
}
```

## Unsafe raw HTML should not run

<script>alert('NO SCRIPT')</script>
<style>body { color: red }</style>
<iframe src="https://example.com"></iframe>
<div onclick="alert('NO CLICK')" style="color:red" class="evil">raw div unsafe</div>
<img src="https://example.com/a.png">
[unsafe javascript link](javascript:alert(1))

## Regression samples

| Kiri | Tengah | Kanan |
| :--- | :---: | ---: |
| A | B | C |

> [!WARNING]
> Callout harus tetap tampil rapi.

![Remote image placeholder](https://example.com/a.png)
````
