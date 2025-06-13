package dev.quarris.fireandflames.world.inventory.crafting;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ArtisanRecipeOutput(int requiredCount, ItemStack main, ItemStack byproduct) {

    public static final StreamCodec<RegistryFriendlyByteBuf, ArtisanRecipeOutput> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ArtisanRecipeOutput::requiredCount,
        ItemStack.STREAM_CODEC, ArtisanRecipeOutput::main,
        ItemStack.OPTIONAL_STREAM_CODEC, ArtisanRecipeOutput::byproduct,
        ArtisanRecipeOutput::new
    );

}
