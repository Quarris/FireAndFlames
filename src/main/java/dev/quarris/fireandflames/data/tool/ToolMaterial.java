package dev.quarris.fireandflames.data.tool;

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

public record ToolMaterial(
    String name,
    int durability,
    int durabilityBonus,
    TagKey<Block> deniesBlocks,
    float speed,
    float speedBonus,
    float damageModifier,
    float damageBonus,
    float attackSpeed
) {

    private static final Codec<TagKey<Block>> BLOCK_TAG_CODEC = TagKey.codec(Registries.BLOCK);
    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<Block>> BLOCK_TAG_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(BLOCK_TAG_CODEC);

    public static final Codec<ToolMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(ToolMaterial::name),
        Codec.INT.fieldOf("durability").forGetter(ToolMaterial::durability),
        Codec.INT.fieldOf("durability_bonus").forGetter(ToolMaterial::durabilityBonus),
        BLOCK_TAG_CODEC.fieldOf("denies_blocks").forGetter(ToolMaterial::deniesBlocks),
        Codec.FLOAT.fieldOf("speed").forGetter(ToolMaterial::speed),
        Codec.FLOAT.fieldOf("speed_bonus").forGetter(ToolMaterial::speedBonus),
        Codec.FLOAT.fieldOf("damage").forGetter(ToolMaterial::damageModifier),
        Codec.FLOAT.fieldOf("damage_bonus").forGetter(ToolMaterial::damageBonus),
        Codec.FLOAT.fieldOf("attack_speed").forGetter(ToolMaterial::attackSpeed)
    ).apply(instance, ToolMaterial::new));

    public static final Codec<Holder<ToolMaterial>> HOLDER_CODEC = RegistryFileCodec.create(RegistrySetup.Keys.MATERIALS, ToolMaterial.CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolMaterial> STREAM_CODEC = StreamCodec.of(ToolMaterial::toNetwork, ToolMaterial::fromNetwork);

    private static void toNetwork(RegistryFriendlyByteBuf buf, ToolMaterial material) {
        ByteBufCodecs.STRING_UTF8.encode(buf, material.name());
        ByteBufCodecs.INT.encode(buf, material.durability());
        ByteBufCodecs.INT.encode(buf, material.durabilityBonus());
        BLOCK_TAG_STREAM_CODEC.encode(buf, material.deniesBlocks());
        ByteBufCodecs.FLOAT.encode(buf, material.speed());
        ByteBufCodecs.FLOAT.encode(buf, material.speedBonus());
        ByteBufCodecs.FLOAT.encode(buf, material.damageModifier());
        ByteBufCodecs.FLOAT.encode(buf, material.damageBonus());
        ByteBufCodecs.FLOAT.encode(buf, material.attackSpeed());
    }

    private static ToolMaterial fromNetwork(RegistryFriendlyByteBuf buf) {
        var name = ByteBufCodecs.STRING_UTF8.decode(buf);
        var durability = ByteBufCodecs.INT.decode(buf);
        var durabilityBonus = ByteBufCodecs.INT.decode(buf);
        TagKey<Block> deniesBlocks = BLOCK_TAG_STREAM_CODEC.decode(buf);
        var speed = ByteBufCodecs.FLOAT.decode(buf);
        var speedBonus = ByteBufCodecs.FLOAT.decode(buf);
        var damage = ByteBufCodecs.FLOAT.decode(buf);
        var damageBonus = ByteBufCodecs.FLOAT.decode(buf);
        var attackSpeed = ByteBufCodecs.FLOAT.decode(buf);

        return new ToolMaterial(name, durability, durabilityBonus, deniesBlocks, speed, speedBonus, damage, damageBonus, attackSpeed);
    }
}
