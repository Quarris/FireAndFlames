package dev.quarris.fireandflames.compat.jade;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.world.block.entity.CastingBlockEntity;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.ui.Color;
import snownee.jade.api.view.*;
import snownee.jade.impl.ui.SimpleProgressStyle;
import snownee.jade.impl.ui.SlimProgressStyle;

import java.util.List;

public enum CastingProgressProvider implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, ProgressView> {
    INSTANCE;

    public static final ResourceLocation UID = ModRef.res("crucible_progress");

    @Override
    public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
        return ClientViewGroup.map(groups, (tag) -> {
            ProgressView progressView = new ProgressView(new SimpleProgressStyle());
            if (tag.contains("Progress")) {
                progressView.progress = tag.getFloat("Progress");
            }
            return progressView;
        }, null);
    }

    @Override
    public @Nullable List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
       BlockEntity blockEntity = (BlockEntity) accessor.getTarget();
        if (blockEntity instanceof CastingBlockEntity castingBlock && castingBlock.getRecipe() != null) {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("Progress", castingBlock.getCoolingTicks() / (float) castingBlock.getRecipe().value().coolingTime);
            return List.of(new ViewGroup<>(List.of(tag)));
        } else {
            return null;
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
