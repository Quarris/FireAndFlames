package dev.quarris.fireandflames.world.inventory.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SmithingAnvilRecipeSerializer implements RecipeSerializer<SmithingAnvilRecipe> {
    public static final MapCodec<SmithingAnvilRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.optionalFieldOf("group", "").forGetter(SmithingAnvilRecipe::group),
        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SmithingAnvilRecipe::input),
        IItemOutput.CODEC.listOf().fieldOf("possible_outputs").forGetter(SmithingAnvilRecipe::possibleOutputs)
    ).apply(instance, SmithingAnvilRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SmithingAnvilRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, SmithingAnvilRecipe::group,
        Ingredient.CONTENTS_STREAM_CODEC, SmithingAnvilRecipe::input,
        IItemOutput.LIST_STREAM_CODEC, SmithingAnvilRecipe::possibleOutputs,
        SmithingAnvilRecipe::new
    );

    @Override
    public MapCodec<SmithingAnvilRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SmithingAnvilRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
