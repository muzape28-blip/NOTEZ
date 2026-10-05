# NOTEZ v0.1.6

**Status:** Production release, published.
**Version:** `versionName 0.1.6`, `versionCode 7`.
**Published:** 2026-10-04T08:01:39Z.
**Tag:** `v0.1.6` → commit `c68c7f1`.
**Signed APK built from:** commit `8a4a8f3`.

---

## Added

- **Diagnostics page** — new in-app performance diagnostics screen, reachable from the
  navigation drawer, backed by the new `diagnostics/` package (`PerfTracker`,
  `PerfSession`, `PerfStore`, `PerfEvent`) and `activity_diagnostics.xml`.
- **Aurora backdrop** — `AuroraBackdropView` adds the glass/aurora background treatment
  behind main surfaces.
- **Shared UI style helpers** — `UiStyle` centralizes accent color and typography
  application so screens stop hardcoding values.
- **Sectioned Markdown Guide** — `MarkdownGuideParser` + `MarkdownGuideSection` split
  `assets/help/markdown_guide.md` into sections with a table of contents
  (`item_guide_toc.xml`), replacing the single long scroll.
- **FILE menu save actions** — `Simpan file` and `Simpan sebagai...` in the editor, with
  the chosen target URI remembered per note (`NoteFileTargetStore`).

## Changed

- Editor scrolling reworked for smoother long-note scrolling (`activity_editor.xml`,
  `EditorActivity.kt`).
- Settings page restructured into expandable sections (`activity_settings.xml`,
  `SettingsActivity.setupExpandableSections()`).
- Music drawer layout and controller polish (`include_music_drawer.xml`,
  `MusicDrawerController.kt`).
- Reading View code block header stays fixed while the code body scrolls horizontally
  (`MarkdownPreviewRenderer.kt`).
- README rewritten for structure and detail.
- Production release workflow re-pointed to v0.1.6 (`.github/workflows/release.yml`).

## Not included

- `Buka file` (Open File) in the FILE menu — tracked as issue #19 (P0). The FILE menu in
  this release only offers `Simpan file` and `Simpan sebagai...`.
- Music duplicate title, metadata-vs-filename, and panel hierarchy work — issues #20,
  #21, #24.
- Space Workspace foundation from `docs/RFC-NOTEZv0.1.6-.md` §40 — not implemented; no
  `space/` package exists in this release.

## Verification evidence

- Main CI: PASS — run `37185315839` at `8a4a8f3` (2026-10-04T07:17:05Z).
- Production signed build: PASS — run `37186021371` at `8a4a8f3`
  (2026-10-04T07:31:01Z, 2m35s).
- APK badging (`aapt dump badging`):

```text
package: name='com.zaba.notez' versionCode='7' versionName='0.1.6' platformBuildVersionName='14' platformBuildVersionCode='34' compileSdkVersion='34' compileSdkVersionCodename='14'
```

- Signature (`apksigner verify --print-certs`):

```text
V2 Signer: certificate DN: CN=ZABA, OU=NOTEZ, O=ZABA
V2 Signer: certificate SHA-256 digest: 30abfea450583df17d70b6892d0513066804c05430cfd2d544b5cb9c5f961384
V2 Signer: certificate SHA-1 digest: 03db4d320316015d41e5e1ede7e473276fec4009
```

- Artifact: `NOTEZv0.1.6-release.apk`, 3,254,575 bytes.
- APK SHA-256:

```text
07a47128d05d93c7c61ea9a529ab7b88ff828470f7b0047e254f5d340844d488
```

## Release hygiene gaps (recorded, not blocking)

- Tag `v0.1.6` points to `c68c7f1`, while the signed APK was built from `8a4a8f3`.
  The only difference is the README commit `Revise README for clarity and humor
  enhancement`, so the binary is unaffected — but tag and artifact source are not the
  same commit.
- Asset name `NOTEZv0.1.6-release.apk` deviates from the v0.1.5 convention
  (`NOTEZ-v0.1.5-release.apk`).
- The GitHub release body did not carry changelog, badging, or checksum at publish time.

## Pending verification

- **Signing certificate continuity with v0.1.5** — not yet compared. Run
  `apksigner verify --print-certs` on the v0.1.5 APK and confirm the certificate SHA-256
  digest matches `30abfea4...`. A mismatch would break in-place upgrades.
- **Device regression smoke test on the release APK** — not recorded. Release builds use
  `isMinifyEnabled = true` and `isShrinkResources = true`, so R8-only regressions will not
  show up in debug builds.
