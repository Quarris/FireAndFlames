package dev.quarris.fireandflames.util.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.data.config.number.ConstantNumber;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.List;

public record FluidInput(FluidIngredient ingredient, INumberProvider amount) {

    public static final Codec<FluidInput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        FluidIngredient.MAP_CODEC_NONEMPTY.forGetter(FluidInput::ingredient),
        INumberProvider.CODEC.fieldOf("amount").forGetter(FluidInput::amount)
    ).apply(instance, FluidInput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidInput> STREAM_CODEC = StreamCodec.composite(
        FluidIngredient.STREAM_CODEC, FluidInput::ingredient,
        INumberProvider.STREAM_CODEC, FluidInput::amount,
        FluidInput::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidInput>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
        ByteBufCodecs.collection(NonNullList::createWithCapacity));

    public FluidInput withAmount(INumberProvider amount) {
        return new FluidInput(this.ingredient, amount);
    }

    public FluidInput(FluidStack fluid) {
        this(FluidIngredient.single(fluid), fluid.getAmount());
    }

    public FluidInput(FluidIngredient ingredient, int amount) {
        this(ingredient, new ConstantNumber(amount));
    }

    public FluidInput(Fluid fluid, int amount) {
        this(FluidIngredient.single(fluid), new ConstantNumber(amount));
    }

    public FluidInput(TagKey<Fluid> fluidTag, int amount) {
        this(FluidIngredient.tag(fluidTag), new ConstantNumber(amount));
    }

    public FluidInput(Fluid fluid, INumberProvider amount) {
        this(FluidIngredient.single(fluid), amount);
    }

    public FluidInput(TagKey<Fluid> fluidTag, INumberProvider amount) {
        this(FluidIngredient.tag(fluidTag), amount);
    }

    public boolean matchesWithAmount(FluidStack input) {
        return this.test(input) && input.getAmount() >= this.amount.evaluateInt();
    }

    public boolean test(FluidStack input) {
        return this.ingredient.test(input);
    }
}
