# Intel

A personal Android app for The Division 2's weekly Manhunt. It shows this week's three Scout objectives one at a time, decoded into plain words, so there is no need to read guide sites.

## Install on the Pixel

Add this repo to Obtainium (`https://github.com/Gandalondon/intel`). Obtainium installs `Intel.apk` from the latest release and offers each new build as an update.

## How it updates

- **The app:** every push to `main` that changes the app builds an APK and publishes it as a release. Obtainium picks it up. The signing key is stored as the repo secrets `SIGNING_KEYSTORE_B64` and `SIGNING_PASSWORD`, never in the code, so every build is signed the same way and updates install over the top.
- **The weekly objectives:** `weeks/current.json`. The app downloads it every time it opens, so new objectives never need a new build. A new `id` resets progress to Objective 1. Offline, it shows the last week it downloaded.

## Files

- `app/`: the app (Kotlin, Jetpack Compose, one screen).
- `weeks/current.json`: this week's objectives.
- `docs/style-guide.html`: the style guide. Edit `tools/style-guide/template.html`, then run `python3 tools/style-guide/make.py`.
