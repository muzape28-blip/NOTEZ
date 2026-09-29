# UAT Result — Markdown Preview v4 / Markdown Max Tahap 1

**Tanggal:** 2026-09-29  
**Status:** DEVICE VERIFIED / PASS  
**Repo:** NOTEZ  
**Branch:** `main`  
**Implementation commit:** `1bee29f feat: expand markdown preview max`  
**Latest docs commit at time of UAT evidence:** `7146fc9 docs: record markdown preview v4 CI evidence`

---

## 1. Artifact / CI evidence

Implementation CI:

```text
GitHub Actions run : 36515946866
URL                : https://github.com/muzape28-blip/NOTEZ/actions/runs/36515946866
Conclusion         : success
Artifact           : notez-debug
Artifact id        : 11010543404
Artifact size      : 8,094,466 bytes
```

Latest docs/main CI:

```text
GitHub Actions run : 36516206526
URL                : https://github.com/muzape28-blip/NOTEZ/actions/runs/36516206526
Conclusion         : success
Artifact           : notez-debug
Artifact id        : 11011126427
Artifact size      : 8,094,464 bytes
```

---

## 2. User/device evidence

User tested the Markdown Preview v4 debug artifact on device and reported:

```text
Yuuppsss semuanya pass memuaskan
```

Screenshots supplied in chat:

```text
Screenshot_20260929-103059.png
Screenshot_20260929-103104.png
Screenshot_20260929-103106.png
Screenshot_20260929-103109.png
Screenshot_20260929-103112.png
Screenshot_20260929-103115.png
Screenshot_20260929-103118.png
Screenshot_20260929-103121.png
Screenshot_20260929-103132.png
Screenshot_20260929-103135.png
Screenshot_20260929-103139.png
Screenshot_20260929-103143.png
```

---

## 3. Observed pass items

From screenshots and user report:

| Area | Result | Evidence summary |
| --- | --- | --- |
| Highlight `==...==` | PASS | Highlighted text visible in Reading View |
| Superscript `x^2^` | PASS | `2` rendered as superscript |
| Subscript `H~2~O` | PASS | `2` rendered as subscript |
| Footnote | PASS | Reference `[1]` and footnotes section visible |
| Definition list | PASS | `API` / `Offline-first` definitions render as glossary blocks |
| Safe HTML `<br>` | PASS | Line break renders |
| Safe HTML `<details>` | PASS | Details block opens/closes |
| Code block badge/highlight | PASS | Kotlin/JSON badges and colored syntax visible |
| Table | PASS | Alignment/table polish visible, horizontal scroll works for wide UAT table |
| Callout | PASS | WARNING card renders cleanly |
| Remote Markdown image | PASS | Placeholder card shown, no auto-load |
| Unsafe raw HTML | PASS | `<script>`, `<style>`, `<iframe>`, raw `<img>` shown inert/escaped, no execution |
| Unsafe link | PASS | `javascript:` appears inert, no navigation/execute reported |
| Edit mode/autosave regression | PASS | User reported all pass |

---

## 4. Notes / follow-up polish candidates

Non-blocking observations:

- The UAT checklist table is intentionally wide and requires horizontal scroll on phone; this is acceptable for table behavior, but future UAT docs can use mobile-friendlier bullet lists.
- `<kbd>` styling is functional but subtle on the tested theme; future visual polish can make keyboard chips more distinct.

---

## 5. Final verdict

```text
Markdown Preview v4 / Markdown Max Tahap 1: DEVICE VERIFIED / PASS
```

This is not a public release claim. The feature is implemented on `main`, CI-green, and device-UAT passed; publishing a release APK remains a separate release step.
