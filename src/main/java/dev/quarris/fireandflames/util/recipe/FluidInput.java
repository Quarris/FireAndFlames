package dev.quarris.fireandflames.util.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.List;

public record FluidInput(FluidIngredient ingredient, int amount) {

    public static final Codec<FluidInput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        FluidIngredient.MAP_CODEC_NONEMPTY.forGetter(FluidInput::ingredient),
        Codec.INT.fieldOf("count").forGetter(FluidInput::amount)
    ).apply(instance, FluidInput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidInput> STREAM_CODEC = StreamCodec.composite(
        FluidIngredient.STREAM_CODEC, FluidInput::ingredient,
        ByteBufCodecs.INT, FluidInput::amount,
        FluidInput::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidInput>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
        ByteBufCodecs.collection(NonNullList::createWithCapacity));

    public FluidInput withAmount(int amount) {
        return new FluidInput(this.ingredient, amount);
    }

    public FluidInput(FluidStack fluid) {
        this(FluidIngredient.single(fluid), fluid.getAmount());
    }

    public FluidInput(Fluid fluid, int amount) {
        this(FluidIngredient.single(fluid), amount);
    }

    public FluidInput(TagKey<Fluid> fluidTag, int amount) {
        this(FluidIngredient.tag(fluidTag), amount);
    }

    public boolean matchesAmount(FluidStack input) {
        return this.test(input) && input.getAmount() >= amount;
    }

    public boolean test(FluidStack input) {
        return this.ingredient.test(input);
    }
}
