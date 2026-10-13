#!/usr/bin/env python3
"""Apply the Z-Stream identity patch to the APK-matched CloudStream checkout."""
from __future__ import annotations
import re, sys, subprocess
from pathlib import Path
SOURCE_COMMIT = "8bdc5999968265d74dae8ddff3656950be255edc"
UPSTREAM_URL = "https://github.com/recloudstream/cloudstream"
FORK_URL = "https://github.com/whitesr09/OmniStream"

def replace_exact(text, old, new, label, expected=1):
    n = text.count(old)
    if n == 0 and new in text:
        return text
    if n != expected:
        raise ValueError(f"{label}: expected {expected} occurrence(s), found {n}")
    return text.replace(old, new)

def edit(path, transform):
    old = path.read_text(encoding="utf-8")
    new = transform(old)
    if old != new:
        path.write_text(new, encoding="utf-8")

def replace_color(text, name, old, new):
    a = f'<color name="{name}">{old}</color>'
    b = f'<color name="{name}">{new}</color>'
    if a in text: return text.replace(a, b, 1)
    if b in text: return text
    raise ValueError(f"colors.xml: unexpected value for {name}")

def rebrand_string_file(text):
    def fix(m):
        attrs, body = m.group("attrs"), m.group("body")
        km = re.search(r'\bname="([^"]+)"', attrs)
        key = km.group(1) if km else ""
        if key in {"legal_notice_text", "cs3wiki"} or "https://" in body or "http://" in body:
            return m.group(0)
        body = "Z-Stream" if key == "app_name" else re.sub(r"(?i)CloudStream", "Z-Stream", body)
        return m.group("open") + body + m.group("close")
    return re.sub(r'(?P<open><string\b(?P<attrs>[^>]*)>)(?P<body>.*?)(?P<close></string>)',
                  fix, text, flags=re.S)

def replace_themes(text):
    arrays = {
      "themes_names": '''<string-array name="themes_names">
        <item>Z-Stream Cinema</item><item>Dark</item><item>Gray</item><item>Amoled</item>
        <item>Flashbang</item><item>System</item><item>Material You</item><item>Dracula</item>
        <item>Lavender Dreams</item><item>Silent Blue</item>
      </string-array>''',
      "themes_names_values": '''<string-array name="themes_names_values">
        <item>Light</item><item>AmoledLight</item><item>Black</item><item>Amoled</item>
        <item>Light</item><item>System</item><item>Monet</item><item>Dracula</item>
        <item>Lavender</item><item>SilentBlue</item>
      </string-array>'''
    }
    for key, replacement in arrays.items():
        pattern = rf'<string-array name="{key}">.*?</string-array>'
        if replacement not in text:
            text, n = re.subn(pattern, replacement, text, count=1, flags=re.S)
            if n != 1: raise ValueError(f"array.xml: cannot find {key}")
    return text

def draw_icons(root):
    from PIL import Image, ImageDraw
    def icon(size, round_icon=False):
        im = Image.new("RGBA", (size,size), (7,24,47,255))
        px = im.load()
        for y in range(size):
            for x in range(size):
                t = min(1.0, (x+y)/(2*size))
                px[x,y] = (int(7+20*t), int(24+61*t), int(47+112*t), 255)
        d = ImageDraw.Draw(im)
        margin = int(size*.115)
        if round_icon:
            mask = Image.new("L",(size,size),0)
            ImageDraw.Draw(mask).ellipse((0,0,size-1,size-1),fill=255)
            im.putalpha(mask)
            d = ImageDraw.Draw(im)
        d.rounded_rectangle((margin,margin,size-margin,size-margin),radius=max(1,int(size*.15)),
                            outline=(103,174,255,160),width=max(1,int(size*.012)))
        p=[(size*.27,size*.29),(size*.73,size*.29),(size*.73,size*.39),
           (size*.42,size*.62),(size*.73,size*.62),(size*.73,size*.72),
           (size*.27,size*.72),(size*.27,size*.62),(size*.58,size*.39),(size*.27,size*.39)]
        d.polygon(p,fill=(255,255,255,255))
        d.line((int(size*.29),int(size*.31),int(size*.69),int(size*.31)),
               fill=(110,205,255,230),width=max(1,int(size*.009)))
        return im
    for source in (root/"app/src/main", root/"app/src/debug", root/"app/src/prerelease"):
        if not (source/"res").is_dir(): continue
        for density, size in (("mdpi",48),("hdpi",72),("xhdpi",96),("xxhdpi",144),("xxxhdpi",192)):
            folder = source/"res"/f"mipmap-{density}"
            folder.mkdir(parents=True, exist_ok=True)
            icon(size).save(folder/"ic_launcher.png")
            icon(size, True).save(folder/"ic_launcher_round.png")
        store = source/"ic_launcher-playstore.png"
        if store.parent.exists(): icon(512).save(store)
        vector = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#07182F" android:pathData="M0,0h108v108h-108z"/>
    <path android:fillColor="#FFFFFF" android:pathData="M29,31h50v10l-32,26h32v10h-50v-10l32,-26h-32z"/>
    <path android:fillColor="#6ECAFF" android:pathData="M29,31h50v4h-50z"/>
