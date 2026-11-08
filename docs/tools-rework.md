# Tools rework

Design notes and plan for the tools rework on `feature/tools-ench`.

## Gameplay chain

Casting Table pours metal into a material shape → Smithing Anvil hammers the shape into a part →
Tinkers Workbench (one tab per tool type) assembles parts into a tool.

The Artisan Table (stonecutter-style station) is a separate feature and not part of this rework.

## Stats (done)

Materials provide the stats; each part takes the stats it controls.

- Stats: durability, mining tier, mining speed, attack damage, attack speed (`ToolStat`).
- Every material has one value per stat (`ToolMaterial`). There are no bonus fields.
  - durability: multiplied by the tool type's durability modifier
  - mining tier: the `denies_blocks` tag
  - mining speed: multiplies the tool type's rule speeds
  - attack damage / attack speed: added to the tool type's base values
- Each part type lists the stats it controls (`PartTypeSetup`, one line per part type).
  Which part controls what is **not final**. Current placeholder:
  - pickaxe/axe/shovel/hoe head, sword blade: mining tier, mining speed, attack damage
  - handle (also used as the grip): durability, attack speed
  - binding, wide guard: nothing (will carry traits later)
- A stat controlled by several parts on one tool is a plain average (e.g. handle + grip).
  The mining tier can't be averaged, so it comes from the main part if it controls the tier, otherwise the lowest-index slot.
  A stat no part controls falls back to the main part.
- Worked out in `ResolvedToolStats`, used both by `ICustomTool.onItemLoad` and the tool tooltip,
  which shows each part's material and which part(s) each stat comes from.

Known effect of the placeholder: durability comes only from the handle/grip, so an iron pickaxe with wood handles has wood durability (59).

## Part enchantments (next)

- A part holds at most one enchantment.
- Ways to enchant a part:
  - Smithing Anvil: putting an enchanted book in while shaping a part chooses which enchantment goes on.
  - Enchanting table: enchant the part normally to get a random enchantment (only one is kept).
  - Vanilla anvil: the first enchantment on the book gets attached.
  - A part shape can be enchanted the same ways; it keeps its enchantment when it's hammered into a part.
- A part accepts any enchantment that suits at least one tool the part can go into.
  When the tool is built, enchantments that don't suit that tool are dropped (warn in the workbench preview).
- Stacking across parts: an enchantment of level L is worth 2^L points; the tool gets the largest level whose 2^L fits in the total.
  - Unbreaking I + I → II
  - Fortune III + III → IV, Fortune III ×4 → V
  - Fortune III + II + II → IV, Fortune III + II → III
  - With 3–5 parts this caps at two levels above the best part.
- Incompatible enchantments on different parts (Fortune/Silk Touch, Sharpness/Smite):
  the one attached last takes precedence. E.g. a Silk Touch tool whose part is replaced with a Fortune part gets Fortune.
  This needs the attach order stored on the tool.
- Finished tools cannot be enchanted at all. To change a tool, replace a part and/or attach modifiers.

## Remaining plan

1. ~~Stat model, remove the debug `MiningSpeedEffect(2, 2)`, fix obsidian/netherite tiers.~~ Done.
2. Part enchantments (above).
3. Modifiers and traits as a datapack registry (synced, Holder codecs). Tools store only references and levels.
   Remove `ToolModifierManager`, `CBUpdateToolModifiers` and the empty `TOOL_MODIFIERS` registry.
   - Traits come from materials and are fixed. Modifiers are player-added and slot-limited. Both are lists of (definition, level).
   - Stat effects: pure functions applied in the `onItemLoad` rebuild.
   - Behaviour effects: a small code-registered interface (`onHit`, `onBlockBroken`, `onInventoryTick`) called from one event handler.
4. Rendering: keep the greyscale per-part layers, add per-material palettes via the vanilla `paletted_permutations` atlas source
   (like armour trims) with the current single-colour tint as fallback. Use an `ItemOverrides`-based model that picks sprites per
   (part, material) and caches combined models. Delete the unused `ToolItemRenderer` / `ToolRenderer`.
5. Behaviour effects: hammer area mining. Then traits on materials.

## Other known issues

- `TinkersWorkbenchMenu`: `quickMoveStack` is a TODO (no shift-click) and `stillValid` always returns true.
- `ToolType` ordering is 1 for most types; slot positions and ordering live in `ToolType` (author's own TODO).
- Hardcoded English strings remain (e.g. "You Shouldn't Have This"), and material names are plain strings, not translation keys.
- Five near-identical tool item classes extend vanilla tool classes with a dummy `MaterialTier`.
