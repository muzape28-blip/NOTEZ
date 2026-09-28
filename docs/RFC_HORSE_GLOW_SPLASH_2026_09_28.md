# RFC — NOTEZ Horse Glow Splash + Launcher Icon Direction

| Field | Value |
| --- | --- |
| Tanggal | 2026-09-28 |
| Status | IMPLEMENTED LOCALLY — static checks passed; CI/device UAT pending |
| Repo | NOTEZ |
| Basis saat ditulis | `main` @ `656c79a` (`docs: add theme system v2 appearance RFC`) |
| Jenis perubahan | Branding/UI startup polish proposal |
| Prinsip utama | Launcher icon tetap static dan clean; splash boleh punya animasi glow singkat yang premium, ringan, offline, tanpa dependency baru, dan tidak memperlambat NOTEZ. |

---

## 1. Ringkasan keputusan yang diusulkan

User menyukai arah visual horse crest untuk NOTEZ, dengan catatan splash tidak perlu animasi kuda bergerak penuh. Arah yang disetujui secara konsep:

```text
Launcher icon:
static horse crest simplified.

Splash:
gambar horse crest yang sama,
soft glow muncul di sisi-sisi kuda,
aksen hijau NOTEZ muncul seperti cahaya/coretan pendek dari bawah.
```

Keputusan desain utama:

- kuda tidak perlu benar-benar dianimasikan rearing frame-by-frame;
- yang hidup adalah cahaya/glow, bukan gerakan tubuh kompleks;
- glow harus subtle/premium, bukan ledakan aura ramai;
- aksen hijau tetap identitas NOTEZ;
- durasi splash pendek, target sekitar `800–1100ms`;
- tidak ada sound;
- tidak ada network;
- tidak ada Lottie/dependency baru untuk MVP.

Nama konsep kerja:

```text
NOTEZ Horse Glow Splash
```

---

## 2. Current source/repo inspection

File yang dicek untuk konteks current implementation:

```text
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/res/drawable/ic_notez_fg.xml
app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
app/src/main/res/values/colors.xml
```

Current facts:

- `minSdk = 26`, `targetSdk = 34`.
- App saat ini langsung launch ke `MainActivity` sebagai launcher activity.
- Manifest memakai:

  ```xml
  android:icon="@mipmap/ic_launcher"
  android:theme="@style/Theme.Notez.GithubDark"
  ```

- Current launcher icon adalah adaptive icon:
  - background: `@color/ic_launcher_background` = `#0A0F0A`;
  - foreground: `@drawable/ic_notez_fg`;
  - foreground vector: stylized `N` putih + aksen hijau neon.
- Tidak ada `SplashActivity` khusus saat ini.
- Tidak ada `android.permission.INTERNET`, dan RFC ini tidak boleh menambahkannya.
- Concept image yang dipilih user tersimpan di workspace:

  ```text
  design/notez_horse_launcher_icon_concept.png
  ```

Catatan evidence:

```text
File concept image adalah mockup/desain, bukan asset Android final.
Belum dipasang ke APK.
Belum diuji di launcher/adaptive crop.
```

---

## 3. Design intent

Horse crest memberi NOTEZ identitas yang lebih kuat:

```text
classic stationery
heritage writing/sketchbook
premium dark note app
sedikit fantasy crest
tetap modern Markdown/offline app
```

Aksen hijau tetap membawa DNA NOTEZ lama:

```text
OLED dark
neon green cursor/coretan
ide yang menyala
quick note capture
```

Splash glow harus terasa seperti:

```text
kuda crest mulai hidup
tepi kuda menyala lembut
coretan hijau muncul sebagai tanda NOTEZ siap dipakai
```

Bukan seperti:

```text
anime aura berlebihan
api/smoke ramai
video intro panjang
brand stationery tertentu
```

---

## 4. Trademark / brand safety

Arah ini boleh mengambil inspirasi umum dari rasa `classic stationery` dan `horse crest`, tapi tidak boleh meniru brand tertentu.

Non-negotiable:

- jangan pakai nama `Faber-Castell` atau variasinya;
- jangan pakai logo/tata letak khas brand lain;
- jangan pakai castle/knight/crest yang terlalu mirip brand lain;
- jangan copy packaging color/layout brand tertentu;
- horse crest NOTEZ harus original dan sederhana.

Arah aman:

```text
original horse silhouette
original NOTEZ green accent
original dark note-app background
no external brand marks
```

---

## 5. Goals

### 5.1 Launcher icon goals

- Tetap static karena Android launcher icon tidak menjadi animasi home-screen biasa.
- Lebih readable di ukuran kecil dibanding full emblem dengan teks panjang.
- Memakai horse crest simplified yang jelas dalam 48dp.
- Tidak memakai full word `NOTEZ` di launcher icon karena nama app sudah tampil di bawah icon.
- Tetap adaptive-icon safe zone.
- Future candidate: monochrome/themed icon untuk Android 13+.

### 5.2 Splash goals

