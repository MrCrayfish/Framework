# Changelog

## [0.13.22 → 0.14.1] MC 26.2 upgrade

Upgraded from Minecraft 26.1.2 to 26.2 (Fabric + NeoForge). Confirmed working in-game (verified via [[Backpacked]], which depends on this).

- Bumped `minecraft_version` 26.1.2 → 26.2, `neo_form_version` → 26.2-1, `fabric_version` → 0.152.1+26.2, `fabric_loader_version` → 0.19.3, `neoforge_version` → 26.2.0.6-beta, version ranges updated accordingly.
- Bumped Fabric Loom plugin 1.16-SNAPSHOT → 1.17.11, Gradle wrapper 9.4.1 → 9.5.1. `net.neoforged.moddev` plugin stays at 2.0.141 (already 26.2-compatible).
- Removed `includeInternal` from the fabric loader-attribute configuration loop — Loom 1.17 no longer creates that configuration.
- `StandaloneModelRenderer`: MC 26.2 removed `MultiBufferSource` entirely (replaced by the deferred `SubmitNodeCollector` rendering pipeline). The three `draw(...)` overloads that took a `MultiBufferSource` were converted to `submitDraw(...)` overloads taking a `SubmitNodeCollector`; the duplicate `BlockStateModelPart` overload was removed since an equivalent `submitDraw` already existed.
- `Sheets.translucentBlockSheet()`/`cutoutBlockSheet()` renamed to `translucentBlockItemSheet()`/`cutoutBlockItemSheet()`.
- `Minecraft#screen` field and `Minecraft#setScreen` moved to the new `Minecraft.gui` (`Gui#screen()` / `Gui#setScreen`). Updated `Overlayable`, `Overlay`, and the two `WidgetsTestClient` testmod files (now call `setScreenAndShow`).
- `BlockPos#getCenter()` removed; replaced with `Vec3.atCenterOf(pos)` in `LevelLocation`.
- `SyncedClassKey`: `MagmaCube`/`Slime` moved from `net.minecraft.world.entity.monster` to `net.minecraft.world.entity.monster.cubemob`.
- `client.GameRendererMixin`'s `@WrapOperation` on `extractGui` crashed at launch — MC 26.2 removed `GameRenderer#extractGui` and moved the screen-extraction call (`Screen#extractRenderStateWithTooltipAndSubtitles`) into `Gui#extractRenderState`. Moved the `onRenderScreen` `@WrapOperation` out of `GameRendererMixin` into `FabricGuiMixin` (already targeting `Gui.class`), retargeted at `extractRenderState`. (Originally landed as a separate `FabricGuiMixin` split-out in an earlier attempt; consolidated here since the end state is what matters.)
- `multiloader-common.gradle`: made the `signing { sign publishing.publications.mavenJava }` call conditional on `SIGNING_KEY` being set — it was unconditionally registering a sign task that failed `publishToMavenLocal` when no key is configured (needed for local dependency testing against [[Backpacked]]).

**Not yet done:** Forge subproject (currently excluded from `settings.gradle`, untouched).