</vector>
'''
        for path in (source/"res/drawable/ic_launcher_foreground.xml",
                     source/"res/drawable-v24/ic_launcher_foreground.xml"):
            if path.parent.exists(): path.write_text(vector, encoding="utf-8")
        for path in (source/"res/drawable/ic_launcher_background.xml",
                     source/"res/values/ic_launcher_background.xml"):
            if path.is_file():
                s = path.read_text(encoding="utf-8")
                s = re.sub(r'#[0-9A-Fa-f]{6,8}', "#07182F", s, count=1)
                path.write_text(s, encoding="utf-8")
    splash = root/"app/src/main/res/drawable/splash_background.xml"
    if splash.exists():
        edit(splash, lambda s: s.replace('<color android:color="?attr/primaryBlackBackground"/>',
                                         '<color android:color="#07182F"/>'))

def main():
    root = Path(sys.argv[1] if len(sys.argv)>1 else ".").resolve()
    try: commit = subprocess.check_output(["git","rev-parse","HEAD"],cwd=root,text=True).strip()
    except Exception: commit = ""
    if commit and not commit.startswith(SOURCE_COMMIT[:7]):
        raise SystemExit(f"Wrong source revision {commit}; expected {SOURCE_COMMIT}")
    for path in (root/"app/src").glob("**/res/values*/strings.xml"):
        if path.name != "donottranslate-strings.xml": edit(path, rebrand_string_file)
    p = root/"app/src/main/res/values/donottranslate-strings.xml"
    def nontrans(s):
        s = replace_exact(s, '<string name="app_name_download_path">Cloudstream</string>',
                          '<string name="app_name_download_path">Z-Stream</string>', "download folder label")
        if "Z-Stream is an unofficial CloudStream fork maintained by N S H D." not in s:
            start = s.find('<string name="legal_notice_text">')
            end = s.find("</string>", start)
            if start < 0 or end < 0: raise ValueError("legal_notice_text missing")
            credit = ("\\n\\nZ-Stream is an unofficial CloudStream fork maintained by N S H D. "
                      "Original CloudStream authors and contributors retain their original attribution. "
                      "This fork is not affiliated with or endorsed by reCloudStream. The upstream GPL-3.0 "
                      "license and copyright notices are retained. Corresponding modified source must be "
                      "made available when redistributing this build.")
            s = s[:end] + credit + s[end:]
        return s
    edit(p, nontrans)
    edit(root/"app/build.gradle.kts", lambda s: replace_exact(s,
         'applicationId = "com.lagradost.cloudstream3"', 'applicationId = "com.nshd.zstream"', "application ID"))
    edit(root/"gradle/libs.versions.toml", lambda s: replace_exact(s,
         'versionName = "4.8.0"', 'versionName = "4.8.0-zstream.1"', "version name"))
    def light_default(s):
        old = 'settingsManager.getString(act.getString(R.string.app_theme_key), "AmoledLight")'
        new = 'settingsManager.getString(act.getString(R.string.app_theme_key), "Light")'
        if old in s: return replace_exact(s, old, new, "first-run light theme", expected=2)
        if s.count(new) >= 2: return s
        raise ValueError("theme fallback expressions not found")
    edit(root/"app/src/main/java/com/lagradost/cloudstream3/CommonActivity.kt", light_default)
    edit(root/"app/src/main/res/values/array.xml", replace_themes)
    def palette(s):
        for name, old, new in [
          ("colorPrimary","#3d50fa","#2563EB"),("colorPrimaryDark","#3700B3","#1D4ED8"),
          ("colorAccent","#3b65f5","#3B82F6"),("lightPrimaryGrayBackground","#f1f1f1","#F3F6FC"),
          ("lightBitDarkerGrayBackground","#fff","#FFFFFF"),("lightGrayBackground","#eeeeee","#E6EDF8"),
          ("lightItemBackground","#eeeeee","#FFFFFF"),("lightTextColor","#202125","#12213A"),
          ("lightGrayTextColor","#5f6267","#5B6B84"),("lightIconColor","#5f6267","#2E5FA8")]:
            s = replace_color(s, name, old, new)
        if "zstream_splash_bg" not in s:
            s = s.replace("</resources>", '    <color name="zstream_splash_bg">#07182F</color>\\n</resources>')
        return s
    edit(root/"app/src/main/res/values/colors.xml", palette)
    def updater(s):
        s = replace_exact(s, 'private const val GITHUB_USER_NAME = "recloudstream"',
                          'private const val GITHUB_USER_NAME = "whitesr09"', "updater owner")
        s = replace_exact(s, 'private const val GITHUB_REPO = "cloudstream"',
                          'private const val GITHUB_REPO = "OmniStream"', "updater repository")
        s = replace_exact(s, 'private const val PRERELEASE_PACKAGE_NAME = "com.lagradost.cloudstream3.prerelease"',
                          'private const val PRERELEASE_PACKAGE_NAME = "com.nshd.zstream.prerelease"', "prerelease package")
        return s
    edit(root/"app/src/main/java/com/lagradost/cloudstream3/utils/InAppUpdater.kt", updater)
    edit(root/"app/src/main/res/xml/settings_general.xml", lambda s: replace_exact(
         s, UPSTREAM_URL, FORK_URL, "settings repository links", expected=2))
    def banner(s):
        title = "# Z-Stream — unofficial CloudStream fork\\n\\n"
        if title in s: return s
        intro = ("# Z-Stream — unofficial CloudStream fork\\n\\n> Maintainer: **N S H D**. Derived from the original "
                 f"[CloudStream project]({UPSTREAM_URL}). Original authors, contributor credits, copyright notices "
                 "and the GPL-3.0 license are retained. This fork is not affiliated with or endorsed by reCloudStream.\\n\\n"
                 "---\\n\\n## Upstream CloudStream documentation\\n")
        return replace_exact(s, "# CloudStream\\n", intro, "README fork banner")
    edit(root/"README.md", banner)
    (root/"ZSTREAM-FORK-NOTICE.md").write_text(f"""# Z-Stream fork and license notice

Z-Stream is an unofficial derivative of [CloudStream]({UPSTREAM_URL}), based on source commit `{SOURCE_COMMIT}` (the revision recorded in the supplied version 4.8.0 APK).

**Fork maintainer:** N S H D.

**Original attribution:** CloudStream's original authors and contributors retain their original copyright and attribution. This fork does not claim authorship of the upstream application and is not affiliated with or endorsed by reCloudStream.

**License:** GPL-3.0. The original `LICENSE` file and code copyright notices remain untouched. When redistributing a modified APK, provide corresponding source and build instructions for that exact build, preserving upstream notices and this fork notice.

**Repository / updates:** {FORK_URL}
""", encoding="utf-8")
    draw_icons(root)
    print("Z-Stream rebrand applied to", root)

if __name__ == "__main__":
    main()
