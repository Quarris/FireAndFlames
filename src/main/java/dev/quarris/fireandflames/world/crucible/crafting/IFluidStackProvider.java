package dev.quarris.fireandflames.world.crucible.crafting;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Function;

public interface IFluidStackProvider {

    Codec<IFluidStackProvider> CODEC = Codec.xor(Direct.CODEC, Tag.CODEC)
        .xmap(either -> either.map(Function.identity(), Function.identity()),
            output -> {
                if (output instanceof Direct direct) {
                    return Either.left(direct);
                }

                if (output instanceof Tag tag) {
                    return Either.right(tag);
                }

                throw new UnsupportedOperationException("Fluid output is neither Direct nor Tag");
            });

    FluidStack createFluid();

    boolean matches(FluidStack stack);

    record Direct(FluidStack stack) implements IFluidStackProvider {

        public static final Codec<Direct> CODEC = FluidStack.CODEC.xmap(Direct::new, Direct::createFluid);

        @Override
        public FluidStack createFluid() {
            return this.stack.copy();
        }

        @Override
        public boolean matches(FluidStack stack) {
            return this.stack.is(stack.getFluid());
        }
    }

    record Tag(TagKey<Fluid> tag, int amount) implements IFluidStackProvider {

        public static final Codec<Tag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(Tag::tag),
            Codec.INT.fieldOf("amount").forGetter(Tag::amount)
        ).apply(instance, Tag::new));

        @Override
        public FluidStack createFluid() {
            return BuiltInRegistries.FLUID.getTag(this.tag).map(tags -> new FluidStack(tags.get(0), this.amount)).orElseThrow(() -> new IllegalArgumentException("Could not create fluid from tag " + this.tag));
        }

        @Override
        public boolean matches(FluidStack stack) {
            return stack.is(this.tag);
        }
    }
}
