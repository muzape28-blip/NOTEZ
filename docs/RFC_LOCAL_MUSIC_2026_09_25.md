# RFC — NOTEZ Local Music Drawer

**Tanggal:** 2026-09-25  
**Status:** DESIGNED — menunggu approval user sebelum implementasi  
**Repo:** NOTEZ  
**Basis saat ditulis:** `main` @ `c0fe34a` (`fix: harden note delete and editor save`)  
**Target awal:** fitur musik lokal yang menemani user menulis catatan panjang tanpa membuka aplikasi lain.

---

## 1. Ringkasan keputusan

NOTEZ akan punya fitur **local music player** di drawer. Musik berasal dari file lokal yang dipilih user melalui file picker Android, bukan hasil scan storage otomatis dan bukan download/search online.

Keputusan desain saat ini:

1. Music tersedia di **MainActivity** dan **EditorActivity**.
2. Drawer tetap dibuka dengan gesture yang sama: tahan edge kiri sebentar lalu swipe kanan.
3. Playback tetap jalan ketika drawer ditutup, saat pindah Main ↔ Editor, saat screen off, dan saat app masuk background.
4. Engine playback memakai **Media3 ExoPlayer + MediaSessionService**.
5. Input lagu memakai **Add Music** lewat Storage Access Framework/file picker, bukan scan MP3.
6. Tidak ada online search, download musik, atau YouTube/ytmp3 flow.
7. Tidak ada fake bar/wave animation di MVP.
8. UI music berada di drawer, bukan screen penuh.
9. Music library disimpan terpisah dari database notes agar tidak menyentuh schema Room catatan pada MVP.

---

## 2. Problem statement

NOTEZ dibuat untuk menulis catatan panjang tanpa batas praktis karakter. Saat menulis lama, user ingin mendengarkan musik tanpa membuka aplikasi lain. Membuka app lain mengganggu flow menulis dan membuat NOTEZ tidak terasa sebagai ruang fokus.

Masalah yang ingin diselesaikan:

- User bisa menulis sambil mendengarkan musik lokal.
- User tidak perlu pindah ke aplikasi musik lain.
- User tidak perlu search/download online dari dalam NOTEZ.
- Musik tetap berjalan saat layar mati atau app background.
- UI tetap ringan dan tidak mengubah NOTEZ menjadi aplikasi musik besar.

---

## 3. Non-goals

Fitur ini **tidak** bertujuan untuk:

- Search musik online.
- Download musik online.
- YouTube downloader / ytmp3 converter.
- Streaming musik.
- Scan seluruh storage otomatis.
- Minta permission luas `READ_MEDIA_AUDIO` pada MVP.
- Visualizer audio real-time.
- Equalizer DSP.
- Lyrics.
- Playlist custom kompleks.
- Edit metadata file audio.
- Menghapus/memodifikasi file musik asli milik user.
- Migrasi database notes.

---

## 4. Sumber teknis dan constraints platform

### 4.1 Background/screen-off playback

Untuk playback yang tetap berjalan ketika app tidak foreground, Android merekomendasikan player dan media session dikelola melalui foreground service / `MediaSessionService`. Media3 menyediakan `MediaSessionService` untuk kasus ini.

Referensi:

- Android Media3 basic playback app / background playback:  
  https://developer.android.com/media/implement/playback-app
- Media3 overview:  
  https://developer.android.com/media/media3

### 4.2 Android 14 foreground service type

Karena target SDK NOTEZ adalah 34, service media playback harus mendeklarasikan foreground service type `mediaPlayback`. Android 14 juga mensyaratkan permission foreground-service-type yang sesuai untuk media playback.

Referensi:

- Android 14 foreground service types:  
  https://developer.android.com/about/versions/14/changes/fgs-types-required

### 4.3 Add Music via Storage Access Framework

MVP memakai file picker supaya user eksplisit memilih file audio. Dengan cara ini, NOTEZ tidak perlu scan storage dan tidak perlu permission luas untuk membaca semua audio. Akses URI yang dipilih perlu dipersist jika ingin tetap bisa diputar setelah app/device restart.

Referensi:

- Storage Access Framework / documents and files:  
  https://developer.android.com/training/data-storage/shared/documents-files

---

## 5. UX contract

### 5.1 Drawer sections

Main drawer:

```text
NOTEZ
Settings
  Tema
  Ekspor JSON
  Ekspor TXT
  Impor JSON
  Folder backup otomatis
Music
  ...music controls...
```

Editor drawer:

```text
NOTEZ
Music
  ...music controls...
```

Rationale:

