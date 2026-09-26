# UAT Result — Markdown Preview v2 + Trash Glyph

**Tanggal:** 2026-09-26
**Status:** DEVICE UAT PASS — debug APK + signed release APK
**Tested feature commit:** `58ab61e` (`fix: resolve markdown preview theme colors`)
**Release prep commit:** `9fdb1e5` (`docs: prepare v0.1.1 markdown preview release`)
**Tester:** User/device UAT
**Build types tested:** Debug APK from CI and signed release APK from production workflow
**Release status:** Signed release APK lulus UAT; GitHub Release `v0.1.1` belum dipublish karena user ingin diskusi polish icon/UI kecil dulu.

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
PRODUCTION BUILD VERIFIED   : YES — workflow run 36246838081 success
SIGNED APK DEVICE VERIFIED  : YES — user installed release APK and reported PASS
GITHUB RELEASE PUBLISHED    : NO — intentionally held for icon/UI polish discussion
```

Production workflow evidence:

```text
Workflow run : 36246838081
Status       : success
Artifact     : notez-v0.1.1-release
Artifact size: 1,983,005 bytes
Head SHA     : 9fdb1e5941a20d6bc2241368aecbc4222a78619f
URL          : https://github.com/muzape28-blip/NOTEZ/actions/runs/36246838081
```

---

## 3. Checklist result

| Area | Result | Evidence |
| --- | --- | --- |
| CI debug build | PASS | User reported CI green |
| Debug APK install | PASS | User installed debug APK |
| Signed release build | PASS | Production workflow `36246838081` success |
| Signed release APK install | PASS | User reported all pass |
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

- GitHub Release `v0.1.1` is not published yet by deliberate user choice.
- Raw HTML allowlist remains out of MVP by decision; raw HTML stays disabled/escaped.
- Remote images remain placeholders because NOTEZ keeps no `INTERNET` permission.
- Table text alignment follows Markdown/GFM syntax; users should use `:---`/`---` for prose columns and `---:` for numeric/right-aligned columns.
- User wants a small discussion before publish, mostly around icon/UI polish ideas.

---

## 5. Recommended next step

Before publishing `v0.1.1`, decide whether to keep the current trash glyph/UI as-is or make one small polish pass.

If no further visual change is requested:

```text
1. Tag commit 9fdb1e5 as v0.1.1.
2. Create GitHub Release v0.1.1.
3. Upload signed APK from artifact notez-v0.1.1-release.
4. Use docs/RELEASE_NOTES_V0.1.1.md as release body.
```

If visual polish is requested:

```text
1. Make the smallest icon/UI patch.
2. Run debug CI.
3. Trigger production build again.
4. Install signed release APK again for compact UAT.
5. Publish after PASS.
```
