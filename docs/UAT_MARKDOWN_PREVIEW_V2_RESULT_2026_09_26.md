# UAT Result — Markdown Preview v2 + Trash Glyph

**Tanggal:** 2026-09-26
**Status:** DEVICE UAT PASS — debug APK + signed release APK + GitHub Release published
**Tested feature commit:** `58ab61e` (`fix: resolve markdown preview theme colors`)
**Release commit:** `ef31d50ce94d355706d266e2c63a5998330e750a`
**Tester:** User/device UAT
**Build types tested:** Debug APK from CI and signed release APK from production workflow
**Release status:** GitHub Release `v0.1.1` published.
**Release URL:** https://github.com/muzape28-blip/NOTEZ/releases/tag/v0.1.1

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

User feedback debug APK:

```text
Mantaapsss semua berfungsi ... raw html tetap seperti adanya raw html dan semua fungsi fitur aman semua mantaapsss
```

User feedback signed release APK:

```text
Yooosshh semua pass
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
DESIGNED                    : YES — RFC tersedia
IMPLEMENTED                 : YES — commit 58ab61e
LOCAL STATIC VERIFIED       : YES — XML/source guards/JS parser smoke
CI DEBUG VERIFIED           : YES — CI debug green after fix 58ab61e
DEVICE DEBUG VERIFIED       : YES — user installed debug APK and reported PASS
PRODUCTION BUILD VERIFIED   : YES — workflow run 36250304432 success
SIGNED APK DEVICE VERIFIED  : YES — user installed release APK and reported PASS
GITHUB RELEASE PUBLISHED    : YES — v0.1.1 published with APK asset
```

Production workflow evidence:

```text
Workflow run        : 36250304432
Status              : success
Artifact            : notez-v0.1.1-release
Workflow artifact   : 1,982,985 bytes
Release APK asset   : NOTEZv0.1.1-release.apk
Release APK size    : 2,906,652 bytes
Release APK SHA-256 : 365dab81c540422aa17bb7b8801aa8d16d1ef40165d53d849fbff6026c88420e
Head SHA            : ef31d50ce94d355706d266e2c63a5998330e750a
Workflow URL        : https://github.com/muzape28-blip/NOTEZ/actions/runs/36250304432
Release URL         : https://github.com/muzape28-blip/NOTEZ/releases/tag/v0.1.1
```

---

## 3. Checklist result

| Area | Result | Evidence |
| --- | --- | --- |
| CI debug build | PASS | User reported CI green |
| Debug APK install | PASS | User installed debug APK |
| Signed release build | PASS | Production workflow `36250304432` success |
| Signed release APK install | PASS | User reported all pass |
| GitHub Release publish | PASS | Release `v0.1.1` published with APK asset |
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
| Release APK compact UAT | PASS | User reported all pass after installing signed release APK |

---

## 4. Known limitations / follow-up

- Raw HTML allowlist remains out of MVP by decision; raw HTML stays disabled/escaped.
- Remote images remain placeholders because NOTEZ keeps no `INTERNET` permission.
- Table text alignment follows Markdown/GFM syntax; users should use `:---`/`---` for prose columns and `---:` for numeric/right-aligned columns.
- User wants to continue a small discussion around future icon/UI polish after release.

---

## 5. Next discussion candidates

Potential future polish for `v0.1.2` or later:

```text
- Home card visual polish without changing flow.
- Optional custom trash glyph refinement if the current icon ever feels too generic.
- Possible future real Trash/Recently Deleted RFC, if user wants restore beyond snackbar undo.
- Markdown preview v2.1 safe HTML allowlist discussion, if needed later.
```
