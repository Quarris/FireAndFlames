package dev.quarris.fireandflames.compat.jade;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.config.ServerConfigs;
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

import java.util.List;

public enum CrucibleProgressProvider implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, ProgressView> {
    INSTANCE;

    public static final ResourceLocation UID = ModRef.res("crucible_progress");

    @Override
    public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
        return ClientViewGroup.map(groups, (tag) -> {
            int defaultHeat = ServerConfigs.getBaseTemperature();
            int heat = tag.getInt("Heat");
            int clampedHeat = Math.clamp(heat, defaultHeat - 800, defaultHeat + 1600);
            float progress = (clampedHeat - (defaultHeat - 800)) / 2400F;
            int color = Color.rgb(Mth.lerpInt(progress, 100, 255), Mth.lerpInt(progress, 50, 100), 0).toInt();
            ProgressView progressView = new ProgressView(new SimpleProgressStyle().color(color));
            progressView.progress = progress;
            return progressView;
        }, null);
    }

    @Override
    public @Nullable List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
       BlockEntity blockEntity = (BlockEntity) accessor.getTarget();
        if (blockEntity instanceof CrucibleControllerBlockEntity crucible) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Heat", crucible.getHeat());
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
