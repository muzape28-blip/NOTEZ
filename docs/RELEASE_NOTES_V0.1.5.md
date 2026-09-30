# NOTEZ v0.1.5

**Status:** Production bugfix release candidate.
**Version:** `versionName 0.1.5`, `versionCode 6`.

---

## Fixed

- Fixed Reading View code block language labels/headers so they stay fixed while long code lines scroll horizontally.
- Moved the horizontal scrolling area to the code body, not the whole code card/header.
- Prevented long fenced code blocks from causing page-level horizontal overflow in Reading View.

## Changed

- Minor Indonesian copy polish:
  - editor counter text now shows `karakter` without the old `(tanpa batas)` suffix;
  - delete snackbar action uses `BATALKAN`;
  - empty search copy suggests trying another query or creating a new note.

## Verification evidence

- PR/debug CI build passed before merge.
- `main` CI build passed after merge at commit `cd7c0d7`.
- Device visual test passed on debug APK for fenced code blocks:
  - `python` long single-line code block;
  - `md` long single-line code block;
  - `kotlin` long single-line code block;
  - `txt` long single-line code block;
  - fenced code block without language.
- Device regression smoke test passed for Home empty state/search-eye behavior:
  - search/eye hidden at zero notes;
  - search/eye visible after creating a note.

## Scope

No Space Workspace, Card Grid, storage redesign, Settings redesign, or release workflow trigger changes are included in v0.1.5. This release is intentionally a small stable bugfix/polish release before v0.1.6 planning.

## Production release requirements

Before publishing the final APK, verify the signed release artifact reports:

```text
versionCode='6'
versionName='0.1.5'
```

Also verify signature and publish the SHA-256 checksum next to the APK.
