package dev.quarris.fireandflames.world.inventory.crafting;

import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.data.tool.material.IMaterialHolder;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.setup.RegistrySetup;
import dev.quarris.fireandflames.util.data.DataMapUtil;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
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
        List<ArtisanRecipeOutput> outputs = new ArrayList<>();
        DataMapUtil.getConverters(input.item(), registries).stream()
            .filter(data -> input.item().getCount() >= data.converter().getCountForUnits(this.units.evaluateInt()))
            .forEach(data -> {
                Holder<ToolMaterial> mat = data.material();

                ItemStack mainOutput = this.result.createItemStack();
                if (mainOutput.getItem() instanceof IMaterialHolder materialHolder) {
                    materialHolder.setMaterial(mainOutput, mat);
                }

                outputs.add(new ArtisanRecipeOutput(data.converter().getCountForUnits(this.units.evaluateInt()), mainOutput, ItemStack.EMPTY));
            });

        return outputs;
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