- Memakai visual horse crest yang sama agar branding konsisten.
- Animasi glow sisi-sisi kuda terasa premium dan singkat.
- Aksen hijau muncul seperti cahaya/coretan dari bawah kuda.
- Target durasi total sekitar `800–1100ms`.
- Tidak menahan user terlalu lama.
- Jika animasi disable/gagal, langsung masuk Home.
- Tidak mengubah data, notes, Markdown renderer, drawer, music, backup, atau theme preference.

---

## 6. Non-goals

RFC ini tidak bertujuan untuk:

- membuat animated launcher icon di home screen;
- membuat video splash panjang;
- menambah sound effect;
- menambah Lottie dependency untuk MVP;
- menambah network permission;
- memuat asset dari internet;
- memakai CDN/script/font/image remote;
- mengubah note data model;
- mengubah editor/view mode;
- mengubah Markdown renderer security contract;
- mengubah delete flow;
- mengubah Theme System v2 scope;
- membuat onboarding/tutorial baru;
- menambah branding copy panjang di startup.

---

## 7. Recommended visual spec

### 7.1 Base palette

```text
Background dark: #0A0F0A or #0D1117
Horse ivory:     #F3EBD2 / #FFF5D8
Shadow graphite: #30363D
Glow warm:       #F3EBD2 at low alpha
NOTEZ green:     #39FF14
Green glow:      #39FF14 at low alpha
```

### 7.2 Layering concept

Splash can be composed as layers:

```text
[dark background]
[soft warm glow behind horse]
[horse crest image]
[green accent light/coretan]
[optional tiny fade shadow]
```

### 7.3 Animation timing proposal

```text
0ms
  dark background visible

80ms - 250ms
  horse alpha 0 → 1
  horse scale 0.96 → 1.00

220ms - 650ms
  warm edge glow alpha 0 → 0.85 → 0.45

420ms - 850ms
  green accent alpha 0 → 1
  accent scaleX/clip 0 → 1
  subtle green glow pulse

900ms - 1100ms
  splash overlay fade out
  Home remains interactive
```

### 7.4 Motion rules

- No big rotation/rearing body movement for MVP.
- Horse may have subtle scale/fade only.
- Glow is the main animation.
- Green accent should feel like light appears, not messy smoke.
- Respect Android animator duration preference if technically feasible.

---

## 8. Technical direction options

### Option A — MainActivity splash overlay recommended for MVP

Implement an overlay in `MainActivity` layout/code:

```text
MainActivity content loads normally
splash overlay appears on top for ~900ms
AnimatorSet fades/scales glow + accent
then overlay visibility = gone
```

Pros:

- no new Activity lifecycle route;
- no dependency;
- no network;
- easy rollback;
- app can initialize normal content underneath;
- less risk than changing launcher Activity chain.

Cons:

- Android system splash still appears briefly before MainActivity draw;
- need ensure overlay does not block too long;
- must avoid repeated animation on every resume.

Recommended behavior:

```text
play only on cold app start / first MainActivity creation
not on every resume from editor/settings
```

### Option B — Dedicated SplashActivity

Create `SplashActivity` as launcher, then route to `MainActivity`.

Pros:

- clearer branding separation;
- splash code isolated.

Cons:

- adds Activity route/manifest complexity;
- risk back-stack oddities if not finished correctly;
- can delay startup more easily;
- needs more UAT for back/rotation.

Not recommended for MVP unless MainActivity overlay proves messy.

### Option C — Lottie / animation JSON

Pros:

- smooth designer-like animation.

Cons:

- new dependency/supply-chain review;
- asset pipeline complexity;
- not needed for subtle glow;
- overkill for current NOTEZ principles.

Not recommended for MVP.

---

## 9. Recommended MVP implementation plan

```text
Phase 1 — Design assets
- Keep selected concept image as reference.
- Prepare production splash assets locally:
  - base horse crest PNG/vector;
  - optional glow silhouette layer;
  - green accent drawable/vector.
- Prepare simplified launcher foreground candidate.

Phase 2 — Static launcher icon
- Replace current N-only foreground only after icon crop/readability is approved.
- Keep adaptive icon background dark.
- Add monochrome icon candidate if simple enough.

Phase 3 — Splash overlay
- Add overlay to MainActivity or root layout.
- Animate alpha/scale for horse/glow/accent.
- Skip or shorten animation when system animators are disabled.
- Remove overlay after animation.

Phase 4 — Verification
- Static XML/resource checks.
- Confirm no INTERNET permission.
- Confirm no new dependency unless explicitly approved.
- Debug CI build.
- User device UAT.
```

Recommended MVP scope if implemented next:

```text
1. Keep current launcher icon for now.
2. Add splash glow mock using local generated asset.
3. Test startup feel on device.
4. If UAT passes, then replace launcher icon in a separate patch.
```

Reason:

```text
Splash behavior risk and launcher icon crop risk are different.
Separating them gives easier rollback and clearer UAT.
```

---

