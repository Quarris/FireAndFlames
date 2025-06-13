package dev.quarris.fireandflames.data.map;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.setup.MaterialConverterSetup;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public record ItemMaterialConverter(
    ItemInput item,
    int conversionUnits
) implements IMaterialConverter<ItemStack> {

    public static final MapCodec<ItemMaterialConverter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ItemInput.CODEC.fieldOf("item").forGetter(ItemMaterialConverter::item),
        ExtraCodecs.POSITIVE_INT.fieldOf("units").forGetter(ItemMaterialConverter::conversionUnits)
    ).apply(instance, ItemMaterialConverter::new));

    @Override
    public MapCodec<? extends IMaterialConverter<ItemStack>> codec() {
        return MaterialConverterSetup.ITEM.get();
    }

    @Override
    public boolean matches(Object input) {
        if (!(input instanceof ItemStack stack)) return false;

        return this.item.test(stack);
    }

    @Override
    public int getCountForUnits(int requiredUnits) {
        return this.item.count().evaluateInt() * Mth.ceil(requiredUnits / (float) this.conversionUnits);
    }

    @Override
    public int getUnits() {
        return this.conversionUnits;
    }
}
