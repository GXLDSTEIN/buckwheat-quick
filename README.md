# Buckwheat Quick

Fast expense entry with visible daily and period balances. A personal fork of [danilkinkin/buckwheat](https://github.com/danilkinkin/buckwheat), built with Kotlin and Jetpack Compose.

## Install and update

**[Download the latest APK](https://github.com/GXLDSTEIN/buckwheat-quick/releases/latest)** · Android 10 or newer.

To receive updates through [Obtainium](https://github.com/ImranR98/Obtainium):

1. Open Obtainium → **Add app**.
2. Paste `https://github.com/GXLDSTEIN/buckwheat-quick` as the source URL.
3. Add the app and install the APK. Future published releases appear as updates.

Each release has one universal APK, `buckwheat-quick.apk`. No APK filter or architecture selection is needed. Releases keep the same application ID and signing certificate, so updates preserve your purchases. This is a separate app from upstream Buckwheat, and starts with its own empty database on first installation.

### Установка через Obtainium

Открой Obtainium → **Добавить приложение**, вставь ссылку `https://github.com/GXLDSTEIN/buckwheat-quick` и добавь приложение. Обновления будут поступать из Releases этого репозитория.

При первом запуске задай бюджет и дату окончания периода: конец месяца или следующую зарплату. Для записи покупки достаточно суммы и нажатия галочки; категория и комментарий необязательны.

## Features

- Numeric keypad ready for recording a purchase.
- Separate category and optional free-text comment; two quick choices plus a full-screen category grid with search.
- Signed balance for today and the selected budget period, including overspending.
- One-tap analytics; tap any category in analytics or history for its expenses and comments.
- Undo after adding an expense, history access and CSV export.
- Automatic redistribution of the remaining budget across the remaining days by default.
- Local financial records: no account or synchronization required.

Archive browsing, full backup/restore and advanced category management are planned beyond the first version.

## Development

See [QUICKSTART.md](QUICKSTART.md) for building, signing and publishing releases; [CHANGELOG.md](CHANGELOG.md) for changes; [VALIDATION.md](VALIDATION.md) for the first version's checks.

## Upstream and license

Forked from [Buckwheat](https://github.com/danilkinkin/buckwheat), developed by [Danil Зakhvatkin](https://github.com/danilkinkin) and [contributors](https://github.com/danilkinkin/buckwheat/graphs/contributors). See the [original README](UPSTREAM_README.md). Licensed under [GNU GPL v3](LICENSE).
