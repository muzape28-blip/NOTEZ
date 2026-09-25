# NOTEZ v0.1.0 Release Notes

**Status:** RELEASE CANDIDATE — signed production artifact already CI-built before this release-note PR.

## Highlights

- Unlimited local notes with Room persistence.
- Markdown view/edit flow in editor.
- Swipe-only drawer navigation.
- Settings moved into drawer.
- Eye icon search toggle.
- JSON/TXT export and JSON import.
- Optional automatic backup folder.
- Local music drawer player:
  - Add Music from local files via Android file picker.
  - Music controls in Main and Editor drawers.
  - Previous / play-pause / next controls.
  - Loop All / Once / Shuffle modes.
  - Media3 playback service for background/screen-off playback.
- Data-safety pass:
  - safer delete + undo ordering;
  - editor save hardened for final `onPause` persistence.

## Privacy / network

- No `INTERNET` permission.
- No online music search.
- No music downloader.
- No storage-wide audio scan in MVP.
- Music files are user-selected local files.

## Verification evidence

- Debug CI after changes: user reported green.
- Production workflow `Build NOTEZ Production — v0.1.0`: run `36109540939`, conclusion `success`.
- Signed release artifact: `notez-v0.1.0-release`, size `1,961,686` bytes.
- User UAT reported local music drawer, Add Music, Main/Editor controls, and background behavior working without found issues.

## Known limits

- Music library is app-local JSON, not synced.
- If a selected audio file is moved/deleted by the user, playback can fail gracefully.
- GitHub Release/tag publication is handled outside the APK build workflow.
