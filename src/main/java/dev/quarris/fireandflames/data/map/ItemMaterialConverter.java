package dev.quarris.fireandflames.data.map;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.setup.MaterialConverterSetup;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

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
        if (input instanceof ItemStack stack) {
            return this.item.test(stack);
        }

        if (input instanceof Ingredient ingredient) {
            for (ItemStack testStack : ingredient.getItems()) {
                if (this.item.test(testStack)) {
                    return true;
                }
            }
        }

        return false;
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
