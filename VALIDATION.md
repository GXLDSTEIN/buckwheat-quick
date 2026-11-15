# Validation — 0.1.0

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
