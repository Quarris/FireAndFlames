package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.world.block.CrucibleControllerBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockStateGen extends BlockStateProvider {

    public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ModRef.ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.simpleBlockWithItem(BlockSetup.FIRE_CLAY.get(), this.models().cubeAll("fire_clay", blockTexture(BlockSetup.FIRE_CLAY.get())));
        this.simpleBlockWithItem(BlockSetup.FIRE_BRICKS.get(), this.models().cubeAll("fire_bricks", blockTexture(BlockSetup.FIRE_BRICKS.get())));
        this.simpleBlockWithItem(BlockSetup.CRUCIBLE_WINDOW.get(), this.models().cubeColumn("crucible_window", blockTexture(BlockSetup.CRUCIBLE_WINDOW.get()), blockTexture(BlockSetup.FIRE_BRICKS.get())).renderType(RenderType.cutout().name));
        this.simpleBlockWithItem(BlockSetup.CRUCIBLE_DRAIN.get(), this.models().cubeAll("crucible_drain", blockTexture(BlockSetup.CRUCIBLE_DRAIN.get())));
        this.simpleBlockWithItem(BlockSetup.CRUCIBLE_TANK.get(), this.models().cubeColumn("crucible_tank", blockTexture(BlockSetup.CRUCIBLE_TANK.get()), blockTexture(BlockSetup.FIRE_BRICKS.get())).renderType(RenderType.cutout().name));

        var faucetModel = this.models().withExistingParent("crucible_faucet", ModRef.res("block/crucible_faucet_base")).texture("texture", blockTexture(BlockSetup.FIRE_BRICKS.get()));
        this.horizontalBlock(BlockSetup.CRUCIBLE_FAWSIT.get(), faucetModel);
        this.simpleBlockItem(BlockSetup.CRUCIBLE_FAWSIT.get(), faucetModel);

        this.simpleBlockWithItem(BlockSetup.CASTING_BASIN.get(), this.models().withExistingParent("casting_basin", ModRef.res("block/casting_basin_base")).texture("texture", blockTexture(BlockSetup.FIRE_BRICKS.get())));
        this.simpleBlockWithItem(BlockSetup.CASTING_TABLE.get(), this.models().withExistingParent("casting_table", ModRef.res("block/casting_table_base")).texture("texture", blockTexture(BlockSetup.FIRE_BRICKS.get())));

        this.simpleBlockWithItem(BlockSetup.MOLTEN_IRON.get(), this.models().getBuilder("molten_iron").texture("particle", ModRef.res("block/molten_iron")));

        BlockSetup.CRUCIBLE_CONTROLLER.asOptional().ifPresent(block -> {
            this.getVariantBuilder(block).forAllStates(
                (state) -> {
                    boolean lit = state.getValue(CrucibleControllerBlock.LIT);
                    String name = "crucible_controller";
                    ResourceLocation side = blockTexture(BlockSetup.FIRE_BRICKS.get());
                    ResourceLocation front = blockTexture(state.getBlock());
                    if (lit) {
                        front = front.withSuffix("_on");
                        name += "_on";
                    }

                    return ConfiguredModel.builder()
                        .modelFile(models().cubeAll(name, side).texture("north", front))
                        .uvLock(true)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build();
                });
        });

        this.itemModels().simpleBlockItem(BlockSetup.CRUCIBLE_CONTROLLER.get());
    }
}
