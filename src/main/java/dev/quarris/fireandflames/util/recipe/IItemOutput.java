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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

                throw new UnsupportedOperationException("Item output is neither Direct nor Tag");
            });

    StreamCodec<RegistryFriendlyByteBuf, IItemOutput> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    StreamCodec<RegistryFriendlyByteBuf, List<IItemOutput>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
        ByteBufCodecs.collection(NonNullList::createWithCapacity));

    IItemOutput withAmount(int amount);

    ItemStack createItemStack();

    record Stack(ItemStack stack) implements IItemOutput {

        public static final Codec<Stack> CODEC = ItemStack.CODEC.xmap(Stack::new, Stack::createItemStack);

        public Stack(Item item, int amount) {
            this(new ItemStack(item, amount));
        }

        public Stack(Item item) {
            this(new ItemStack(item));
        }

        @Override
        public IItemOutput withAmount(int amount) {
            return new Stack(this.stack.copyWithCount(amount));
        }

        @Override
        public ItemStack createItemStack() {
            return this.stack.copy();
        }
    }

    record Tag(TagKey<Item> tag, int count) implements IItemOutput {

        public static final Codec<Tag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(Tag::tag),
            Codec.INT.fieldOf("count").forGetter(Tag::count)
        ).apply(instance, Tag::new));

        public Tag(TagKey<Item> tag) {
            this(tag, 1);
        }

        @Override
        public IItemOutput withAmount(int amount) {
            return new Tag(this.tag, amount);
        }

        @Override
        public ItemStack createItemStack() {
            return BuiltInRegistries.ITEM.getTag(this.tag).map(tags -> new ItemStack(tags.get(0), this.count)).orElseThrow(() -> new IllegalArgumentException("Could not create fluid from tag " + this.tag));
        }
    }
}
