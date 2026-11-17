# Changelog

## 0.2.0 — 2026-11-17

- Replace the horizontal category strip with two quick choices and an always-visible All button.
- Choose from a full-screen category grid with search, vertical scrolling and larger cells at larger font sizes.
- Add a one-tap analytics shortcut beside history in the main toolbar.
- Tap categories in analytics or history to see only their expenses, comments, total and count for the active budget period.
- Keep every category individually accessible in analytics, including uncategorized expenses and categories beyond the chart’s top groups.
- Preserve the existing database, application ID and signing key when updating.

## 0.1.0 — 2026-11-15

First personal version, focused on fast purchase entry and visible budget balances.

- Separate optional category and comment for each expense.
- Built-in categories, custom category creation, and frequently used categories first.
- Always-visible signed balances for today and the selected budget period.
- Confirmation with undo after adding an expense; duplicate-save protection.
- Category labels in history, category-based analytics, and separate CSV columns.
- History button and immediate refresh of edited history rows.
- Separate installation and empty database; future schema changes require safe migrations.
- Previous period transactions remain stored when starting a new budget.
- Permanent local APK signing and a GitHub Actions build/release workflow.

Archive browsing, full backup/restore and advanced category management are outside this first version.
