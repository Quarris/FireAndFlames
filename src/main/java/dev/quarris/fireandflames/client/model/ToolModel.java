package dev.quarris.fireandflames.client.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.NamedRenderTypeManager;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public record ToolModel(List<PartSprite> parts) implements IUnbakedGeometry<ToolModel> {

    public static final Codec<ToolModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        PartSprite.CODEC.listOf().fieldOf("parts").forGetter(ToolModel::parts)
    ).apply(instance, ToolModel::new));

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        List<BakedQuad> bakedQuads = new ArrayList<>();

        var rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        }

        final ModelState finalModelState = modelState;
        this.parts.stream().sorted(Comparator.comparingInt(PartSprite::index)).forEach(part -> {
            TextureAtlasSprite sprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, part.texture()));
            bakedQuads.addAll(UnbakedGeometryHelper.bakeElements(UnbakedGeometryHelper.createUnbakedItemElements(part.index(), sprite), $ -> sprite, finalModelState));
        });

        var renderTypeHint = context.getRenderTypeHint();
        //NamedRenderTypeManager.get(ResourceLocation.withDefaultNamespace("cutout"))
        var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
        TextureAtlasSprite particleSprite = spriteGetter.apply(context.getMaterial("particle"));
        return new ToolModel.Baked(
            bakedQuads,
            context.useAmbientOcclusion(),
            context.isGui3d(),
            context.useBlockLight(),
            particleSprite,
            context.getTransforms(),
            overrides,
            renderTypes
        );
    }

    public record PartSprite(ResourceLocation texture, int index) {
        public static final Codec<PartSprite> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(PartSprite::texture),
            Codec.INT.fieldOf("index").forGetter(PartSprite::index)
        ).apply(instance, PartSprite::new));
    }

    public static class Baked implements IDynamicBakedModel {

        protected final List<BakedQuad> faces;
        protected final boolean hasAmbientOcclusion;
        protected final boolean isGui3d;
        protected final boolean usesBlockLight;
        protected final TextureAtlasSprite particleIcon;
        protected final ItemTransforms transforms;
        protected final ItemOverrides overrides;
        protected final ChunkRenderTypeSet blockRenderTypes;
        protected final List<RenderType> itemRenderTypes;
        protected final List<RenderType> fabulousItemRenderTypes;

        public Baked(
            List<BakedQuad> faces,
            boolean hasAmbientOcclusion,
            boolean isGui3d,
            boolean usesBlockLight,
            TextureAtlasSprite particleIcon,
            ItemTransforms transforms,
            ItemOverrides overrides,
            RenderTypeGroup renderTypes) {
            this.faces = faces;
            this.hasAmbientOcclusion = hasAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.usesBlockLight = usesBlockLight;
            this.particleIcon = particleIcon;
            this.transforms = transforms;
            this.overrides = overrides;
            this.blockRenderTypes = !renderTypes.isEmpty() ? net.neoforged.neoforge.client.ChunkRenderTypeSet.of(renderTypes.block()) : null;
            this.itemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entity()) : null;
            this.fabulousItemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entityFabulous()) : null;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
            return this.faces;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return this.hasAmbientOcclusion;
        }

        @Override
        public boolean isGui3d() {
            return this.isGui3d;
        }

        @Override
        public boolean usesBlockLight() {
            return this.usesBlockLight;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return this.particleIcon;
        }

        @Override
        public ItemTransforms getTransforms() {
            return this.transforms;
        }

        @Override
        public ItemOverrides getOverrides() {
            return this.overrides;
        }

        @Override
        public net.neoforged.neoforge.client.ChunkRenderTypeSet getRenderTypes(@org.jetbrains.annotations.NotNull BlockState state, @org.jetbrains.annotations.NotNull RandomSource rand, @org.jetbrains.annotations.NotNull net.neoforged.neoforge.client.model.data.ModelData data) {
            if (blockRenderTypes != null)
                return blockRenderTypes;
            return IDynamicBakedModel.super.getRenderTypes(state, rand, data);
        }

        @Override
        public List<net.minecraft.client.renderer.RenderType> getRenderTypes(net.minecraft.world.item.ItemStack itemStack, boolean fabulous) {
            if (!fabulous) {
                if (itemRenderTypes != null)
                    return itemRenderTypes;
            } else {
                if (fabulousItemRenderTypes != null)
                    return fabulousItemRenderTypes;
            }
            return IDynamicBakedModel.super.getRenderTypes(itemStack, fabulous);
        }
    }
}
