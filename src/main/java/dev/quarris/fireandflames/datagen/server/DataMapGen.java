package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.data.maps.FuelData;
import dev.quarris.fireandflames.setup.DataMapSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;

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
    }
}
