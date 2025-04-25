package dev.quarris.fireandflames.world.crucible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.world.block.CrucibleControllerBlock;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.TagSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrucibleStructure {

    public static final Codec<CrucibleStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("width").forGetter(CrucibleStructure::getWidth),
        Codec.INT.fieldOf("height").forGetter(CrucibleStructure::getHeight),
        Codec.INT.fieldOf("depth").forGetter(CrucibleStructure::getDepth),
        Codec.BOOL.fieldOf("structureValid").forGetter(CrucibleStructure::isStructureValid),
        BlockPos.CODEC.optionalFieldOf("structurePosition").forGetter(CrucibleStructure::getStructurePosition),
        BlockPos.CODEC.optionalFieldOf("controllerPosition").forGetter(CrucibleStructure::getControllerPosition)
    ).apply(instance, (w, h, d, valid, strPos, ctrlPos) -> new CrucibleStructure(w, h, d, valid, strPos.orElse(null), ctrlPos.orElse(null))));

    private static final int MAX_SIZE = 23; // Maximum width/depth

    private Level level;
    private boolean structureValid;
    private int width, height, depth; // xSize, ySize, zSize including walls
    private BlockPos structurePosition; // Minimum position of the crucible structure (min x,y,z)
    private BlockPos controllerPosition;

    public CrucibleStructure(BlockPos controllerPosition) {
        this.controllerPosition = controllerPosition;
    }

    private CrucibleStructure(int width, int height, int depth, boolean structureValid, BlockPos structurePosition, BlockPos controllerPosition) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.structureValid = structureValid;
        this.structurePosition = structurePosition;
        this.controllerPosition = controllerPosition;
    }

    public int getVolume() {
        if (!this.structureValid) {
            return 0;
        }

        return (this.width - 2) * (this.depth - 2) * (this.height - 1);
    }

    public boolean validateCrucibleStructure() {
        if (this.level == null) return false;

        if (!this.calculateCrucibleShape()) {
            return false;
        }

        // Validate the structure.
        // Check floor (excluding the bottom edges, i.e. size of width-2*depth-2)
        for (int x = 1; x < width - 1; x++) {
            for (int z = 1; z < depth - 1; z++) {
                BlockPos floorPos = structurePosition.offset(x, 0, z);
                if (!isValidCrucibleBlock(floorPos)) {
                    return false;
                }
            }
        }

        // Create a list of BlockPos and compute the positions of all wall blocks (width*depth) (excluding corners)
        List<BlockPos> wallPositions = new ArrayList<>();

        // Add the North wall positions (excluding corners)
        for (int x = 1; x < width - 1; x++) {
            wallPositions.add(structurePosition.offset(x, 1, 0));
        }

        // Add the South wall positions (excluding corners)
        for (int x = 1; x < width - 1; x++) {
            wallPositions.add(structurePosition.offset(x, 1, depth - 1));
        }

        // Add the East wall positions (excluding corners)
        for (int z = 1; z < depth - 1; z++) {
            wallPositions.add(structurePosition.offset(width - 1, 1, z));
        }

        // Add the West wall positions (excluding corners)
        for (int z = 1; z < depth - 1; z++) {
            wallPositions.add(structurePosition.offset(0, 1, z));
        }

        // For each wall position, go up until any of the blocks are no longer crucible blocks
        // That indicates the height of the structure
        int internalHeight = 0;
        boolean reachedTop = false;
        boolean foundController = false;
        while (!reachedTop) {

            for (BlockPos baseWallPos : wallPositions) {
                BlockPos wallPos = baseWallPos.above(internalHeight);
                BlockState state = this.level.getBlockState(wallPos);
                if (state.is(BlockSetup.CRUCIBLE_CONTROLLER.get())) {
                    if (!wallPos.equals(this.controllerPosition)) {
                        reachedTop = true;
                        break;
                    }

                    foundController = true;
                }

                if (!this.level.isInWorldBounds(wallPos) || !isValidCrucibleBlock(wallPos)) {
                    reachedTop = true;
                    break;
                }
            }

            if (!reachedTop) {
                internalHeight++;
            }
        }

        if (!foundController) {
            return false;
        }

        this.height = internalHeight + 1;

        // The structure is valid if we found a height of at least 2 blocks
        this.structureValid = this.height >= 1;

        System.out.printf("Width: %d, Height: %d, Depth: %d%n", this.width, this.height, this.depth);
        return this.structureValid;
    }

    /**
     * Calculates the width, depth and the down extent of the structure.
     * @return
     */
    private boolean calculateCrucibleShape() {
        Direction facing = this.level.getBlockState(this.controllerPosition).getValue(CrucibleControllerBlock.FACING);

        // Reset structure info
        this.width = 0;
        this.height = 0;
        this.depth = 0;
        this.structureValid = false;
        this.structurePosition = null;

        // 1. Search back for air blocks until reaching a fire brick or max distance
        // This determines the depth of the structure
        Direction back = facing.getOpposite();
        Direction right = facing.getClockWise();
        Direction left = facing.getCounterClockWise();
        BlockPos searchPos = controllerPosition.relative(back);
        BlockPos.MutableBlockPos structurePos = controllerPosition.mutable();
        BlockPos.MutableBlockPos maxPos = controllerPosition.mutable();

        // Search back of the controller until reaching a valid crucible block.
        boolean invalidSize = true;
        for (int i = 0; i < MAX_SIZE - 2; i++) {
            BlockPos checkPos = searchPos.relative(back, i);
            BlockState state = level.getBlockState(checkPos);

            if (isValidCrucibleBlock(checkPos)) {
                if (i == 0) return false; // Need at least 1 air block
                structurePos.set(Math.min(structurePos.getX(), checkPos.getX()), structurePos.getY(), Math.min(structurePos.getZ(), checkPos.getZ()));
                maxPos.set(Math.max(maxPos.getX(), checkPos.getX()), maxPos.getY(), Math.max(maxPos.getZ(), checkPos.getZ()));
                invalidSize = false;
                break;
            } else if (!state.isAir()) {
                return false;
            }
        }

        if (invalidSize) { // Could not find back of the crucible
            return false;
        }

        invalidSize = true;
        // Search the internal air spaces left of the controller until reaching a valid crucible block
        for (int i = 0; i < MAX_SIZE; i++) {
            BlockPos checkPos = searchPos.relative(left, i);
            if (isValidCrucibleBlock(checkPos)) {
                structurePos.set(Math.min(structurePos.getX(), checkPos.getX()), structurePos.getY(), Math.min(structurePos.getZ(), checkPos.getZ()));
                maxPos.set(Math.max(maxPos.getX(), checkPos.getX()), maxPos.getY(), Math.max(maxPos.getZ(), checkPos.getZ()));
                invalidSize = false;
                break;
            } else if (!level.getBlockState(checkPos).isAir()) {
                return false;
            }
        }

        if (invalidSize) { // Could not find right wall of the crucible
            return false;
        }

        // Search the internal air spaces right of the controller until reaching a valid crucible block

        invalidSize = true;
        for (int i = 0; i < MAX_SIZE; i++) {
            BlockPos checkPos = searchPos.relative(right, i);
            if (isValidCrucibleBlock(checkPos)) {
                structurePos.set(Math.min(structurePos.getX(), checkPos.getX()), structurePos.getY(), Math.min(structurePos.getZ(), checkPos.getZ()));
                maxPos.set(Math.max(maxPos.getX(), checkPos.getX()), maxPos.getY(), Math.max(maxPos.getZ(), checkPos.getZ()));
                invalidSize = false;
                break;
            } else if (!level.getBlockState(checkPos).isAir()) {
                return false;
            }
        }

        if (invalidSize) { // Could not find right wall of the crucible
            return false;
        }

        // Calculate width and depth from the extents
        width = maxPos.getX() - structurePos.getX() + 1;
        depth = maxPos.getZ() - structurePos.getZ() + 1;

        // Internal width must be within valid range
        if (width < 3 || width > MAX_SIZE || depth < 3 || depth > MAX_SIZE) { // Minimum 3x3 (1x1 internal)
            return false;
        }

        // Find rightmost air extent by searching right from behind the controller
        BlockPos downCheckPos = searchPos.below();
        while (level.getBlockState(downCheckPos).isAir()) {
            downCheckPos = downCheckPos.below();
        }

        if (!isValidCrucibleBlock(downCheckPos)) {
            return false;
        }

        structurePos.setY(downCheckPos.getY());
        this.structurePosition = structurePos.immutable();
        return true;
    }

    public void invalidate() {
        this.structureValid = false;
    }

    private boolean isValidCrucibleBlock(BlockPos pos) {
        return level.getBlockState(pos).is(TagSetup.BlockTags.VALID_CRUCIBLE_BLOCKS);
    }

    public static boolean isValidCrucibleBlock(BlockState state) {
        return state.is(TagSetup.BlockTags.VALID_CRUCIBLE_BLOCKS);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getDepth() {
        return this.depth;
    }

    public boolean isStructureValid() {
        return this.structureValid;
    }

    public Optional<BlockPos> getStructurePosition() {
        return Optional.ofNullable(this.structurePosition);
    }

    public Optional<BlockPos> getControllerPosition() {
        return Optional.ofNullable(this.controllerPosition);
    }
}
