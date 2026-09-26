# UAT — Markdown Preview v2 + Trash Glyph

**Tanggal:** 2026-09-26
**Status:** UAT TOOLING — gunakan setelah APK/CI build tersedia
**Scope:** Markdown Preview v2 di Editor view mode dan trash glyph di home note card.

---

## 1. Cara pakai cepat

Ada dua opsi:

1. **Import JSON test note** via NOTEZ:
   - buka drawer Main;
   - Settings → Impor JSON;
   - pilih file `docs/UAT_MARKDOWN_PREVIEW_V2_NOTEZ_BACKUP.json` dari checkout/download repo;
   - buka note berjudul `UAT — Markdown Preview v2`.

2. **Copy-paste manual**:
   - buat note baru di NOTEZ;
   - copy isi dari bagian **2. Sample Markdown** di bawah;
   - simpan/masuk view mode.

---

## 2. Sample Markdown

Copy isi di dalam block ini ke body note:

````markdown
# UAT — Markdown Preview v2

Tujuan: memastikan NOTEZ bisa membaca Markdown gaya GitHub/Obsidian tanpa internet.

Visit https://github.com/muzape28-blip/NOTEZ dan https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/organizing-information-with-tables

## Table: skills

| Skill | Level | Notes |
| :--- | :---: | ---: |
| Kotlin | Good | Android app |
| Markdown | Good | README/SKILLS |
| Long column test | Needs horizontal scroll | This cell intentionally contains a long sentence so we can see whether table layout stays readable on a narrow phone screen. |

## Tasks

- [x] Replace X/delete icon with trash glyph
- [x] Render table as a real table
- [x] Linkify bare URLs
- [ ] Device UAT on actual phone

## Code block

```kotlin
fun helloNotez() {
    println("NOTEZ Markdown Preview v2")
}
```

Inline code should look like `val renderer = "markdown-it"` inside a sentence.

> [!NOTE]
> This is a NOTE callout. It should look like a calm info card.

> [!TIP]
> This is a TIP callout. It should use a green-ish accent.

> [!IMPORTANT]
> This is IMPORTANT. It should stand out without being too noisy.

> [!WARNING]
> Jangan hapus catatan tanpa backup.

> [!CAUTION]
> Dangerous actions should look more serious.

## Raw HTML should not execute

<script>alert('NOTEZ should not run this')</script>

<div onclick="alert('nope')">This raw HTML should be escaped/disabled, not interactive HTML.</div>

## Remote image should not auto-load

![Example remote logo](https://example.com/logo.png)

## Done

If this page is readable, table scrolls horizontally, and links open outside NOTEZ, the MVP preview is on the right track.
````

---

## 3. Expected result checklist

| Area | Expected result | PASS/FAIL | Notes |
| --- | --- | --- | --- |
| Home card | Delete action shows custom trash glyph, not X/built-in icon |  |  |
| Home card | Tap trash still deletes with existing undo behavior |  |  |
| Editor edit mode | Native EditText editing still works |  |  |
| Toggle | Edit → view preserves latest content |  |  |
| Heading | `# UAT — Markdown Preview v2` renders as heading |  |  |
| Bare URL | Bare GitHub/docs URLs become clickable links |  |  |
| External link | Tapping URL opens browser/external handler, not internal WebView navigation |  |  |
| Table | Table renders as a real table with borders/padding |  |  |
| Wide table | Long table/cell remains readable and scrollable horizontally |  |  |
| Task list | `[x]` and `[ ]` render as disabled checkboxes |  |  |
| Code block | Fenced Kotlin block is monospace with block background |  |  |
| Inline code | Inline code has subtle code styling |  |  |
| Callout NOTE | `> [!NOTE]` renders as callout card |  |  |
| Callout TIP | `> [!TIP]` renders as callout card |  |  |
| Callout IMPORTANT | `> [!IMPORTANT]` renders as callout card |  |  |
| Callout WARNING | `> [!WARNING]` renders as callout card |  |  |
| Callout CAUTION | `> [!CAUTION]` renders as callout card |  |  |
| Raw HTML | `<script>` / `<div onclick>` do not execute |  |  |
| Raw HTML | Raw HTML is escaped/disabled according to RFC |  |  |
| Remote image | `![...](https://...)` shows placeholder/link, not auto-loaded image |  |  |
| Offline/privacy | APK still has no `android.permission.INTERNET` |  |  |
| Drawer/music | Editor drawer/music still works after preview change |  |  |
| Orientation | Portrait and landscape are usable |  |  |

---

## 4. Failure notes template

```text
Device:
Android version:
Theme:
APK/build:
Scenario:
Expected:
Actual:
Screenshot/video:
Can reproduce after restart? yes/no
```