- Main tetap punya Settings + Music.
- Editor fokus untuk menulis; drawer editor cukup Music agar tidak terlalu ramai.
- Jika user nanti meminta Settings juga tersedia di Editor, itu bisa jadi revisi RFC.

### 5.2 Collapsed Music row

Ketika collapsed dan belum ada track:

```text
Music
```

Ketika collapsed dan ada track aktif:

```text
Music
Judul - Artist
```

Tap `Music` expand/collapse.

### 5.3 Expanded Music empty state

Jika belum ada lagu:

```text
Music
Belum ada musik lokal
Add Music
```

### 5.4 Expanded Music dengan track aktif

```text
Music

Judul lagu
Artist atau nama file

«      ▶ / ■      »
Mode: Loop All / Once / Shuffle

Add Music

Library
- Track 1
- Track 2
- Track 3
```

Keterangan tombol:

- `«` = previous.
- `»` = next.
- `▶` = continue/play ketika sedang pause/stop.
- `■` = pause ketika sedang playing. Bentuk final boleh memakai vector pause/stop-style, bukan emoji OEM.
- Mode button berubah sesuai mode playback.

### 5.5 Playback modes

MVP memakai 3 mode:

1. **Loop All**
   - Track berjalan berurutan.
   - Setelah track terakhir, kembali ke track pertama.

2. **Once**
   - Track aktif diputar sekali.
   - Setelah selesai, playback stop/pause di akhir.

3. **Shuffle**
   - Next memilih track random dari library.
   - Previous boleh kembali ke history shuffle jika implementasinya sederhana, atau fallback ke track sebelumnya dalam urutan library pada MVP. Pilihan final ditentukan saat implementasi.

Tap mode button cycle:

```text
Loop All → Once → Shuffle → Loop All
```

### 5.6 Add Music

Tap `Add Music`:

1. Buka Android file picker.
2. Filter MIME `audio/*`.
3. User bisa memilih satu atau beberapa file jika file picker/device mendukung.
4. NOTEZ mengambil persistable read URI permission jika tersedia.
5. NOTEZ membaca metadata dasar.
6. Track masuk library.
7. Jika library sebelumnya kosong, track pertama boleh disiapkan sebagai current track, tapi tidak auto-play kecuali user menekan play.

### 5.7 No fake animation in MVP

Tidak ada fake bars/wave/cava animation di MVP. Alasan:

- Menjaga scope kecil.
- Menghindari CPU/RAM/animation jank di editor.
- Drawer tetap clean.
- Visual polish bisa ditambahkan setelah player stabil.

---

## 6. Data model

MVP tidak mengubah Room `notez.db`. Music library disimpan sebagai JSON di app files:

```text
filesDir/music_library.json
```

Draft schema:

```json
{
  "version": 1,
  "playbackMode": "LOOP_ALL",
  "currentTrackId": "track-id",
  "tracks": [
    {
      "id": "stable-id",
      "uri": "content://...",
      "displayName": "tabun.mp3",
      "title": "Tabun",
      "artist": "YOASOBI",
      "durationMs": 276000,
      "mimeType": "audio/mpeg",
      "addedAt": 1790312400000,
      "lastPlayedAt": 0
    }
  ]
}
```

### 6.1 Track ID

Track ID harus stabil dan tidak bergantung pada posisi list. Candidate:

```text
sha256(uri + displayName + addedAt)
```

Atau UUID saat import. UUID lebih sederhana untuk MVP.

### 6.2 Metadata fallback

Jika metadata title/artist tidak tersedia:

- `title` = filename tanpa ekstensi.
- `artist` = kosong atau `(unknown)`.
- `durationMs` = `0` jika gagal dibaca.

### 6.3 Invalid URI behavior

Jika file dipindah/dihapus atau URI permission hilang:

- Track tetap ada di library.
- Saat play gagal, tampilkan toast/snackbar:

```text
File musik tidak bisa dibuka
```

- Track tidak otomatis dihapus tanpa konfirmasi.

---

## 7. Architecture

### 7.1 Components

```text
MusicLibraryStore.kt
- load/save music_library.json
- add tracks
- remove track later, not required MVP
- persist playback mode/current track

MusicMetadataReader.kt
- read displayName/title/artist/duration from selected URI
- optional embedded artwork extraction deferred

MusicPlaybackService.kt
- extends MediaSessionService
- owns ExoPlayer
- owns MediaSession
- keeps playback alive in background/screen off

MusicControllerHost.kt / MusicController helper
- Activity-side connector to MediaController
- exposes play/pause/next/prev/mode state to drawer UI

MusicDrawerBinder.kt
- binds drawer views to controller and library state
- reusable from MainActivity and EditorActivity
```

