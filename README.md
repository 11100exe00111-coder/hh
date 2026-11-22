# Client Boost

Client-side-only performance mod for Minecraft Java 1.20.1 (Forge 47.x), tuned for mobile launchers like Zalith Launcher 2.

All source files sit in the repo root (flat layout). `build.gradle` places `mods.toml` into `META-INF` automatically.
The workflow lives in `.github/workflows/build.yml`.

## Get the jar
Actions tab -> latest run -> Artifacts (bottom of the page) -> `clientboost-jar`.
The first build takes several minutes because Forge has to download Minecraft.

## Install
Put the jar in your Forge 1.20.1 version's `mods` folder. Config: `config/clientboost-client.toml`.
