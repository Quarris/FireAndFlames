package dev.quarris.fireandflames.util.fluid;

import dev.quarris.fireandflames.client.fluid.SimpleClientFluidExtensions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomFluidHolder {

    private final ResourceLocation id;

    private IClientFluidTypeExtensions extensions;
    private BaseFlowingFluid.Properties properties;
    private DeferredBlock<LiquidBlock> liquidBlock;
    private DeferredItem<BucketItem> bucket;
    private DeferredHolder<FluidType, FluidType> fluidType;
    private DeferredHolder<Fluid, BaseFlowingFluid.Source> source;
    private DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing;

    public CustomFluidHolder(ResourceLocation id) {
        this.id = id;
    }

    CustomFluidHolder with(BaseFlowingFluid.Properties properties, DeferredItem<BucketItem> bucket, DeferredBlock<LiquidBlock> liquidBlock, DeferredHolder<FluidType, FluidType> fluidType, DeferredHolder<Fluid, BaseFlowingFluid.Source> source, DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing) {
        this.properties = properties;
        this.bucket = bucket;
        this.liquidBlock = liquidBlock;
        this.fluidType = fluidType;
        this.source = source;
        this.flowing = flowing;
        return this;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public BaseFlowingFluid.Properties getProperties() {
        return this.properties;
    }

    public DeferredHolder<FluidType, FluidType> getFluidType() {
        return this.fluidType;
    }

    public DeferredItem<BucketItem> getBucket() {
        return this.bucket;
    }

    public DeferredHolder<Fluid, BaseFlowingFluid.Source> getSource() {
        return this.source;
    }

    public DeferredHolder<Fluid, BaseFlowingFluid.Flowing> getFlowing() {
        return this.flowing;
    }

    public DeferredBlock<LiquidBlock> getLiquidBlock() {
        return this.liquidBlock;
    }

    public IClientFluidTypeExtensions getFluidExtensions() {
        if (this.extensions == null) {
            this.extensions = new SimpleClientFluidExtensions(this.id);
        }

        return this.extensions;
    }

    public static Builder builder(Supplier<FluidType> fluidTypeSupplier){
        return new Builder(fluidTypeSupplier);
    }

    public static class Builder {

        private Supplier<FluidType> fluidType;
        private Function<FlowingFluid, LiquidBlock> liquidBlock = flowingFluid -> new LiquidBlock(flowingFluid, BlockBehaviour.Properties.of());
        private Function<Fluid, BucketItem> bucket = fluid -> new BucketItem(fluid, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        private Function<BaseFlowingFluid.Properties, BaseFlowingFluid.Source> sourceFactory = BaseFlowingFluid.Source::new;
        private Function<BaseFlowingFluid.Properties, BaseFlowingFluid.Flowing> flowingFactory = BaseFlowingFluid.Flowing::new;

        private Consumer<BaseFlowingFluid.Properties> propertiesBuilder;

        private Builder(Supplier<FluidType> fluidType) {
            this.fluidType = fluidType;
        }

        public Builder liquidBlock(Function<FlowingFluid, LiquidBlock> liquidBlock) {
            this.liquidBlock = liquidBlock;
            return this;
        }

        public Builder customBlockProperties(Function<BlockBehaviour.Properties, BlockBehaviour.Properties> props) {
            this.liquidBlock = flowingFluid -> new LiquidBlock(flowingFluid, props.apply(BlockBehaviour.Properties.of()
                .replaceable()
                .noCollission()
                .randomTicks()
                .strength(100.0F)
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .liquid()
                .sound(SoundType.EMPTY)));
            return this;
        }

        public Builder bucket(Function<Fluid, BucketItem> bucket) {
            this.bucket = bucket;
            return this;
        }

        public Builder customBucketProperties(Supplier<Item.Properties> props) {
            this.bucket = fluid -> new BucketItem(fluid, props.get());
            return this;
        }

        public Builder source(Function<BaseFlowingFluid.Properties, BaseFlowingFluid.Source> sourceFactory) {
            this.sourceFactory = sourceFactory;
            return this;
        }

        public Builder flowing(Function<BaseFlowingFluid.Properties, BaseFlowingFluid.Flowing> flowingFactory) {
            this.flowingFactory = flowingFactory;
            return this;
        }

        public Builder applyProperties(Consumer<BaseFlowingFluid.Properties> builder) {
            this.propertiesBuilder = builder;
            return this;
        }

        CustomFluidHolder build(
            DeferredRegister<FluidType> fluidTypeRegistry,
            DeferredRegister<Fluid> fluidRegistry,
            DeferredRegister.Items itemRegistry,
            DeferredRegister.Blocks blockRegistry,
            ResourceLocation id
        ) {
            Objects.requireNonNull(this.fluidType);
            Objects.requireNonNull(this.liquidBlock);
            Objects.requireNonNull(this.bucket);
            Objects.requireNonNull(this.sourceFactory);
            Objects.requireNonNull(this.flowingFactory);

            CustomFluidHolder fluidHolder = new CustomFluidHolder(id);
            BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(
                () -> fluidHolder.getFluidType().value(),
                () -> fluidHolder.getSource().value(),
                () -> fluidHolder.getFlowing().value()
            );

            DeferredItem<BucketItem> bucket = itemRegistry.register(id.withSuffix("_bucket").getPath(), () -> this.bucket.apply(fluidHolder.source.get()));
            DeferredBlock<LiquidBlock> liquidBlock = blockRegistry.register(id.getPath(), () -> this.liquidBlock.apply(fluidHolder.source.get()));

            properties.bucket(bucket);
            properties.block(liquidBlock);
            if (this.propertiesBuilder != null) {
                this.propertiesBuilder.accept(properties);
            }

            DeferredHolder<FluidType, FluidType> fluidType = fluidTypeRegistry.register(id.getPath(), this.fluidType);
            DeferredHolder<Fluid, BaseFlowingFluid.Source> sourceFluidHolder = fluidRegistry.register(id.getPath(), () -> this.sourceFactory.apply(properties));
            DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingFluidHolder = fluidRegistry.register(id.withPrefix("flowing_").getPath(), () -> this.flowingFactory.apply(properties));

            return fluidHolder.with(properties, bucket, liquidBlock, fluidType, sourceFluidHolder, flowingFluidHolder);
        }
    }

}
