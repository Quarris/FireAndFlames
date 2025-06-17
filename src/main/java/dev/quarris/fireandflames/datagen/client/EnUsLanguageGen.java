package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.part.CommonPartSlots;
import dev.quarris.fireandflames.setup.*;
import dev.quarris.fireandflames.util.fluid.CustomFluidHolder;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.function.Supplier;

public class EnUsLanguageGen extends LanguageProvider {

    public EnUsLanguageGen(PackOutput output) {
        super(output, ModRef.ID, "en_us");
    }

    private void addItems() {
        this.addItem(ItemSetup.FIRE_CLAY_BALL, "Fire Clay Ball");
        this.addItem(ItemSetup.FIRE_BRICK, "Fire Brick");
        this.addItem(ItemSetup.INGOT_CAST, "Ingot Cast");
        this.addItem(ItemSetup.NUGGET_CAST, "Nugget Cast");
        this.addItem(ItemSetup.BLADE_SHAPE_CAST, "Blade Shape Cast");
        this.addItem(ItemSetup.TOOL_SHAPE_CAST, "Tool Shape Cast");
        this.addItem(ItemSetup.MISC_SHAPE_CAST, "Misc Shape Cast");
    }

    private void addBlocks() {
        this.addBlock(BlockSetup.FIRE_CLAY, "Fire Clay");
        this.addBlock(BlockSetup.FIRE_BRICKS, "Fire Bricks");
        this.addBlock(BlockSetup.CRUCIBLE_CONTROLLER, "Crucible Controller");
        this.addBlock(BlockSetup.CRUCIBLE_WINDOW, "Crucible Window");
        this.addBlock(BlockSetup.CRUCIBLE_DRAIN, "Crucible Drain");
        this.addBlock(BlockSetup.CRUCIBLE_TANK, "Crucible Fuel Tank");
        this.addBlock(BlockSetup.CRUCIBLE_BURNER, "Crucible Fuel Burner");
        this.addBlock(BlockSetup.CRUCIBLE_FAWSIT, "Crucible Fawsit (Faucet)");
        this.addBlock(BlockSetup.CASTING_BASIN, "Casting Basin");
        this.addBlock(BlockSetup.CASTING_TABLE, "Casting Table");
        this.addBlock(BlockSetup.TINKERS_WORKBENCH, "Tinkers' Workbench");
        this.addBlock(BlockSetup.SMITHING_ANVIL, "Smithing Anvil");
        this.addBlock(BlockSetup.ARTISAN_TABLE, "Artisan Table");
    }

    private void addToolsAndParts() {
        // Tools
        this.addItem(ToolItemSetup.PICKAXE, "%s Pickaxe");
        this.addItem(ToolItemSetup.AXE, "%s Axe");
        this.addItem(ToolItemSetup.SHOVEL, "%s Shovel");
        this.addItem(ToolItemSetup.HOE, "%s Hoe");
        this.addItem(ToolItemSetup.HAMMER, "%s Hammer");
        this.addItem(ToolItemSetup.SWORD, "%s Sword");

        // Parts
        this.add(PartTypeSetup.PICKAXE_HEAD.getKey().location().toLanguageKey("item"), "%s Pickaxe Head");
        this.add(PartTypeSetup.AXE_HEAD.getKey().location().toLanguageKey("item"), "%s Axe Head");
        this.add(PartTypeSetup.SHOVEL_HEAD.getKey().location().toLanguageKey("item"), "%s Shovel Head");
        this.add(PartTypeSetup.HOE_HEAD.getKey().location().toLanguageKey("item"), "%s Hoe Head");
        this.add(PartTypeSetup.SWORD_BLADE.getKey().location().toLanguageKey("item"), "%s Sword Blade");
        this.add(PartTypeSetup.HANDLE.getKey().location().toLanguageKey("item"), "%s Handle");
        this.add(PartTypeSetup.BINDING.getKey().location().toLanguageKey("item"), "%s Tool Binding");
        this.add(PartTypeSetup.WIDE_GUARD.getKey().location().toLanguageKey("item"), "%s Wide Guard");

        // Shapes
        this.addItem(ToolItemSetup.TOOL_SHAPE, "%s Tool Shape");
        this.addItem(ToolItemSetup.BLADE_SHAPE, "%s Blade Shape");
        this.addItem(ToolItemSetup.MISC_SHAPE, "%s Misc Shape");
    }

    private void addFluids() {
        this.addFluidHolder(FluidSetup.MOLTEN_IRON, "Molten Iron");
        this.addFluidHolder(FluidSetup.MOLTEN_GOLD, "Molten Gold");
        this.addFluidHolder(FluidSetup.MOLTEN_COPPER, "Molten Copper");
        this.addFluidHolder(FluidSetup.MOLTEN_ANCIENT_DEBRIS, "Molten Ancient Debris");
        this.addFluidHolder(FluidSetup.MOLTEN_NETHERITE, "Molten Netherite");
    }

