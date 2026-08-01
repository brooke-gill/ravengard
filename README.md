# Ravengard Crown Overlay

Client-only Fabric mod for Minecraft **26.2** that:

- Overlays Crown sell values on items in open GUIs
- Highlights profitable inventory↔container swaps (red = dump, teal = take) when your inventory is full
- Hover an item and press **K** to favourite/unfavourite it (favourites are never marked to swap out)

**Network policy:** passive HUD only. It never sends, intercepts, or modifies Minecraft/Hypixel packets; all data comes from already-synced client item lore and open GUI slots.

## Requirements

- Java **25+**
- Fabric Loader **0.19.3+**
- Fabric API **0.156.0+26.2** (or newer for 26.2)

## Prism Launcher setup

For the `26.2 Modded` instance this repo already:

1. Added **Fabric Loader 0.19.3** + Intermediary to `mmc-pack.json`
2. Dropped **Fabric API** `0.156.0+26.2` and **ravengard-1.0.0.jar** into `minecraft/mods/`

Launch once in Prism so it finishes downloading Fabric libraries. Instance Java is already 25.

To rebuild and refresh the mod jar:

```bash
export JAVA_HOME=/home/brooke/.local/jdk/jdk-25.0.4+7   # or your JDK 25
./gradlew build
cp build/libs/ravengard-1.0.0.jar \
  "$HOME/.var/app/org.prismlauncher.PrismLauncher/data/PrismLauncher/instances/26.2 Modded/minecraft/mods/"
```

## Build

```bash
export JAVA_HOME=/path/to/jdk-25
./gradlew build
```

## Usage

- Open any inventory/container: crown integers appear on items that have a Crowns lore line
- With a **full** 36-slot inventory and a chest/other GUI open: red/teal highlights show the greedy min-swap set that maximizes total crowns
- Hover an item and press **K** to favourite it (pink `*` mark); favourites are skipped as dump targets. Press again to unfavourite.
- Favourites are saved to `config/ravengard/favorites.json`
# ravengard