### 7.2 Ownership

Player ownership:

```text
MusicPlaybackService owns ExoPlayer and MediaSession.
```

Activities do **not** own player. Activities only bind/connect to the service.

This prevents playback from stopping when:

- drawer closes;
- MainActivity opens EditorActivity;
- EditorActivity finishes;
- app goes background;
- screen turns off.

### 7.3 Service lifecycle

Draft lifecycle:

1. User presses play in drawer.
2. Activity connects to `MusicPlaybackService` via Media3 `MediaController`.
3. Service creates ExoPlayer + MediaSession if not already created.
4. Service enters foreground while playback is ongoing.
5. Media notification appears.
6. Pause/stop can move service out of active playback state.
7. Service releases player/session when no playback and no controllers, following Media3 service lifecycle.

---

## 8. Permissions and manifest

Expected permissions:

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Notes:

- `POST_NOTIFICATIONS` is runtime permission on Android 13+.
- If notification permission is denied, behavior must be tested. Media playback notification/control may be degraded; playback service correctness must be verified on target device.
- No `INTERNET` permission.
- No `READ_MEDIA_AUDIO` in MVP because there is no storage scan.
- No `READ_EXTERNAL_STORAGE` in MVP.

Service draft:

```xml
<service
    android:name=".music.MusicPlaybackService"
    android:exported="true"
    android:foregroundServiceType="mediaPlayback">
    <intent-filter>
        <action android:name="androidx.media3.session.MediaSessionService" />
    </intent-filter>
</service>
```

`exported` final value must follow Media3 requirements and security review. If external controllers are not needed, prefer the most restrictive supported setup. This must be verified against Media3 docs/source during implementation.

---

## 9. Dependency plan

Add minimal Media3 dependencies only:

```kotlin
implementation("androidx.media3:media3-exoplayer:<pinned-version>")
implementation("androidx.media3:media3-session:<pinned-version>")
```

Do **not** add `media3-ui` in MVP unless required. Drawer UI is native XML.

Version pinning rule:

- Use one explicit Media3 version.
- Do not use dynamic versions.
- Record version in implementation summary.
- Measure APK size change after CI build.

---

## 10. UI files draft

Potential layout changes:

```text
app/src/main/res/layout/activity_main.xml
- add Music row and submenu to existing drawer

app/src/main/res/layout/activity_editor.xml
- wrap content in DrawerLayout
- add drawer panel with Music row/submenu

app/src/main/res/layout/include_music_drawer.xml
- reusable Music drawer content if practical
```

Potential drawables:

```text
ic_music_prev.xml
ic_music_next.xml
ic_music_play.xml
ic_music_pause.xml
ic_music_loop_all.xml
ic_music_once.xml
ic_music_shuffle.xml
```

Use vector drawables, not emoji, for stable rendering across OEMs.

---

## 11. Implementation phases

### Phase 0 — RFC approval

This document only. No code implementation.

### Phase 1 — Music library + Add Music

- Add `MusicLibraryStore`.
- Add file picker launcher in Main and Editor or shared host.
- Persist selected audio URI permission.
- Read basic metadata.
- Render library list in drawer.
- No playback yet or only prepare state.

### Phase 2 — Media3 playback service

- Add Media3 dependencies.
- Add `MusicPlaybackService`.
- Add permissions and manifest service.
- Implement play/pause/next/prev.
- Implement mode cycle.
- Verify notification behavior.

### Phase 3 — Drawer integration Main + Editor

- Add Music drawer UI to Main.
- Wrap Editor in drawer and add same Music UI.
- Ensure playback survives Main ↔ Editor transitions.
- Ensure editor keyboard/text selection remains usable.

### Phase 4 — Hardening

- Invalid URI handling.
- Permission denied notification behavior.
- Rotation behavior.
- Screen-off/background UAT.
- APK size report.

---

## 12. Acceptance criteria

### Build/CI

- Debug build passes CI.
- Release build path still valid.
- No accidental `INTERNET` permission.
- APK size delta reported.

### Add Music

- User can tap `Add Music` from Main drawer.
- User can select at least one local audio file.
- Track appears in Music library.
- Track persists after app restart.
- If file picker is cancelled, no toast/error spam.

### Playback

- Tap track plays it.
- Play/pause button toggles icon/state correctly.
- `«` previous works.
- `»` next works.
- Mode cycles Loop All → Once → Shuffle → Loop All.
- Once mode stops after current track finishes.
- Loop All wraps from last track to first.
- Shuffle chooses a different/random track when possible.

### Main + Editor

