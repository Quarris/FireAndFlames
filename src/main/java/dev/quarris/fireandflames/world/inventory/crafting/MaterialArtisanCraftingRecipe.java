package dev.quarris.fireandflames.world.inventory.crafting;

import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.data.map.ConverterData;
import dev.quarris.fireandflames.data.map.ItemMaterialConverter;
import dev.quarris.fireandflames.data.tool.material.IMaterialHolder;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.setup.RegistrySetup;
import dev.quarris.fireandflames.util.data.DataMapUtil;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record MaterialArtisanCraftingRecipe(
    INumberProvider units,
    IItemOutput result
) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return DataMapUtil.getConverters(input.item(), level.registryAccess()).stream()
            .anyMatch(data -> input.item().getCount() >= data.converter().getCountForUnits(this.units.evaluateInt()));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return this.result.createItemStack();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result.createItemStack();
    }

    public List<ArtisanRecipeOutput> createResults(SingleRecipeInput input, HolderLookup.Provider registries) {
        List<ConverterData> converters = DataMapUtil.getConverters(input.item(), registries);
        return this.createResultsFromConverters(input.item().getCount(), DataMapUtil.getAllConverters(registries), converters);
    }

    public List<ArtisanRecipeOutput> createResultsFromConverters(int inputCount, List<ConverterData> byproductConverters, List<ConverterData> inputConverters) {
        List<ArtisanRecipeOutput> outputs = new ArrayList<>();

        inputConverters.stream()
            .filter(data -> data.converter() instanceof ItemMaterialConverter)
            .filter(data -> inputCount >= data.converter().getCountForUnits(this.units.evaluateInt()))
            .forEach(data -> {
                Holder<ToolMaterial> mat = data.material();

                ItemStack mainOutput = this.result.createItemStack();
                if (mainOutput.getItem() instanceof IMaterialHolder materialHolder) {
                    materialHolder.setMaterial(mainOutput, mat);
                }

                int requiredInputCount = data.converter().getCountForUnits(this.units.evaluateInt());
                int totalUnitsFromInput = data.converter().getUnits();
                int usedUnits = this.units.evaluateInt();
                int leftoverUnits = totalUnitsFromInput - usedUnits;

                ItemStack byproduct = calculateBestByproduct(leftoverUnits, mat, byproductConverters.stream().filter(bcData -> bcData.material().equals(mat)).toList());

                outputs.add(new ArtisanRecipeOutput(requiredInputCount, mainOutput, byproduct));
            });

        return outputs;
    }

    private static ItemStack calculateBestByproduct(int leftoverUnits, Holder<ToolMaterial> material, List<ConverterData> converters) {
        if (leftoverUnits <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack bestByproduct = ItemStack.EMPTY;
        int bestRemainder = Integer.MAX_VALUE;
        int bestOutputCount = Integer.MAX_VALUE;

        for (ConverterData converterData : converters) {
            // Only consider converters for the same material
            if (!converterData.material().equals(material)) {
                continue;
            }

            // Only consider ItemMaterialConverters
            if (!(converterData.converter() instanceof ItemMaterialConverter itemConverter)) {
                continue;
            }

            int converterUnits = itemConverter.getUnits();

            // Check if we have enough leftover units for at least one conversion
            if (converterUnits <= leftoverUnits) {
                int outputCount = leftoverUnits / converterUnits;
                int remainder = leftoverUnits % converterUnits;

                // Check if this is better (lower remainder, or same remainder but lower output count)
                if (remainder < bestRemainder || (remainder == bestRemainder && outputCount < bestOutputCount)) {
                    // Create the output item from this converter
                    ItemStack outputItem = itemConverter.item().ingredient().getItems()[0].copyWithCount(outputCount);

                    bestByproduct = outputItem;
                    bestRemainder = remainder;
                    bestOutputCount = outputCount;
                }
            }
        }

        return bestByproduct;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.MATERIAL_ARTISAN_CRAFTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.MATERIAL_ARTISAN_CRAFTING_TYPE.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }
}
