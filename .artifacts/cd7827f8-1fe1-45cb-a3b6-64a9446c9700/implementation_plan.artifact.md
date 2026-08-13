# Add "Copy library to clipboard" button

This plan adds a button to the Library toolbar overflow menu that allows users to copy a list of all manga/manhwa titles in their current library (respecting active filters/search) to the clipboard.

## User Review Required

> [!NOTE]
> The list will currently only contain manga titles, separated by newlines. If a filter or search is active, only the visible items will be included.

## Proposed Changes

### [Library]

#### [MODIFY] [LibraryTab.kt](file:///C:/Users/emman/Documents/Manga_APP/mihon/app/src/main/java/eu/kanade/tachiyomi/ui/library/LibraryTab.kt)
- Add `onClickGetLibraryList` callback that retrieves titles from `state.libraryData.favorites` and uses `context.copyToClipboard`.
- Pass this callback to `LibraryToolbar`.

#### [MODIFY] [LibraryToolbar.kt](file:///C:/Users/emman/Documents/Manga_APP/mihon/app/src/main/java/eu/kanade/presentation/library/components/LibraryToolbar.kt)
- Update `LibraryToolbar` and `LibraryRegularToolbar` to accept `onClickGetLibraryList`.
- Add an `OverflowAction` in `LibraryRegularToolbar` to trigger the callback.

## Verification Plan

### Manual Verification
1. Open the Library tab.
2. Tap the overflow menu (three dots) in the toolbar.
3. Select "Library List" (or the chosen string).
4. Verify that a list of titles is copied to the clipboard.
5. Apply a filter or search and verify that only the filtered items are copied.
