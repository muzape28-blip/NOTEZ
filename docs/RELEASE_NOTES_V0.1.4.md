# NOTEZ v0.1.4

**Status:** Production hotfix release.
**Version:** `versionName 0.1.4`, `versionCode 5`.

---

## Hotfix

- Fixed `Pengaturan → Versi` showing `unknown`.
- The displayed version now has a reliable packaged fallback from the Gradle release version.
- Aligned `gradle.properties` with the release version so production builds no longer inherit stale version properties.

## Scope

No feature/UI redesign in this hotfix. v0.1.3 remains the main feature batch; v0.1.4 only fixes the version label and release metadata.

## Verification target

- Production workflow only: signed release APK.
- No debug APK is published for this hotfix release.