    private void addGui() {
        this.add("container.fireandflames.crucible.title", "Crucible");
        this.add("container.fireandflames.crucible_burner.title", "Crucible Fuel Burner");
        this.add("container.fireandflames.tinkers_workbench.title", "Tinker's Workbench");
        this.add("container.fireandflames.artisan_table.title", "Artisan Table");
        this.add("container.fireandflames.smithing_anvil.title", "Smithing Anvil");
        this.add("container.fireandflames.crucible.fluid_tank.empty", "Empty");
        this.add("container.fireandflames.fluid_storage.fluid_amount", "%s - %s mb");
        this.add("container.fireandflames.fluid_storage.more", "and %s more...");

        this.add("creative_tabs.fireandflames.main", "Fire and Flames");
        this.add("creative_tabs.fireandflames.tools", "F&F - Parts");
    }

    private void addItemDescriptions() {
        this.add("block.fireandflames.casting_basin.description", "Forms blocks");
        this.add("block.fireandflames.casting_table.description", "Forms items");
        this.add("block.fireandflames.crucible_controller.description", "Heart of the Crucible");
        this.add("block.fireandflames.crucible_burner.description", "Provider solid fuel for Crucible");
        this.add("block.fireandflames.crucible_tank.description", "Provider fluid fuel for Crucible");
        this.add("block.fireandflames.crucible_drain.description", "Allows interaction with fluids in the Crucible");
        this.add("block.fireandflames.crucible_faucet.description", "Extracts fluids into tanks below");
    }

    private void addMisc() {
        this.add("death.attack.crucible_melting", "%1$s was melted by the heat of the crucible");
        this.add("death.attack.crucible_melting.player", "%1$s was thrown to the pits of the crucible by %2$s");

        this.addPartSlot(CommonPartSlots.PICKAXE_HEAD.name(), "Pickaxe Head");
        this.addPartSlot(CommonPartSlots.AXE_HEAD.name(), "Axe Head");
        this.addPartSlot(CommonPartSlots.SHOVEL_HEAD.name(), "Shovel Head");
        this.addPartSlot(CommonPartSlots.HOE_HEAD.name(), "Hoe Head");
        this.addPartSlot(CommonPartSlots.HAMMER_LEFT.name(), "Hammer Head");
        this.addPartSlot(CommonPartSlots.HAMMER_RIGHT.name(), "Hammer Head");
        this.addPartSlot(CommonPartSlots.SWORD_BLADE.name(), "Sword Blade");

        this.addPartSlot(CommonPartSlots.BINDING.name(), "Binding");
        this.addPartSlot(CommonPartSlots.HANDLE.name(), "Handle");
        this.addPartSlot(CommonPartSlots.GRIP.name(), "Grip");
        this.addPartSlot(CommonPartSlots.WIDE_GUARD.name(), "Wide Guard");
    }

    private void addCompat() {
        // JEI
        this.add("gui.fireandflames.jei.category.crucible", "Crucible");
        this.add("gui.fireandflames.jei.category.alloying", "Alloying");
        this.add("gui.fireandflames.jei.category.basin_casting", "Basin Casting");
        this.add("gui.fireandflames.jei.category.table_casting", "Table Casting");
        this.add("gui.fireandflames.jei.category.entity_melting", "Entity Melting");
        this.add("gui.fireandflames.jei.category.artisan_table", "Artisan Crafting");
        this.add("gui.fireandflames.jei.cast_consumed", "Consumes Cast!");

        // Jade
        this.add("config.jade.plugin_fireandflames.crucible_heat", "Crucible Heat");
    }

    @Override
    protected void addTranslations() {
        this.addItems();
        this.addBlocks();
        this.addToolsAndParts();
        this.addFluids();
        this.addGui();
        this.addItemDescriptions();
        this.addMisc();
        this.addCompat();
    }

    private void addPartSlot(String partSlotId, String name) {
        this.add(Util.makeDescriptionId("tool_part.slot", ModRef.res(partSlotId)), name);
    }

    private void addFluid(Supplier<? extends FluidType> fluid, String name) {
        this.add(fluid.get().getDescriptionId(), name);
    }

    private void add(FluidType fluid, String name) {
        this.add(fluid.getDescriptionId(), name);
    }

    private void addFluidHolder(CustomFluidHolder fluidHolder, String name) {
        this.addBlock(fluidHolder.getLiquidBlock(), name);
        this.addFluid(fluidHolder.getFluidType(), name);
        this.addItem(fluidHolder.getBucket(), name + " Bucket");
    }
}
