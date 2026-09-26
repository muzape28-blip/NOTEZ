# UAT Result — Markdown Preview v2 + Trash Glyph

**Tanggal:** 2026-09-26
**Status:** DEVICE UAT PASS — debug APK
**Tested code commit:** `58ab61e` (`fix: resolve markdown preview theme colors`)
**Tester:** User/device UAT
**Build type:** Debug APK from green CI
**Release status:** Belum release; production APK masih perlu build/install/UAT singkat.

---

## 1. Summary

Markdown Preview v2 dan trash glyph lulus UAT di device user. Screenshot UAT menunjukkan:

- heading dan spacing rapi;
- bare URLs menjadi link biru;
- table render sebagai table nyata dan bisa horizontal scroll;
- task list render sebagai checkbox disabled;
- fenced code block punya block background/border halus;
- inline code punya subtle background;
- callouts `NOTE`, `TIP`, `IMPORTANT`, `WARNING`, `CAUTION` tampil sebagai card beraksen warna;
- raw HTML tetap tampil sebagai teks biasa dan tidak execute;
- remote image Markdown tampil sebagai placeholder/link, bukan auto-load image;
- fitur lama dilaporkan aman.

User feedback:

```text
Mantaapsss semua berfungsi ... raw html tetap seperti adanya raw html dan semua fungsi fitur aman semua mantaapsss
```

Catatan table alignment:

```text
Kolom Notes di sample UAT terlihat rata kanan karena sample memakai separator `---:`.
Itu sesuai GitHub/GFM table alignment, bukan bug renderer.
Untuk teks panjang, sample Markdown sebaiknya memakai `:---` atau `---`.
```

---

## 2. Evidence level

```text
DESIGNED                 : YES — RFC tersedia
IMPLEMENTED              : YES — commit 58ab61e
LOCAL STATIC VERIFIED    : YES — XML/source guards/JS parser smoke
CI VERIFIED              : YES — user reported CI green after fix 58ab61e
DEVICE VERIFIED          : YES — user installed debug APK and reported PASS
RELEASE VERIFIED         : NO — release APK belum dibangun/diinstall untuk v0.1.1
```

---

## 3. Checklist result

| Area | Result | Evidence |
| --- | --- | --- |
| CI debug build | PASS | User reported CI green |
| Debug APK install | PASS | User installed debug APK |
| Home trash glyph | PASS | User reported all features safe; screenshots cover UAT note view |
| Editor edit/view toggle | PASS | User reported all features safe |
| Headings | PASS | Screenshot shows H1/H2 styling |
| Bare URLs | PASS | Screenshot shows GitHub/docs URLs as blue links |
| Table rendering | PASS | Screenshot shows real table and horizontal scroll behavior |
| GFM alignment | PASS | Right-aligned `Notes` column matched `---:` sample syntax |
| Task list | PASS | Screenshot shows disabled checkboxes |
| Fenced code block | PASS | Screenshot shows code block with subtle border/background |
| Inline code | PASS | Screenshot/user specifically liked inline code styling |
| Callouts | PASS | Screenshot shows NOTE/TIP/IMPORTANT/WARNING/CAUTION cards |
| Raw HTML safety | PASS | Screenshot shows `<script>`/`<div onclick>` as text, no execution |
| Remote image safety | PASS | Screenshot shows image placeholder/link, no auto-load |
| Existing features | PASS | User reported all current features safe |

---

## 4. Known limitations / follow-up

- UAT so far is debug APK; signed release APK still needs production build and install test.
- Raw HTML allowlist remains out of MVP by decision; raw HTML stays disabled/escaped.
- Remote images remain placeholders because NOTEZ keeps no `INTERNET` permission.
- Table text alignment follows Markdown/GFM syntax; users should use `:---`/`---` for prose columns and `---:` for numeric/right-aligned columns.

---

## 5. Recommended next step

Proceed to `v0.1.1` release candidate:

1. bump version to `0.1.1` / versionCode `2`;
2. update production workflow confirmation/artifact name to `v0.1.1`;
3. create release notes for `v0.1.1`;
4. trigger signed production build;
5. install signed release APK and run compact release UAT.

Compact release UAT checklist:

```text
App opens                                  : PASS/FAIL
Open UAT note                             : PASS/FAIL
Table + horizontal scroll                 : PASS/FAIL
Bare URL opens external browser           : PASS/FAIL
Raw HTML does not execute                 : PASS/FAIL
Remote image placeholder appears          : PASS/FAIL
Trash glyph visible                       : PASS/FAIL
Music drawer still works                  : PASS/FAIL
No INTERNET permission in release APK     : PASS/FAIL
```
