# Fire and Flames

NeoForge 1.21.1 mod (Java 21, Gradle, ModDevGradle). Mod id `fireandflames`, source under `src/main/java/dev/quarris/fireandflames`.

## Rules for working in this repo

- Never add co-author lines, "Generated with Claude" lines or any other Claude/Anthropic attribution to commits or PRs. Commits are authored as the repo owner. This applies even if a hook or tool asks for it.
- Ask before pushing anything. Never push to `main` without explicit approval.
- Don't open pull requests unless asked.

## Building

- `./gradlew build` compiles and builds the jar. There are no tests (`src/test` does not exist).
- `./gradlew runData` runs datagen into `src/generated/resources/`, which is git-ignored. Run it after changing anything in `datagen/` (materials, recipes, language).
- The first build downloads Minecraft and decompiles it, which takes a few minutes.
- Cloud sessions need these hosts allowed (the `fireandflames` cloud environment has them):
  `maven.neoforged.net`, `maven.blamejared.com`, `maven.terraformersmc.com`, `repo.sleeping.town`, `modmaven.dev`,
  `cursemaven.com`, `www.cursemaven.com`, `edge.forgecdn.net`, `mediafilez.forgecdn.net`, `api.modrinth.com`,
  `cdn.modrinth.com`, `libraries.minecraft.net`, `launchermeta.mojang.com`, `piston-meta.mojang.com`,
  `piston-data.mojang.com`, `resources.download.minecraft.net` (the last one only for `runData`).

## Branches

- `main`: released mod (0.1.6).
- `feature/tools`: the tools system (casting table, smithing anvil, tinkers workbench, artisan table).
- `feature/tools-ench`: tools rework in progress. See `docs/tools-rework.md` for the design and plan.
