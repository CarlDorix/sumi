# Getting started with Sumi

Sumi ships empty. It has no sources and no content of its own — you add sources, then use them to
find things to read.

## 1. Add an extension repository

This is the step everything else depends on, and the one most problems trace back to.

A **repository** is a list of extensions. An **extension** teaches Sumi how to read one specific
site. Without a repo you have no extensions; without extensions you have no sources; without
sources the app has nothing to show you.

**Search → ⋮ → Extensions → ⋮ → Edit repos**, then add:

```
https://raw.githubusercontent.com/keiyoushi/extensions/repo
```

If you migrated from an older Tachiyomi or Mihon install, check what's already listed. Repos ending
in `tachiyomiorg/extensions` are dead — that project stopped publishing in January 2024, so any
extension from it is frozen at whatever version it was then. Sites have changed since, which is why
those sources return nothing, fail to search, or fail to update. Remove them.

## 2. Install extensions

**Search → ⋮ → Extensions.** Browse the list and install the ones for sites you want. Sumi will warn
that an extension is untrusted the first time — that's it telling you the extension was signed by
someone other than the app author, which is true of all of them.

Extension **updates** appear here too, and a badge on the Search tab tells you when some are
waiting. If that badge never appears, your repo is probably dead — see step 1.

## 3. Find something to read

**Search** is the tab for this. Type in the field at the top and press search to look across every
enabled source at once, or tap one of your source chips to browse just that one.

The screen also shows popular titles from a source of your choosing — tap **Change** beside the
heading to pick which — plus your recent searches once you've made some.

## 4. Add to your library

Open anything and tap the heart. That puts it in your library, which is what the Home tab is about.

## The Home tab

Everything you'd reach for while reading lives here, as a row of tabs you can swipe between:

| Tab | What it's for |
|---|---|
| **Recent** | What you were last reading. Start here — it answers "what do I open next" |
| **Library** | Everything you've saved |
| **Unread** | Entries with chapters you haven't read |
| **Finished** | Started and fully caught up |
| **Downloaded** | Entries with chapters saved offline |
| **Genres** | Every genre in your library, with counts — tap one to browse it |
| **History** | What you've read, newest first, with chapter runs grouped together |

Rows with unread chapters show a **Continue reading** button that opens the next one directly.

## Settings

**Settings** holds two quick toggles at the top — *Downloaded only* (hide anything not saved
offline) and *Incognito* (stop recording history) — then the download queue, categories and
statistics, then everything configurable, grouped into cards.

To change how the library looks, or how it's sorted: **Home → Library → ⋮ → Sort and display**.

## Backups

**Settings → Data and storage → Create backup.** This writes a `.tachibk` file containing your
library, reading history, categories and settings — but *not* your downloaded chapters.

Restore the same way. Backups are compatible with Mihon's, so you can move between them.

**Make one before uninstalling anything.** App data goes with the app, and there is no recovery.

## When something breaks

Most source problems are extension problems, and most extension problems are repo problems. Before
anything else, check step 1.

Sumi's reader, downloader, extension system and backup format are all Mihon's, so
[Mihon's troubleshooting guide](https://mihon.app/docs/guides/troubleshooting/) applies to anything
below the interface — reading, downloading, tracking, storage.

For anything about *Sumi's* interface specifically — the tabs, the search screen, the layout —
that's this fork, and it belongs in
[the issue tracker](https://github.com/CarlDorix/sumi/issues). Please don't take interface
questions to Mihon; they didn't build this and can't help with it.
