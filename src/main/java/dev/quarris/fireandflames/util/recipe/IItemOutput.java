package dev.quarris.fireandflames.util.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.data.config.number.ConstantNumber;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.util.AdditionalCodecs;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Function;

public interface IItemOutput {

    Codec<IItemOutput> CODEC = Codec.xor(Stack.CODEC, Tag.CODEC)
        .xmap(either -> either.map(Function.identity(), Function.identity()),
            output -> {
                if (output instanceof Stack stack) {
                    return Either.left(stack);
                }

                if (output instanceof Tag tag) {
                    return Either.right(tag);
                }

                throw new UnsupportedOperationException("Item output is neither Stack nor Tag");
            });

    StreamCodec<RegistryFriendlyByteBuf, IItemOutput> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    StreamCodec<RegistryFriendlyByteBuf, List<IItemOutput>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
        ByteBufCodecs.collection(NonNullList::createWithCapacity));

    IItemOutput withAmount(INumberProvider count);

    ItemStack createItemStack();

    record Stack(ItemStack stack, INumberProvider count) implements IItemOutput {

        public static final Codec<Stack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AdditionalCodecs.OPTIONAL_SINGLE_ITEM_CODEC.fieldOf("item").forGetter(Stack::stack),
            INumberProvider.CODEC.optionalFieldOf("count", new ConstantNumber(1)).forGetter(Stack::count)
        ).apply(instance, Stack::new));

        public Stack(ItemStack stack) {
            this(stack, new ConstantNumber(stack.getCount()));
        }

        public Stack(ItemLike item, int count) {
            this(item, new ConstantNumber(count));
        }

        public Stack(ItemLike item, INumberProvider count) {
            this(new ItemStack(item), count);
        }

        public Stack(ItemLike item) {
            this(item, 1);
        }

        @Override
        public IItemOutput withAmount(INumberProvider count) {
            return new Stack(this.stack, count);
        }

        @Override
        public ItemStack createItemStack() {
            return this.stack.copyWithCount(this.count.evaluateInt());
        }
    }

    record Tag(TagKey<Item> tag, INumberProvider count) implements IItemOutput {

        public static final Codec<Tag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(Tag::tag),
            INumberProvider.CODEC.optionalFieldOf("count", new ConstantNumber(1)).forGetter(Tag::count)
        ).apply(instance, Tag::new));

        public Tag(TagKey<Item> tag, int count) {
            this(tag, new ConstantNumber(count));
        }

        public Tag(TagKey<Item> tag) {
            this(tag, 1);
        }

        @Override
        public IItemOutput withAmount(INumberProvider count) {
            return new Tag(this.tag, count);
        }

        @Override
        public ItemStack createItemStack() {
            return BuiltInRegistries.ITEM.getTag(this.tag).map(tags -> new ItemStack(tags.get(0), this.count.evaluateInt())).orElseThrow(() -> new IllegalArgumentException("Could not create fluid from tag " + this.tag));
        }
    }
}
