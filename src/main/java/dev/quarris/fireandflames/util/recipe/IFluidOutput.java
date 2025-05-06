package dev.quarris.fireandflames.util.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Function;

public interface IFluidOutput {

    Codec<IFluidOutput> CODEC = Codec.xor(Stack.CODEC, Tag.CODEC)
        .xmap(either -> either.map(Function.identity(), Function.identity()),
            output -> {
                if (output instanceof Stack stack) {
                    return Either.left(stack);
                }

                if (output instanceof Tag tag) {
                    return Either.right(tag);
                }

                throw new UnsupportedOperationException("Fluid output is neither Direct nor Tag");
            });

    StreamCodec<RegistryFriendlyByteBuf, IFluidOutput> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    StreamCodec<RegistryFriendlyByteBuf, List<IFluidOutput>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
        ByteBufCodecs.collection(NonNullList::createWithCapacity));

    IFluidOutput withAmount(int amount);

    FluidStack createFluid();

    record Stack(FluidStack stack) implements IFluidOutput {

        public static final Codec<Stack> CODEC = FluidStack.CODEC.xmap(Stack::new, Stack::stack);

        public Stack(Fluid fluid, int amount) {
            this(new FluidStack(fluid, amount));
        }

        @Override
        public IFluidOutput withAmount(int amount) {
            return new Stack(this.stack.copyWithAmount(amount));
        }

        @Override
        public FluidStack createFluid() {
            return this.stack.copy();
        }
    }

    record Tag(TagKey<Fluid> tag, int amount) implements IFluidOutput {

        public static final Codec<Tag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(Tag::tag),
            Codec.INT.fieldOf("amount").forGetter(Tag::amount)
        ).apply(instance, Tag::new));

        @Override
        public IFluidOutput withAmount(int amount) {
            return new Tag(this.tag, amount);
        }

        @Override
        public FluidStack createFluid() {
            return BuiltInRegistries.FLUID.getTag(this.tag).map(tags -> new FluidStack(tags.get(0), this.amount)).orElseThrow(() -> new IllegalArgumentException("Could not create fluid from tag " + this.tag));
        }
    }
}
