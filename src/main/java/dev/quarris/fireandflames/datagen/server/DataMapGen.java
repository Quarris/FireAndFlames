package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.data.config.number.ConfigNumber;
import dev.quarris.fireandflames.data.map.FuelData;
import dev.quarris.fireandflames.data.map.FluidMaterialConverter;
import dev.quarris.fireandflames.data.map.ItemMaterialConverter;
import dev.quarris.fireandflames.data.map.MaterialConversion;
import dev.quarris.fireandflames.setup.DataMapSetup;
import dev.quarris.fireandflames.setup.MaterialSetup;
import dev.quarris.fireandflames.setup.TagSetup;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataMapGen extends DataMapProvider {

    public DataMapGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        this.builder(DataMapSetup.FLUID_FUEL_DATA)
            .add(Tags.Fluids.LAVA, new FuelData(1300, 2000), false);

        this.builder(DataMapSetup.ITEM_FUEL_DATA)
            .add(Tags.Items.NETHER_STARS, new FuelData(10000, 2000000), false);

        this.builder(DataMapSetup.MATERIAL_CONVERSIONS)
            .add(MaterialSetup.COPPER, new MaterialConversion(List.of(
                new ItemMaterialConverter(new ItemInput(Tags.Items.INGOTS_COPPER), 1),
                new ItemMaterialConverter(new ItemInput(Tags.Items.STORAGE_BLOCKS_COPPER), 9),
                new FluidMaterialConverter(new FluidInput(TagSetup.FluidTags.MOLTEN_COPPER, ConfigNumber.ConfigValue.INGOT_MB.toProvider()), 1)
            )), false)
            .add(MaterialSetup.IRON, new MaterialConversion(List.of(
                new ItemMaterialConverter(new ItemInput(Tags.Items.INGOTS_IRON), 1),
                new ItemMaterialConverter(new ItemInput(Tags.Items.STORAGE_BLOCKS_IRON), 9),
                new ItemMaterialConverter(new ItemInput(Tags.Items.NUGGETS_IRON, 9), 1),
                new FluidMaterialConverter(new FluidInput(TagSetup.FluidTags.MOLTEN_IRON, ConfigNumber.ConfigValue.INGOT_MB.toProvider()), 1)
            )), false)
            .add(MaterialSetup.GOLD, new MaterialConversion(List.of(
                new ItemMaterialConverter(new ItemInput(Tags.Items.INGOTS_GOLD), 1),
                new ItemMaterialConverter(new ItemInput(Tags.Items.STORAGE_BLOCKS_GOLD), 9),
                new ItemMaterialConverter(new ItemInput(Tags.Items.NUGGETS_GOLD, 9), 1),
                new FluidMaterialConverter(new FluidInput(TagSetup.FluidTags.MOLTEN_GOLD, ConfigNumber.ConfigValue.INGOT_MB.toProvider()), 1)
            )), false)
            .add(MaterialSetup.NETHERITE, new MaterialConversion(List.of(
                new ItemMaterialConverter(new ItemInput(Tags.Items.INGOTS_NETHERITE), 1),
                new ItemMaterialConverter(new ItemInput(Tags.Items.STORAGE_BLOCKS_NETHERITE), 9),
                new FluidMaterialConverter(new FluidInput(TagSetup.FluidTags.MOLTEN_NETHERITE, ConfigNumber.ConfigValue.INGOT_MB.toProvider()), 1)
            )), false)
            .add(MaterialSetup.WOOD, new MaterialConversion(List.of(
                new ItemMaterialConverter(new ItemInput(ItemTags.PLANKS, 2), 1),
                new ItemMaterialConverter(new ItemInput(ItemTags.LOGS), 2),
                new ItemMaterialConverter(new ItemInput(Tags.Items.RODS_WOODEN, 4), 1)
            )), false)
            .add(MaterialSetup.FLINT, new MaterialConversion(List.of(
                new ItemMaterialConverter(new ItemInput(Items.FLINT), 1)
            )), false)
            .add(MaterialSetup.OBSIDIAN, new MaterialConversion(List.of(
                new ItemMaterialConverter(new ItemInput(Tags.Items.OBSIDIANS), 1)
            )), false)
            .add(MaterialSetup.STONE, new MaterialConversion(List.of(
                new ItemMaterialConverter(new ItemInput(Tags.Items.STONES), 1),
                new ItemMaterialConverter(new ItemInput(Tags.Items.COBBLESTONES), 1)
            )), false);
    }
}
