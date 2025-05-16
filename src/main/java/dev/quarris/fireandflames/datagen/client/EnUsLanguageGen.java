package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.*;
import dev.quarris.fireandflames.util.fluid.CustomFluidHolder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.function.Supplier;

public class EnUsLanguageGen extends LanguageProvider {

    public EnUsLanguageGen(PackOutput output) {
        super(output, ModRef.ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.addItem(ItemSetup.FIRE_CLAY_BALL, "Fire Clay Ball");
        this.addItem(ItemSetup.FIRE_BRICK, "Fire Brick");
        this.addItem(ItemSetup.INGOT_CAST, "Ingot Cast");
        this.addItem(ItemSetup.NUGGET_CAST, "Nugget Cast");

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

        this.add(TagSetup.FluidTags.MOLTEN_IRON, "Molten Iron");

        this.addFluidHolder(FluidSetup.MOLTEN_IRON, "Molten Iron");
        this.addFluidHolder(FluidSetup.MOLTEN_GOLD, "Molten Gold");
        this.addFluidHolder(FluidSetup.MOLTEN_COPPER, "Molten Copper");
        this.addFluidHolder(FluidSetup.MOLTEN_ANCIENT_DEBRIS, "Molten Ancient Debris");
        this.addFluidHolder(FluidSetup.MOLTEN_NETHERITE, "Molten Netherite");

        this.add("container.fireandflames.crucible.title", "Crucible");
        this.add("container.fireandflames.crucible_burner.title", "Crucible Fuel Burner");
        this.add("container.fireandflames.crucible.fluid_tank.empty", "Empty");
        this.add("container.fireandflames.fluid_storage.fluid_amount", "%s - %s mb");
        this.add("container.fireandflames.fluid_storage.more", "and %s more...");
        this.add("creative_tabs.fireandflames.creative_tab", "Fire and Flames");
        this.add("death.attack.crucible_melting", "%1$s was melted by the heat of the crucible");
        this.add("death.attack.crucible_melting.player", "%1$s was thrown to the pits of the crucible by %2$s");

        // JEI
        this.add("gui.fireandflames.jei.category.crucible", "Crucible");
        this.add("gui.fireandflames.jei.category.alloying", "Alloying");

        // Jade
        this.add("config.jade.plugin_fireandflames.crucible_heat", "Crucible Heat");
    }

    private void addFluid(Supplier<? extends FluidType> fluid, String name) {
        this.add(fluid.get().getDescriptionId(), name);
    }

    private void add(FluidType fluid, String name) {
        this.add(fluid.getDescriptionId(), name);
    }

    private void addFluidHolder(CustomFluidHolder fluidHolder, String name) {
        this.addFluid(fluidHolder.getFluidType(), name);
        this.addItem(fluidHolder.getBucket(), name + " Bucket");
    }
}
