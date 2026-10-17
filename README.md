# AE2 Light UI Compat

**English** | [简体中文](README.zh-CN.md)

A client-side button styling companion for Applied Energistics 2 on **Minecraft 1.20.1 with Forge**. It gives buttons registered through AE2's widget system their own light-theme texture and readable text colors, without globally replacing Minecraft's shared button texture.

[Published releases](https://github.com/yxchen-leo/AE2-Light-UI-Compat/releases) · [Report an issue](https://github.com/yxchen-leo/AE2-Light-UI-Compat/issues)

Current source version: **1.1.0**, paired with **AE2 & Addons Texture Backport 1.1.0**. Local artifacts are in `build/libs/`; this update has not been published to GitHub Releases.

The vertical toolbar now has a continuous, dynamically sized background connected to the main panel. Registered full-size toolbar icons use 18×20 backgrounds, spacing 6, hover depression and keyboard focus styling. Half-size icons retain their existing renderer. Original actions and 16×16 hitboxes are preserved. Both `textures/gui/vertical_buttons_bg.png` and `textures/gui/toolbar_buttons.png` must be present in the `ae2lightuicompat` namespace to enable the feature; resource reloads re-evaluate availability.

The companion pack sets the common toolbar origin to `left: 3, top: 1`. Crafting status keeps its own origin. The build and offline transformation checks pass for Forge 47.4.0 / AE2 15.4.11; no in-game visual acceptance has been performed.


## Why this mod exists

When backporting newer AE2 interfaces to Minecraft 1.20.1, some controls still use Minecraft's shared button renderer. Priority adjustment buttons, such as `+1` and `+5`, can retain their original appearance even when the surrounding screen has a light theme. Replacing the shared texture in a resource pack would also change buttons elsewhere in the game.

AE2 Light UI Compat marks buttons registered through AE2's widget container and gives those buttons a dedicated texture. This lets a compatible resource pack style these controls while keeping unrelated buttons in their original style.

## Features and scope

- Dedicated light-theme textures for supported AE2 buttons, including priority adjustment controls.
- Dark text on active buttons and a separate disabled text color, preserving the original text alpha.
- Automatic fallback to the original rendering when the dedicated texture is unavailable, including after disabling the resource pack and reloading resources.
- Client-side installation; no server installation required.

This mod is the button compatibility component for **AE2 & Addons Texture Backport**. The separate resource pack supplies the backported GUI, item, block, and animated textures. This JAR does not bundle that resource pack or independently replace all AE2 interfaces.

Add-on controls can benefit when they use AE2's widget registration system. Custom add-on renderers and independently created buttons are not automatically covered; compatibility with every add-on is not guaranteed.

## Requirements

| Component | Supported target |
| --- | --- |
| Minecraft | 1.20.1 |
| Mod loader | Forge 47.x |
| Applied Energistics 2 | 15.x, with its required dependencies |
| Resource pack | AE2 & Addons Texture Backport, or another pack providing the dedicated button texture |
| Installation side | Client only |

The build and offline injection checks use **Forge 47.4.0 / AE2 15.4.11**. Fabric, NeoForge, and other Minecraft versions are not supported by this release.

## Installation

1. Install Forge for Minecraft 1.20.1, AE2, and AE2's required dependencies, including GuideME for AE2 15.4.11.
2. Download `AE2-Light-UI-Compat-Forge-1.20.1-1.1.0.jar` from [Releases](https://github.com/yxchen-leo/AE2-Light-UI-Compat/releases).
3. Put the JAR in your client instance's `mods` folder.
4. Install and enable the compatible resource pack in Minecraft's resource pack settings. The pack is distributed separately and is not included in this repository's Release assets.
5. When upgrading, remove the previous companion JAR, including any old `AE2-Isolated-Light-Buttons` version. Keep only one copy installed.

Without the dedicated resource-pack texture, the mod leaves button rendering unchanged. You do not need Gradle or the source files to play.

## Troubleshooting

- **Buttons still look unchanged:** confirm that the compatible pack is enabled and supplies the texture path below. Check whether a higher-priority pack overrides it, then reload resources with `F3 + T`.
- **Only some add-on buttons change:** the mod applies to buttons registered through AE2's widget system; a custom button may use another rendering path.
- **Duplicate-mod or dependency error:** remove older copies and check your Minecraft, Forge, AE2, and AE2 dependency versions.

For a bug report, include the relevant versions, enabled resource packs and their order, the affected screen, reproduction steps, and a screenshot or relevant log excerpt. Remove personal information from logs before sharing.

## For resource-pack authors

Provide this file using the Minecraft 1.20.1 `widgets.png` button layout:

```text
assets/ae2lightuicompat/textures/gui/widgets.png
```

Active text uses `#413F54`; disabled text uses `#D2D3DF`. The mod preserves the existing alpha component.

Since 1.0.2, the internal mod ID and texture namespace are `ae2lightuicompat`. Use resource pack 1.0.3 or newer and remove all older companion JARs before upgrading; the old ID and texture path are no longer used. These identifiers are separate from the display name, **AE2 浅色界面适配 (AE2 Light UI Compat)**. The Java entry point is `local.ae2lightuicompat.AE2LightUICompat`.

## Build and development

Use JDK 17 and set `JAVA_HOME`. This is a standard ForgeGradle project; Python is not required. The Gradle Wrapper downloads the configured Gradle version, and the project includes Java toolchain provisioning. The first build requires internet access.

Windows:

```powershell
.\gradlew.bat build
```

Linux/macOS:

```sh
./gradlew build
```

The output is `build/libs/AE2-Light-UI-Compat-Forge-1.20.1-1.1.0.jar`.

Import the project into IntelliJ IDEA as a Gradle project. Useful development commands:

```powershell
.\gradlew.bat genIntellijRuns
.\gradlew.bat runClient
.\gradlew.bat verifyMixins
```

`runClient` includes Forge 47.4.0, AE2 15.4.11, and GuideME 20.1.15. Add the compatible resource pack to the development client's `run/resourcepacks` folder and enable it to inspect the themed buttons.

`verifyMixins` applies real Mixin transformations to Minecraft and AE2 development classes and checks the resulting bytecode with ASM. It runs as part of `build`. ForgeGradle remaps the release JAR, and the Mixin annotation processor generates its reference map.

For 1.0.2, the build and offline transformations against both development classes and production Forge 47.4.0 / AE2 15.4.11 classes passed. These checks do not replace in-game visual testing; the release verification did not launch Minecraft.

```text
├── build.gradle                 # Dependencies, ForgeGradle, Mixin and checks
├── settings.gradle
├── gradle.properties            # Minecraft, Forge and mod versions
├── gradlew / gradlew.bat
├── gradle/wrapper/
├── src/main/java/local/ae2lightuicompat/
├── src/main/resources/          # mods.toml, Mixin configuration, pack metadata
├── src/test/java/               # Offline Mixin verification
└── src/test/resources/          # Test service configuration
```

`build/`, `.gradle/`, and `run/` are generated locally and are not tracked. Earlier Python build helpers are retained in Git history.

## Development disclosure and license

The implementation, build configuration, and documentation were developed with substantial generative-AI assistance through OpenAI Codex. The project originated from user-defined requirements for a resource-pack backport. Automated checks are described above so users can distinguish them from in-game verification.

The companion code is licensed under [MIT](LICENSE). Third-party tools and resource-pack textures retain their own licenses; this repository's license does not grant redistribution rights to the separate texture pack. This is an independent companion project, not an official AE2 release.

Development references: [Forge 1.20.1 MDK](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html) and [SpongePowered's Forge Mixin guide](https://github.com/SpongePowered/Mixin/wiki/Mixins-on-Minecraft-Forge).
