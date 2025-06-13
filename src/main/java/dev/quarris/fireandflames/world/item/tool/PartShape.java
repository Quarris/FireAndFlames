package dev.quarris.fireandflames.world.item.tool;

import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.data.tool.material.IMaterialHolder;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PartShape extends Item implements IMaterialHolder {

    public PartShape(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        Holder<ToolMaterial> material = this.getMaterial(stack);
        String name = "You Shouldn't Have This";
        if (material != null) {
            name = material.value().name();
        }

        return Component.translatable(this.getDescriptionId(stack), name);
    }

    @Override
    public Holder<ToolMaterial> getMaterial(ItemStack stack) {
        return stack.get(DataComponentSetup.MATERIAL_HOLDER);
    }

    @Override
    public void setMaterial(ItemStack stack, Holder<ToolMaterial> material) {
        stack.set(DataComponentSetup.MATERIAL_HOLDER, material);
    }
}
