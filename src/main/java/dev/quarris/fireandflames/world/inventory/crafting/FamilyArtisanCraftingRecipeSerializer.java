package dev.quarris.fireandflames.world.inventory.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class FamilyArtisanCraftingRecipeSerializer implements RecipeSerializer<FamilyArtisanCraftingRecipe> {

    public static final MapCodec<FamilyArtisanCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("group").forGetter(FamilyArtisanCraftingRecipe::group),
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FamilyArtisanCraftingRecipe::ingredient),
        Codec.simpleMap(
            Codec.stringResolver(variant -> variant.name().toLowerCase(Locale.ROOT), variantName -> {
                for (BlockFamily.Variant variant : BlockFamily.Variant.values()) {
                    if (variant.name().equalsIgnoreCase(variantName)) {
                        return variant;
                    }
                }

                throw new IllegalArgumentException("Variant does not exist: " + variantName);
            }),
            FamilyArtisanCraftingRecipe.FamilyVariantOutput.CODEC,
            Keyable.forStrings(() -> Arrays.stream(BlockFamily.Variant.values()).map(variant -> variant.name().toLowerCase(Locale.ROOT)))
        ).fieldOf("variants").forGetter(FamilyArtisanCraftingRecipe::variants)
    ).apply(instance, FamilyArtisanCraftingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FamilyArtisanCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, FamilyArtisanCraftingRecipe::group,
        Ingredient.CONTENTS_STREAM_CODEC, FamilyArtisanCraftingRecipe::ingredient,
        ByteBufCodecs.map(HashMap::new, StreamCodec.of(
            (buffer, variant) -> buffer.writeUtf(variant.name().toLowerCase(Locale.ROOT)),
            buffer -> {
                String variantName = buffer.readUtf();
                for (BlockFamily.Variant variant : BlockFamily.Variant.values()) {
                    if (variant.name().equalsIgnoreCase(variantName)) {
                        return variant;
                    }
                }

                throw new IllegalArgumentException("Variant does not exist: " + variantName);
            }), FamilyArtisanCraftingRecipe.FamilyVariantOutput.STREAM_CODEC), FamilyArtisanCraftingRecipe::variants,
        FamilyArtisanCraftingRecipe::new
    );

    @Override
    public MapCodec<FamilyArtisanCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FamilyArtisanCraftingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
