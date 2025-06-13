package dev.quarris.fireandflames.data.tool.material;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IMaterialHolder {

    Holder<ToolMaterial> getMaterial(ItemStack stack);

    void setMaterial(ItemStack stack, Holder<ToolMaterial> material);

    default boolean hasMaterial(ItemStack stack) {
        return this.getMaterial(stack) == null;
    }

    default ItemStack createFrom(Holder<ToolMaterial> material) {
        ItemStack stack = new ItemStack((Item) this);
        this.setMaterial(stack, material);
        return stack;
    }
}
