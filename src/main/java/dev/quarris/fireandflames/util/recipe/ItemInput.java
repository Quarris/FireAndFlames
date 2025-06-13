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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public record ItemInput(Ingredient ingredient, INumberProvider count) {

    public static final Codec<ItemInput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Ingredient.MAP_CODEC_NONEMPTY.forGetter(ItemInput::ingredient),
        INumberProvider.CODEC.fieldOf("count").forGetter(ItemInput::count)
    ).apply(instance, ItemInput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemInput> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, ItemInput::ingredient,
        INumberProvider.STREAM_CODEC, ItemInput::count,
        ItemInput::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemInput>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
        ByteBufCodecs.collection(NonNullList::createWithCapacity));

    public ItemInput withAmount(INumberProvider count) {
        return new ItemInput(this.ingredient, count);
    }

    public ItemInput(ItemLike item) {
        this(item, 1);
    }

    public ItemInput(TagKey<Item> itemTag) {
        this(itemTag, 1);
    }

    public ItemInput(ItemStack stack) {
        this(Ingredient.of(stack), new ConstantNumber(stack.getCount()));
    }

    public ItemInput(ItemLike item, int count) {
        this(Ingredient.of(item), new ConstantNumber(count));
    }

    public ItemInput(TagKey<Item> itemTag, int count) {
        this(Ingredient.of(itemTag), new ConstantNumber(count));
    }

    public ItemInput(ItemLike item, INumberProvider count) {
        this(Ingredient.of(item), count);
    }

    public ItemInput(TagKey<Item> itemTag, INumberProvider count) {
        this(Ingredient.of(itemTag), count);
    }

    public boolean matchesAmount(ItemStack input) {
        return this.test(input) && input.getCount() >= this.count.evaluateInt();
    }

    public boolean test(ItemStack input) {
        return this.ingredient.test(input);
    }
}