- Music drawer opens in Main with same swipe behavior.
- Music drawer opens in Editor with same swipe behavior.
- Music keeps playing when drawer closes.
- Music keeps playing when opening Editor from Main.
- Music keeps playing when returning from Editor to Main.
- Editor text input, scroll, Markdown view/edit toggle still work.

### Background/screen-off

- Music keeps playing when screen turns off.
- Music keeps playing when app goes background.
- Media notification appears when playback is active, subject to notification permission.
- Notification controls work if provided by Media3 default notification.

### Data safety

- Notes DB is not migrated for MVP.
- Existing notes remain readable/editable.
- Music library corruption must not corrupt notes.
- Invalid music URI does not crash NOTEZ.

---

## 13. Test plan

### Static/source checks

- Assert no `android.permission.INTERNET` in manifest.
- Assert no `READ_MEDIA_AUDIO` / `READ_EXTERNAL_STORAGE` in MVP unless RFC revised.
- Assert service declares `foregroundServiceType="mediaPlayback"`.
- Assert Media3 dependencies are pinned.
- Assert music library JSON is separate from Room database.

### Local build

Preferred command once wrapper exists:

```bash
./gradlew assembleDebug --console=plain
```

Current limitation: NOTEZ repository currently has no Gradle wrapper, and the agent sandbox previously could not complete local Android build due environment limitations. CI remains canonical until wrapper/local SDK is available.

### Device UAT

Target scenarios:

```text
Add Music from Main drawer                  : PASS/FAIL
Add Music cancel                            : PASS/FAIL
Play selected track                         : PASS/FAIL
Pause/continue icon loop                    : PASS/FAIL
Prev/next                                   : PASS/FAIL
Mode cycle                                  : PASS/FAIL
Drawer close while playing                  : PASS/FAIL
Main → Editor while playing                 : PASS/FAIL
Editor → Main while playing                 : PASS/FAIL
Screen off playback                         : PASS/FAIL
Background playback                         : PASS/FAIL
Notification control                        : PASS/FAIL
Rotate portrait/landscape                   : PASS/FAIL
Editor typing with keyboard                 : PASS/FAIL
Editor text selection                       : PASS/FAIL
Invalid/deleted audio URI                   : PASS/FAIL
No INTERNET permission in APK manifest      : PASS/FAIL
```

---

## 14. Risks and mitigations

### 14.1 Scope growth

Risk: NOTEZ becomes a full music app.

Mitigation:

- MVP only local Add Music + playback controls.
- No online/download/search.
- No visualizer.
- No playlist editor.

### 14.2 Media3 dependency size

Risk: APK size grows noticeably.

Mitigation:

- Add only `media3-exoplayer` and `media3-session`.
- Avoid `media3-ui` unless required.
- Measure CI artifact size before/after.

### 14.3 Notification permission denied

Risk: Android 13+ user denies notification permission, degrading foreground media notification behavior.

Mitigation:

- Request permission only when needed for playback notification.
- Provide clear fallback/error message if platform blocks foreground playback.
- Device test denied/allowed flows.

### 14.4 Editor gesture conflict

Risk: Drawer swipe in Editor conflicts with text selection/keyboard.

Mitigation:

- Keep drawer edge behavior native.
- Test keyboard open, selection, scroll, landscape.
- If conflict is severe, revise Editor drawer access before release.

### 14.5 URI invalidation

Risk: User deletes/moves selected audio file.

Mitigation:

- Catch open/play errors.
- Mark track unavailable or show toast.
- Do not crash.

---

## 15. Rollback plan

The feature should be implemented in reversible commits:

1. Library/Add Music foundation.
2. Media3 service and manifest.
3. Main drawer UI.
4. Editor drawer UI.

Rollback options:

- Disable Music drawer UI while keeping library files harmless.
- Remove Media3 service + dependencies if playback is unstable.
- Preserve notes DB because MVP does not migrate Room schema.

---

## 16. Approval checklist before implementation

User should explicitly approve:

- [ ] Music is local-only via Add Music.
- [ ] No scan storage in MVP.
- [ ] No fake bar/wave animation in MVP.
- [ ] Media3 ExoPlayer + MediaSessionService is acceptable despite APK size increase.
- [ ] Notification/foreground-service permissions are acceptable.
- [ ] Editor gets Music drawer too.
- [ ] Main keeps Settings + Music; Editor gets Music-only drawer.
- [ ] Music library stored in JSON file, not Room DB.

---

## 17. Current status

```text
Designed                         : YES
User-visible behavior specified  : YES
Code implemented                 : NO
Local build verified             : NO
CI verified                      : NO
Device verified                  : NO
Released                         : NO
```
