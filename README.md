# Client Boost

Client-side-only performance mod for **Minecraft Java 1.20.1 (Forge 47.x)**.
Tuned by default for mobile launchers such as **Zalith Launcher 2**.

## Features
- Entity culling: skips rendering living entities beyond a distance (default 32 blocks)
- Nametag culling (default 16 blocks)
- Dynamic render distance: lowers render distance when FPS is low, restores it when FPS recovers
  (your own render distance setting is the maximum)
- Optional background FPS limiter (off by default)

Safe on any server: it is client-only and the server does not need it.

## Get the .jar (GitHub)
1. Push this folder to a GitHub repo (branch `main`).
2. Open the **Actions** tab, then the "Build mod jar" run. When it is green, download the
   `clientboost-jar` artifact (zip containing `clientboost-1.0.0.jar`).
3. Or create a release: `git tag v1.0.0 && git push origin v1.0.0`. The jar is attached to the release automatically.

## Install in Zalith Launcher 2
1. Install **Forge 1.20.1** as a game version in the launcher.
2. Put `clientboost-1.0.0.jar` in that version's `mods` folder
   (open the version's folder from the launcher, or use its mod manager if available).
3. Launch the Forge version.

Config: `config/clientboost-client.toml` (created on first launch).

## Build locally
JDK 17 and Gradle 8.1.1: `gradle build` -> `build/libs/clientboost-1.0.0.jar`.
Dev client: `gradle runClient`.
