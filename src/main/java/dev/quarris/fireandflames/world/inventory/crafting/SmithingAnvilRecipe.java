package dev.quarris.fireandflames.world.inventory.crafting;

import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.data.tool.material.IMaterialHolder;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public record SmithingAnvilRecipe(
    String group,
    Ingredient input,
    List<IItemOutput> possibleOutputs
) implements Recipe<SmithingAnvilRecipe.Input> {

    @Override
    public boolean matches(Input input, Level level) {
        return this.input.test(input.stack);
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        if (input.selectedItem == -1) return ItemStack.EMPTY;
        if (input.selectedItem >= this.possibleOutputs.size()) return ItemStack.EMPTY;

        ItemStack inputStack = input.stack;
        ItemStack outputStack = this.possibleOutputs.get(input.selectedItem).createItemStack();

        if (inputStack.getItem() instanceof IMaterialHolder inputHolder && outputStack.getItem() instanceof IMaterialHolder outputHolder) {
            outputHolder.setMaterial(outputStack, inputHolder.getMaterial(inputStack));
        }

        return outputStack;
    }

    public List<ItemStack> createOutputs(Holder<ToolMaterial> material) {
        return this.possibleOutputs.stream()
            .map(IItemOutput::createItemStack)
            .map(stack -> {
                if (material != null && stack.getItem() instanceof IMaterialHolder materialHolder) {
                    materialHolder.setMaterial(stack, material);
                }
                return stack;
            })
            .toList();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.possibleOutputs.get(0).createItemStack();
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.SMITHING_ANVIL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.SMITHING_ANVIL_TYPE.get();
    }

    public record Input(ItemStack stack, int selectedItem) implements RecipeInput {

        public Input(ItemStack stack) {
            this(stack, -1);
        }

        @Override
        public ItemStack getItem(int index) {
            if (index < 0 || index > 1) {
                throw new IllegalArgumentException("Smithing Recipe Input can only have a single stack. Tried access " + index);
            }

            return this.stack;
        }

        @Override
        public int size() {
            return 1;
        }
    }
}
