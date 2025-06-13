package dev.quarris.fireandflames.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class AdditionalCodecs {

    public static final Codec<ItemStack> OPTIONAL_SINGLE_ITEM_CODEC = ExtraCodecs.optionalEmptyMap(ItemStack.SINGLE_ITEM_CODEC)
        .xmap(optional -> optional.orElse(ItemStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));

}
