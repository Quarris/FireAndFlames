package dev.quarris.fireandflames.world.crucible.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class EntityMeltingRecipeSerializer implements RecipeSerializer<EntityMeltingRecipe> {

    public static final MapCodec<EntityMeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        EntityTypePredicate.CODEC.fieldOf("entity_predicate").forGetter(EntityMeltingRecipe::entityPredicate),
        Codec.BOOL.optionalFieldOf("requires_fluid", false).forGetter(EntityMeltingRecipe::requiresFluid),
        IFluidOutput.CODEC.fieldOf("result").forGetter(EntityMeltingRecipe::result),
        Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(EntityMeltingRecipe::chance),
        Codec.INT.optionalFieldOf("heat", 800).forGetter(EntityMeltingRecipe::heat)
    ).apply(instance, EntityMeltingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityMeltingRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.fromCodecWithRegistries(EntityTypePredicate.CODEC), EntityMeltingRecipe::entityPredicate,
        ByteBufCodecs.BOOL, EntityMeltingRecipe::requiresFluid,
        IFluidOutput.STREAM_CODEC, EntityMeltingRecipe::result,
        ByteBufCodecs.FLOAT, EntityMeltingRecipe::chance,
        ByteBufCodecs.INT, EntityMeltingRecipe::heat,
        EntityMeltingRecipe::new
    );

    @Override
    public MapCodec<EntityMeltingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntityMeltingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
