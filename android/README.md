# Sumi mark — Android launcher assets

Drop into `app/src/main/res/`:

    drawable/ic_launcher_background.xml
    drawable/ic_launcher_foreground.xml
    drawable/ic_launcher_monochrome.xml
    mipmap-anydpi-v26/ic_launcher.xml

Colors (already in `values/colors.xml`):
- `icon_background` #FAFAFA
- `accent_blue` #0058A0
- ink #031019

Geometry: 108 viewport, painted extents stay within r=33 (66dp safe circle).
Legacy PNG mipmaps (mdpi–xxxhdpi) still need generating from the 512 master.
