# UAT Result — Markdown Guide + FAB Floating Cleanup

**Tanggal:** 2026-09-27  
**Status:** DEVICE UAT PASS — debug APK; signed release pending  
**Tested feature commit:** `f84bc7e` (`feat: add local markdown guide`)  
**Tester:** User/device UAT  
**Build type tested:** Debug APK from GitHub Actions  
**Debug workflow:** `36296562618` — https://github.com/muzape28-blip/NOTEZ/actions/runs/36296562618  
**Debug artifact:** `notez-debug` (`10923739450`, 7,716,621 bytes)

---

## 1. Summary

User reported all tested behavior passed after installing the debug APK from CI.

User feedback:

```text
Mmmmm semuanya paass
```

Features covered by this UAT pass:

- drawer menu `Panduan Markdown`;
- local/offline Markdown guide page;
- clickable table of contents / internal heading anchors;
- detailed raw HTML explanation in guide;
- raw HTML remains disabled/escaped;
- external links remain external;
- home FAB bottom-block cleanup;
- existing app behavior reported safe.

---

## 2. Evidence level

```text
DESIGNED                  : YES — RFC docs available
IMPLEMENTED               : YES — commit f84bc7e
LOCAL STATIC VERIFIED     : YES — XML/source/Markdown/JS guards passed
CI DEBUG VERIFIED         : YES — workflow 36296562618 success
DEVICE DEBUG VERIFIED     : YES — user installed debug APK and reported PASS
PRODUCTION BUILD VERIFIED : PENDING
SIGNED APK DEVICE UAT     : PENDING
GITHUB RELEASE PUBLISHED  : PENDING
```

---

## 3. Local/static verification run before push

```text
XML parse seluruh app/src/main XML       : PASS
No INTERNET permission                  : PASS
No addJavascriptInterface               : PASS
No external script/CDN guard             : PASS
Markdown guide fence balance             : PASS
Markdown guide anchor/link matching      : PASS
Feature wiring guards                    : PASS
markdown-it asset node --check           : PASS
Renderer JS extracted node --check       : PASS
markdown-it guide render smoke           : PASS
git diff --check                         : PASS
```

Markdown guide smoke:

```json
{"h2Count":30,"tocAnchor":true,"rawScriptEscaped":true,"htmlLength":63123}
```

---

## 4. Checklist result

| Area | Result | Evidence |
| --- | --- | --- |
| Debug CI build | PASS | Workflow `36296562618` success |
| Debug APK install | PASS | User reported all pass |
| Drawer `Panduan Markdown` | PASS | User reported all pass |
| Markdown guide local/offline | PASS | User reported all pass; static guard checks asset path |
| Clickable TOC / anchors | PASS | Static guard + user reported all pass |
| Raw HTML explanation | PASS | Guide content updated; user approved detailed wording |
| Raw HTML safety | PASS | Static smoke confirmed raw `<script>` escaped |
| FAB bottom-block cleanup | PASS | User reported all pass after debug APK |
| Existing features | PASS | User reported all pass |
| Signed release APK | PENDING | Needs production build + UAT |
| GitHub Release | PENDING | Not published yet |

---

## 5. Known limitations / follow-up

- Raw HTML allowlist remains future candidate only; raw HTML stays disabled/escaped.
- Remote images remain placeholders because NOTEZ keeps no `INTERNET` permission.
- Theme/plugin/material UI discussion is deferred until after `v0.1.2` release.
- Future candidate: Theme System v2 / Appearance Polish inspired by Obsidian/Material, without arbitrary plugin/CSS marketplace in the near term.

---

## 6. Release readiness

Debug CI + device UAT are enough to prepare release docs and production workflow dispatch, but signed release APK must still be built by the production workflow and UAT-labeled honestly after install.