## 10. Acceptance criteria

### 10.1 Product acceptance

- [ ] On cold app open, horse crest splash appears briefly.
- [ ] Glow appears around horse edges.
- [ ] Green accent appears like light/coretan from lower area.
- [ ] Splash feels premium, not noisy.
- [ ] Total perceived delay stays around `<= 1100ms`.
- [ ] Splash does not replay every time returning from editor.
- [ ] Home remains unchanged after splash ends.
- [ ] Existing note create/search/open/delete flows unchanged.
- [ ] Music drawer/settings/Markdown guide unchanged.
- [ ] Launcher icon remains readable if/when changed.

### 10.2 Security/privacy acceptance

- [ ] No `android.permission.INTERNET`.
- [ ] No CDN/remote assets.
- [ ] No external scripts/styles/fonts/images.
- [ ] No `addJavascriptInterface`.
- [ ] No telemetry or startup network.
- [ ] No trademarked third-party logo/name copied into assets.

### 10.3 Technical acceptance

- [ ] XML parse valid.
- [ ] Resource references valid.
- [ ] Debug CI build passes.
- [ ] No new dependency for MVP.
- [ ] Animation cleanup happens after end/cancel.
- [ ] Overlay cannot permanently trap taps if animation fails.
- [ ] Orientation/rotation does not create crash or endless splash loop.
- [ ] Asset sizes are reasonable for APK/startup.
- [ ] No generated cache/build artifacts committed.

---

## 11. UAT plan

Manual UAT after implementation and CI green:

1. Install debug APK.
2. Force stop NOTEZ.
3. Open NOTEZ from launcher.
4. Confirm splash appears once and ends by itself.
5. Confirm glow around horse is visible but not excessive.
6. Confirm green accent light appears from lower area.
7. Confirm home is usable immediately after splash.
8. Tap `+`, create/open note.
9. Press back home; confirm splash does not replay.
10. Open editor/view mode; return home; confirm no replay.
11. Rotate device during or after splash if possible.
12. Try low-brightness dark mode visibility.
13. If animation scale disabled in Developer Options, confirm app still opens cleanly.
14. Confirm no visual crop issue if launcher icon is changed in same build.

Evidence labels:

```text
DESIGNED        = RFC written.
IMPLEMENTED     = code/assets exist.
LOCALLY VERIFIED = static checks available and passed.
CI VERIFIED     = GitHub Actions build passed.
DEVICE VERIFIED = user installed APK and reported pass.
RELEASED        = APK published to GitHub Release.
```

---

## 12. Risks and mitigations

### Risk 1 — Splash makes NOTEZ feel slower

Mitigation:

- target under `1100ms`;
- avoid blocking heavy work;
- allow skip/short path if animation disabled;
- play only on cold start.

### Risk 2 — Glow looks too AI/noisy/sticker-like

Mitigation:

- use clean glow layer;
- reduce alpha;
- avoid smoke texture;
- prefer simple silhouette/edge light.

### Risk 3 — Large PNG increases APK/startup cost

Mitigation:

- optimize PNG/WebP if appropriate;
- keep asset dimensions reasonable;
- avoid multiple huge layers;
- test release artifact size later.

### Risk 4 — Activity lifecycle bugs

Mitigation:

- prefer MainActivity overlay for MVP;
- remove/cancel animator in lifecycle cleanup;
- ensure overlay gone on cancel/failure;
- UAT rotation/back/resume.

### Risk 5 — Trademark similarity

Mitigation:

- keep original horse/crest shape;
- no brand names/logos;
- no castle/knight motif;
- no packaging-like design.

---

## 13. Rollback plan

If splash is disliked or causes startup issue:

```text
revert splash layout/code/assets
keep existing MainActivity launcher path
no database migration needed
no note data affected
```

If launcher icon replacement is disliked:

```text
restore current adaptive icon foreground @drawable/ic_notez_fg
keep same app label/package/data
```

Because this is visual only, rollback should be low-risk if implementation stays isolated.

---

## 14. Open decisions before coding

Before implementation, decide:

1. Use generated concept image directly as temporary splash base, or first clean it into dedicated layers?
2. Splash first, launcher icon later, or both in same patch?
3. Keep full horse body in splash, or crop to horse/head crest for stronger readability?
4. Keep current N-only launcher icon until UAT, or replace immediately with horse crest icon?

Recommendation:

```text
1. Clean into dedicated layers first if possible.
2. Implement splash first.
3. Use full horse crest for splash, simplified horse/head for launcher later.
4. Keep current launcher icon until splash UAT passes.
```

---

## 15. Current status

```text
Status: IMPLEMENTED LOCALLY.
Concept image exists as design mockup.
Production launcher icon asset already installed from previous icon trial.
Splash overlay assets/code are installed locally in MainActivity.
Static XML/resource/security checks passed locally.
Local Gradle build is not available in the sandbox because there is no Gradle wrapper/global gradle.
CI pending until pushed.
Device UAT pending.
```
