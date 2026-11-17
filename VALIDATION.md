# Validation

## 0.2.0 — 2026-11-17

- Debug APK and instrumentation APK build successfully; lint reports zero errors.
- All 10 instrumentation tests pass on Android 15: five existing repository checks and five new category UI/filter checks.
- With 81 categories, the picker shows at least eight selectable items without horizontal scrolling, finds a distant Cyrillic name case-insensitively, clears selection and passes search text to category creation.
- Category details preserve comments, sort newest first, exclude other categories and income transactions, and handle uncategorized and empty categories.
- Manual upgrade over 0.1.0 retained the previous purchases and period balance. The Room schema and signing key are unchanged.
- Manually entered 12, chose Cafés from the grid and added comment Cafe020; both draft fields survived the picker. The analytics shortcut opened the screen directly, and tapping Cafés showed exactly that purchase/comment with total 12 and count 1, excluding Groceries.

## 0.1.0 — 2026-11-15

Validated on 2026-11-15 with Android 15 (API 35), x86_64 emulator. Physical phone testing remains for the user.

## Build and analysis

`./gradlew :app:assembleRelease :app:assembleDebugAndroidTest :app:lintDebug` completed successfully. Lint reports zero errors; upstream warnings remain. APK signature verification passed with APK Signature Scheme v2.

APK: `dist/buckwheat-quick-0.1.0.apk`. Its SHA-256 checksum is beside the APK. The permanent signing key is outside the repository, at `~/.local/share/buckwheat-quick/signing.jks`.

## Repository regression tests

All five `QuickExpenseRepositoryTest` instrumentation tests passed on the emulator:

- Category and comment persist independently.
- An amount alone can be saved; daily overspending stays negative; removal restores the balance.
- Editing metadata preserves the transaction identity and budget totals.
- A new period retains earlier transactions and category references.
- The remaining money is redistributed correctly on the next day.

Test data uses a separate preferences directory and an in-memory Room database.

## Application checks

- Fresh installation starts with budget setup and no expense records.
- Set budget to 10,000 for November 15–30: daily allowance 625.
- Select Groceries and enter 850 with the numeric keypad: daily remainder -225; period remainder 9,150.
- Enter another 20 in Groceries with comment `online`: daily remainder -245; period remainder 9,130.
- Database inspection confirmed both category references and the independent comment.
- Force-stop/relaunch retained purchases and balances.
- Installed the signed release APK over the earlier build without clearing app data: purchases, comment and balances remained.
- History button opened the existing purchases with separate category and comment labels.

GitHub Actions checks builds and lint, and publishes version-tagged APK releases for Obtainium. Published updates must match the initial signing certificate.
