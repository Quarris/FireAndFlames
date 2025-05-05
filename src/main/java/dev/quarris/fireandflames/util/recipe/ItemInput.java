package dev.quarris.fireandflames.util.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record ItemInput(Ingredient ingredient, int count) {

    public static final Codec<ItemInput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Ingredient.MAP_CODEC_NONEMPTY.forGetter(ItemInput::ingredient),
        Codec.INT.fieldOf("count").forGetter(ItemInput::count)
    ).apply(instance, ItemInput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemInput> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, ItemInput::ingredient,
        ByteBufCodecs.INT, ItemInput::count,
        ItemInput::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemInput>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
        ByteBufCodecs.collection(NonNullList::createWithCapacity));

    public ItemInput withAmount(int amount) {
        return new ItemInput(this.ingredient, amount);
    }

    public ItemInput(ItemStack item) {
        this(Ingredient.of(item), item.getCount());
    }

    public ItemInput(Item item, int amount) {
        this(Ingredient.of(item), amount);
    }

    public ItemInput(TagKey<Item> itemTag, int amount) {
        this(Ingredient.of(itemTag), amount);
    }

    public boolean matchesAmount(ItemStack input) {
        return this.test(input) && input.getCount() >= this.count;
    }

    public boolean test(ItemStack input) {
        return this.ingredient.test(input);
    }
}
