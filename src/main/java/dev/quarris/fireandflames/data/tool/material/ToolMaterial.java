package dev.quarris.fireandflames.data.tool.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * A tool material. It provides one value per {@link dev.quarris.fireandflames.data.tool.ToolStat};
 * which of them end up on a tool depends on the part the material is used in.
 *
 * @param durability   Base durability, multiplied by the tool type's durability modifier.
 * @param deniesBlocks Mining tier, as the tag of blocks this material cannot mine.
 * @param speed        Mining speed on blocks the tool is effective against.
 * @param damage       Attack damage added to the tool type's base damage.
 * @param attackSpeed  Attack speed added to the tool type's base attack speed.
 */
public record ToolMaterial(
    String name,
    int durability,
    TagKey<Block> deniesBlocks,
    float speed,
    float damage,
    float attackSpeed,
    MaterialColor color
) {

    private static final Codec<TagKey<Block>> BLOCK_TAG_CODEC = TagKey.codec(Registries.BLOCK);
    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<Block>> BLOCK_TAG_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(BLOCK_TAG_CODEC);

    public static final Codec<ToolMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(ToolMaterial::name),
        Codec.INT.fieldOf("durability").forGetter(ToolMaterial::durability),
        BLOCK_TAG_CODEC.fieldOf("denies_blocks").forGetter(ToolMaterial::deniesBlocks),
        Codec.FLOAT.fieldOf("speed").forGetter(ToolMaterial::speed),
        Codec.FLOAT.fieldOf("damage").forGetter(ToolMaterial::damage),
        Codec.FLOAT.fieldOf("attack_speed").forGetter(ToolMaterial::attackSpeed),
        MaterialColor.CODEC.fieldOf("color").forGetter(ToolMaterial::color)
    ).apply(instance, ToolMaterial::new));

    public static final Codec<Holder<ToolMaterial>> HOLDER_CODEC = RegistryFileCodec.create(RegistrySetup.Keys.MATERIALS, ToolMaterial.CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ToolMaterial>> HOLDER_STREAM_CODEC = ByteBufCodecs.holderRegistry(RegistrySetup.Keys.MATERIALS);

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolMaterial> STREAM_CODEC = StreamCodec.of(ToolMaterial::toNetwork, ToolMaterial::fromNetwork);

    private static void toNetwork(RegistryFriendlyByteBuf buf, ToolMaterial material) {
        ByteBufCodecs.STRING_UTF8.encode(buf, material.name());
        ByteBufCodecs.INT.encode(buf, material.durability());
        BLOCK_TAG_STREAM_CODEC.encode(buf, material.deniesBlocks());
        ByteBufCodecs.FLOAT.encode(buf, material.speed());
        ByteBufCodecs.FLOAT.encode(buf, material.damage());
        ByteBufCodecs.FLOAT.encode(buf, material.attackSpeed());
        MaterialColor.STREAM_CODEC.encode(buf, material.color());
    }

    private static ToolMaterial fromNetwork(RegistryFriendlyByteBuf buf) {
        var name = ByteBufCodecs.STRING_UTF8.decode(buf);
        var durability = ByteBufCodecs.INT.decode(buf);
        TagKey<Block> deniesBlocks = BLOCK_TAG_STREAM_CODEC.decode(buf);
        var speed = ByteBufCodecs.FLOAT.decode(buf);
        var damage = ByteBufCodecs.FLOAT.decode(buf);
        var attackSpeed = ByteBufCodecs.FLOAT.decode(buf);
        var color = MaterialColor.STREAM_CODEC.decode(buf);

        return new ToolMaterial(name, durability, deniesBlocks, speed, damage, attackSpeed, color);
    }
}
