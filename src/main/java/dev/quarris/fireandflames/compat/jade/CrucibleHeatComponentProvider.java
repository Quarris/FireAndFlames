package dev.quarris.fireandflames.compat.jade;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CrucibleHeatComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final ResourceLocation UID = ModRef.res("crucible_heat");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.literal("Burned Fuel: " + blockAccessor.getServerData().getInt("BurnTicks")));
        }
    }


    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof CrucibleControllerBlockEntity controller){
            compoundTag.putInt("BurnTicks", controller.getBurnTicks());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
