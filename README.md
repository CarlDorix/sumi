# Sumi

A personal fork of [Mihon](https://github.com/mihonapp/mihon), reorganised around reading rather
than library management.

This is not an official Mihon build and is not affiliated with the Mihon project. It exists because
I wanted a different interface, not because anything was wrong with theirs.

## What's different

**Three navigation destinations instead of five** — Home, Search, Settings — behind a floating
translucent bar that content scrolls beneath.

**Home** collects everything you'd reach for while reading, as one row of tabs:

| Tab | What it shows |
|---|---|
| Recent | Reading feed built from history, with a cover shelf and continue-reading list |
| Library | The full library, with a hero card and wide featured tiles |
| Unread | Entries with chapters left |
| Finished | Started and caught up |
| Downloaded | Entries with downloaded chapters |
| Genres | Every genre in your library, with counts |
| History | Chapter runs grouped into single rows, with swipe actions |

**Search** is search-first: an inline field rather than a magnifier, a popular shelf from a source
you choose, persisted recent searches, and source and genre chips. Sources, extensions and
migration moved behind the overflow — they're destinations, not scenery.

**Settings** puts library toggles and actions above the settings categories, all in grouped cards,
with every settings screen rendering its groups the same way.

**Manga details** lead with the entry's own artwork, show read progress, and distinguish read from
unread chapters by weight rather than dimming alone.

Also: an **Ember** theme (warm, high contrast), read-progress bars throughout, and a shared
component set so the lists, headers and chips stay consistent.

## Content

Like Mihon, Sumi ships with **no sources and no content**. It reads what you point it at through
extensions you install yourself. Nothing is bundled, hosted or distributed here.

## Building

Standard Android project — open in Android Studio and run, or:

```bash
./gradlew installDebug
```

Requires JDK 17+ (the build provisions its own toolchain) and the Android SDK.

## Credit and licence

Sumi is built on Mihon, which is built on Tachiyomi. Essentially all of the engine — the source
API, reader, downloader, database and sync — is their work; this fork mostly rewrites the
presentation layer.

Licensed under [Apache 2.0](LICENSE), as Mihon is. See the [Mihon repository](https://github.com/mihonapp/mihon)
for the upstream project, and please direct issues about the underlying app there only if you can
reproduce them on an official Mihon build.
