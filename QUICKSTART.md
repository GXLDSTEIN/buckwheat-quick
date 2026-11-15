# Buckwheat Quick

A personal fork of [Buckwheat](https://github.com/danilkinkin/buckwheat) for recording purchases immediately and seeing the remaining budget. GPL-3.0; upstream attribution and license are retained.

## First version (0.1.0)

- Enter an amount using the existing numeric keypad and tap the check mark to save.
- Category and comment are independent, optional fields. Frequent categories appear first; create a category from the same row.
- See the signed remainder for today and for the selected budget period. Set the end date to month end or your next payday in the wallet screen.
- Unspent money is redistributed across the remaining days by default.
- Undo a newly added purchase from the confirmation message; edit earlier purchases in history.
- Category analytics and CSV export use the separate category field.
- This installs as `app.buckwheat.quick`, alongside the original app, with its own empty database.
- Starting a new budget period retains previous records in the database. An archive browsing screen and full backup/restore are future work; current history and exports show the active period.

## Build

Requirements: JDK 17, Android SDK platform 36 and build tools 35.0.0 (Gradle can install accepted SDK components).
Set `ANDROID_HOME` or put `sdk.dir=/path/to/Android/Sdk` in an untracked `local.properties`.

Create a permanent signing key once, and keep a backup outside the repository:

```sh
mkdir -p ~/.local/share/buckwheat-quick
keytool -genkeypair -keystore ~/.local/share/buckwheat-quick/signing.jks \
  -storepass buckwheat-quick -keypass buckwheat-quick -alias buckwheat-quick \
  -keyalg RSA -keysize 3072 -validity 10000 -dname 'CN=Buckwheat Quick'
chmod 600 ~/.local/share/buckwheat-quick/signing.jks
./gradlew :app:assembleRelease :app:lintDebug :app:assembleDebugAndroidTest
```

APK: `app/build/outputs/apk/release/app-release.apk`. Release and debug builds use the same personal signing key locally so updates preserve app data. Override key location/passwords with `BUCKWHEAT_KEYSTORE`, `BUCKWHEAT_STORE_PASSWORD`, and `BUCKWHEAT_KEY_PASSWORD`.

On a device/emulator, run the new repository regression tests:

```sh
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.danilkinkin.buckwheat.di.QuickExpenseRepositoryTest
```

## GitHub versions

The Android workflow checks debug builds, compiles instrumentation tests and runs lint on pushes and pull requests. These checks use a temporary test key. Only tagged releases use the permanent signing key and publish installable updates.

For tagged releases, add the permanent key as a base64 GitHub Actions secret `BUCKWHEAT_KEYSTORE_BASE64` (do not commit it). Add its passwords as `BUCKWHEAT_STORE_PASSWORD` and `BUCKWHEAT_KEY_PASSWORD`. A tag such as `v0.1.0` builds a signed APK and publishes it in GitHub Releases. The workflow verifies that the tag matches `versionName`, and that the APK certificate matches `.github/release-certificate.sha256`.

For each update, increase `versionCode` and `versionName` in `app/build.gradle.kts`, update the changelog, commit and push, then push the matching version tag. Keep the permanent signing key. Releases contain `buckwheat-quick.apk` and `SHA256SUMS`; an existing published APK is preserved when a workflow is re-run.

To repeat an existing release using the latest workflow, run the Android workflow manually with `release_tag` set to the existing tag (for example, `v0.1.0`). Leave that input empty to run checks only. Existing published APK assets are preserved.

Users add the repository URL to Obtainium to install and track updates: [official Obtainium instructions](https://wiki.obtainium.imranr.dev/ui_overview/#add-app-page).

Financial records are local to the phone. GitHub contains source and APKs, not your expense database. Future database schema changes must include non-destructive migrations; destructive fallback is disabled.
