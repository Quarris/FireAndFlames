package dev.quarris.fireandflames.data.map;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.setup.MaterialConverterSetup;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidMaterialConverter(
    FluidInput fluid,
    int conversionUnits
) implements IMaterialConverter<FluidStack> {

    public static final MapCodec<FluidMaterialConverter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidInput.CODEC.fieldOf("fluid").forGetter(FluidMaterialConverter::fluid),
        ExtraCodecs.POSITIVE_INT.fieldOf("units").forGetter(FluidMaterialConverter::conversionUnits)
    ).apply(instance, FluidMaterialConverter::new));

    @Override
    public MapCodec<? extends IMaterialConverter<FluidStack>> codec() {
        return MaterialConverterSetup.FLUID.get();
    }

    @Override
    public boolean matches(Object input) {
        if (!(input instanceof FluidStack inputFluid)) return false;
        return this.fluid.test(inputFluid);
    }

    @Override
    public int getCountForUnits(int requiredUnits) {
        return this.fluid.amount().evaluateInt() * Mth.ceil(requiredUnits / (float) this.conversionUnits);
    }

    @Override
    public int getUnits() {
        return this.conversionUnits;
    }
}
