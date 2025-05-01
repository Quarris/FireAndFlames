package dev.quarris.fireandflames.world.crucible;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.TagSetup;
import dev.quarris.fireandflames.world.block.CrucibleControllerBlock;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class CrucibleStructure {

    public static final Map<BlockPos, CrucibleShape> ALL_CRUCIBLES = new HashMap<>();

    public static final Codec<CrucibleStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockPos.CODEC.fieldOf("controller_position").forGetter(CrucibleStructure::getControllerPosition),
        BlockPos.CODEC.listOf().fieldOf("drain_positions").forGetter(CrucibleStructure::getDrainPositions),
        CrucibleShape.CODEC.fieldOf("shape").forGetter(CrucibleStructure::getShape),
        Codec.BOOL.fieldOf("invalid").forGetter(CrucibleStructure::isInvalid),
        Codec.BOOL.fieldOf("dirty").forGetter(CrucibleStructure::isDirty)
    ).apply(instance, CrucibleStructure::new));

    private static final int MAX_SIZE = 23; // Maximum width/depth

    private final BlockPos controllerPosition;
    private final List<BlockPos> drainPositions = Lists.newArrayList();
    private CrucibleShape shape;
    private boolean invalid;
    private boolean dirty;

    private List<BlockPos> baseSides;
    private AABB internalBounds;

    public static CrucibleStructure of(Level level, BlockPos controllerPosition) {
        return formCrucibleStructure(level, controllerPosition);
    }

    private CrucibleStructure(BlockPos controllerPosition, List<BlockPos> drainPositions, CrucibleShape shape) {
        this.controllerPosition = controllerPosition;
        this.shape = shape;
        this.drainPositions.addAll(drainPositions);
    }

    private CrucibleStructure(BlockPos controllerPosition, List<BlockPos> drainPositions, CrucibleShape shape, boolean dirty, boolean invalid) {
        this(controllerPosition, drainPositions, shape);
        this.dirty = dirty;
        this.invalid = invalid;
    }

    public void notifyChange(Level pLevel, BlockPos pPos, BlockState pState) {
        int changeHeight = pPos.getY() - this.shape.position.getY();
        List<BlockPos> drains = this.getDrainPositions();
        drains.removeIf(drainPos -> {
            int drainHeight = drainPos.getY() - this.shape.position.getY();
            return drainHeight >= changeHeight;
        });
        int newHeight = validateLayersFrom(drains, pLevel, this.controllerPosition, this.getBaseSides(), this.shape, changeHeight);
        if (newHeight != this.shape.height) {
            if (newHeight < 2) {
                this.invalid = true;
                return;
            }

            this.drainPositions.forEach(drainPos -> pLevel.getBlockEntity(drainPos, BlockEntitySetup.CRUCIBLE_DRAIN.get()).ifPresent(drain -> drain.setCruciblePosition(null)));
            this.drainPositions.clear();
            this.drainPositions.addAll(drains);
            this.shape = this.shape.withHeight(newHeight);
            this.dirty = true;
        }
    }

    public List<BlockPos> getDrainPositions() {
        return Lists.newArrayList(this.drainPositions);
    }

    public int getInternalVolume() {
        return this.shape.getInternalVolume();
    }

    public List<BlockPos> getBaseSides() {
        return this.getBaseSides(0);
    }

    public List<BlockPos> getBaseSides(int height) {
        if (this.baseSides == null) {
            this.baseSides = ImmutableList.copyOf(computeBaseSides(this.shape.position(), this.shape.width(), this.shape.depth()));
        }

        if (height == 0) {
            return this.baseSides;
        }

        return this.baseSides.stream().map(p -> p.above(height)).toList();
    }

    public static CrucibleStructure formCrucibleStructure(Level level, BlockPos controllerPosition) {
        CrucibleShape calculatedShape = calculateCrucibleShape(level, controllerPosition);
        if (calculatedShape == null) {
            return null;
        }

        int width = calculatedShape.width;
        int depth = calculatedShape.depth;
        BlockPos startPos = calculatedShape.position;

        // Validate the structure.
        // Create a list of BlockPos and compute the positions of all wall blocks (width*depth) (excluding corners)
        List<BlockPos> wallPositions = computeBaseSides(startPos, width, depth);

        // For each wall position, go up until any of the blocks are no longer crucible blocks
        // That indicates the height of the structure
        List<BlockPos> drains = new ArrayList<>();
        int height = validateLayersFrom(drains, level, controllerPosition, wallPositions, calculatedShape, 0);
        if (height < 2) {
            return null;
        }

        calculatedShape = calculatedShape.withHeight(height);
        return new CrucibleStructure(controllerPosition, drains, calculatedShape);
    }

    /**
     * @param controllerPosition Position of crucible controller that contains the structure.
     * @param baseSides The positions of the walls to validate at.
     * @param shape The shape of the crucible attached to the controller.
     * @param startHeight the height at which to start validating from.
     * @return the height at which the structure was validated at.
     */
    private static int validateLayersFrom(List<BlockPos> outputDrainPositions, Level level, BlockPos controllerPosition, List<BlockPos> baseSides, CrucibleShape shape, int startHeight) {
        int height = startHeight;

        // Validate floor
        if (height == 0) {
            for (int x = 1; x < shape.width() - 1; x++) {
                for (int z = 1; z < shape.depth() - 1; z++) {
                    BlockPos floorPos = shape.position().offset(x, 0, z);
                    if (!isValidCrucibleBlock(level, floorPos)) {
                        return 0;
                    }
                }
            }
            height++;
        }


        boolean foundController = controllerPosition.getY() - shape.position().getY() < startHeight;

        heightCheck:
        while (!level.isOutsideBuildHeight(height)) {
            // Check the internal space is air
            for (int x = 1; x < shape.width() - 1; x++) {
                for (int z = 1; z < shape.depth() - 1; z++) {
                    if (!level.getBlockState(shape.position().offset(x, height, z)).isAir()) {
                        break heightCheck;
                    }
                }
            }

            // Check wall positions
            boolean foundControllerInLayer = false;
            List<BlockPos> drainPositionsInLayer = new ArrayList<>();
            for (BlockPos baseWallPos : baseSides) {
                BlockPos wallPos = baseWallPos.above(height);
                BlockState state = level.getBlockState(wallPos);
                if (!level.isInWorldBounds(wallPos)) {
                    return -1;
                }

                if (state.is(BlockSetup.CRUCIBLE_DRAIN.get())) {
                    drainPositionsInLayer.add(wallPos);
                    continue;
                }

                if (state.is(BlockSetup.CRUCIBLE_CONTROLLER.get())) {
                    if (!wallPos.equals(controllerPosition)) {
                        break heightCheck;
                    }

                    foundControllerInLayer = true;
                    continue;
                }

                if (!isValidCrucibleBlock(level, wallPos)) {
                    break heightCheck;
                }
            }

            // Successfully validated the layer.
            height++;
            outputDrainPositions.addAll(drainPositionsInLayer);
            if (foundControllerInLayer) {
                foundController = true;
            }
        }

        if (!foundController) {
            return -1;
        }

        return height;
    }

    private static List<BlockPos> computeBaseSides(BlockPos startPos, int width, int depth) {
        List<BlockPos> wallPositions = new ArrayList<>();

        // Add the North wall positions (excluding corners)
        for (int x = 1; x < width - 1; x++) {
            wallPositions.add(startPos.offset(x, 0, 0));
        }

        // Add the South wall positions (excluding corners)
        for (int x = 1; x < width - 1; x++) {
            wallPositions.add(startPos.offset(x, 0, depth - 1));
        }

        // Add the East wall positions (excluding corners)
        for (int z = 1; z < depth - 1; z++) {
            wallPositions.add(startPos.offset(width - 1, 0, z));
        }

        // Add the West wall positions (excluding corners)
        for (int z = 1; z < depth - 1; z++) {
            wallPositions.add(startPos.offset(0, 0, z));
        }

        return wallPositions;
    }

    /**
     * Calculates the width, depth and the down extent of the structure.
     * @return
     */
    private static CrucibleShape calculateCrucibleShape(Level level, BlockPos controllerPosition) {
        // 1. Search back for air blocks until reaching a fire brick or max distance
        // This determines the depth of the structure
        Direction facing = level.getBlockState(controllerPosition).getValue(CrucibleControllerBlock.FACING);
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

            if (isValidCrucibleBlock(level, checkPos)) {
                if (i == 0) return null; // Need at least 1 air block
                structurePos.set(Math.min(structurePos.getX(), checkPos.getX()), structurePos.getY(), Math.min(structurePos.getZ(), checkPos.getZ()));
                maxPos.set(Math.max(maxPos.getX(), checkPos.getX()), maxPos.getY(), Math.max(maxPos.getZ(), checkPos.getZ()));
                invalidSize = false;
                break;
            } else if (!state.isAir()) {
                return null;
            }
        }

        if (invalidSize) { // Could not find back of the crucible
            return null;
        }

        invalidSize = true;
        // Search the internal air spaces left of the controller until reaching a valid crucible block
        for (int i = 0; i < MAX_SIZE; i++) {
            BlockPos checkPos = searchPos.relative(left, i);
            if (isValidCrucibleBlock(level, checkPos)) {
                structurePos.set(Math.min(structurePos.getX(), checkPos.getX()), structurePos.getY(), Math.min(structurePos.getZ(), checkPos.getZ()));
                maxPos.set(Math.max(maxPos.getX(), checkPos.getX()), maxPos.getY(), Math.max(maxPos.getZ(), checkPos.getZ()));
                invalidSize = false;
                break;
            } else if (!level.getBlockState(checkPos).isAir()) {
                return null;
            }
        }

        if (invalidSize) { // Could not find right wall of the crucible
            return null;
        }

        // Search the internal air spaces right of the controller until reaching a valid crucible block

        invalidSize = true;
        for (int i = 0; i < MAX_SIZE; i++) {
            BlockPos checkPos = searchPos.relative(right, i);
            if (isValidCrucibleBlock(level, checkPos)) {
                structurePos.set(Math.min(structurePos.getX(), checkPos.getX()), structurePos.getY(), Math.min(structurePos.getZ(), checkPos.getZ()));
                maxPos.set(Math.max(maxPos.getX(), checkPos.getX()), maxPos.getY(), Math.max(maxPos.getZ(), checkPos.getZ()));
                invalidSize = false;
                break;
            } else if (!level.getBlockState(checkPos).isAir()) {
                return null;
            }
        }

        if (invalidSize) { // Could not find right wall of the crucible
            return null;
        }

        // Calculate width and depth from the extents
        int width = maxPos.getX() - structurePos.getX() + 1;
        int depth = maxPos.getZ() - structurePos.getZ() + 1;

        // Internal width must be within valid range
        if (width < 3 || width > MAX_SIZE || depth < 3 || depth > MAX_SIZE) { // Minimum 3x3 (1x1 internal)
            return null;
        }

        // Find right-most air extent by searching right from behind the controller
        BlockPos downCheckPos = searchPos.below();
        while (level.getBlockState(downCheckPos).isAir()) {
            downCheckPos = downCheckPos.below();
        }

        if (!isValidCrucibleBlock(level, downCheckPos)) {
            return null;
        }

        structurePos.setY(downCheckPos.getY());
        BlockPos structurePosition = structurePos.immutable();
        return new CrucibleShape(structurePosition, width, -1, depth);
    }

    public BlockPos getControllerPosition() {
        return this.controllerPosition;
    }

    public CrucibleShape getShape() {
        return this.shape;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public boolean isInvalid() {
        return this.invalid;
    }

    private static boolean isValidCrucibleBlock(Level level, BlockPos pos) {
        return isValidCrucibleBlock(level.getBlockState(pos));
    }

    public static boolean isValidCrucibleBlock(BlockState state) {
        return state.is(TagSetup.BlockTags.VALID_CRUCIBLE_BLOCKS);
    }

    public void markClean() {
        this.dirty = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CrucibleStructure structure = (CrucibleStructure) o;
        return Objects.equals(controllerPosition, structure.controllerPosition) && Objects.equals(shape, structure.shape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controllerPosition, shape);
    }

    public AABB getInternalBounds() {
        if (this.internalBounds == null) {
            this.internalBounds = AABB.of(BoundingBox.fromCorners(this.shape.position.offset(1, 1, 1), this.shape.position.offset(this.shape.width - 2, this.shape.height - 1, this.shape.depth - 2)));
        }

        return this.internalBounds;
    }

    /**
     * @param position  Minimum position of the crucible structure (min x,y,z)
     * @param depth  xSize, ySize, zSize including walls
     */
    public record CrucibleShape(BlockPos position, int width, int height, int depth) {

        public static final Codec<CrucibleShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("position").forGetter(CrucibleShape::position),
            Codec.INT.fieldOf("width").forGetter(CrucibleShape::width),
            Codec.INT.fieldOf("height").forGetter(CrucibleShape::height),
            Codec.INT.fieldOf("depth").forGetter(CrucibleShape::depth)
        ).apply(instance, CrucibleShape::new));

        public CrucibleShape withHeight(int height) {
            return new CrucibleShape(this.position, this.width, height, this.depth);
        }

        public int getInternalVolume() {
            return (this.width - 2) * (this.height - 1) * (this.depth - 2);
        }

        public boolean contains(BlockPos pos) {
            return pos.getX() >= this.position.getX() && pos.getY() >= this.position.getY() && pos.getZ() >= this.position.getZ() &&
                pos.getX() < this.position.getX() + this.width && pos.getY() < this.position.getY() + this.height && pos.getZ() < this.position.getZ() + this.depth;
        }

        public boolean containsAbove(BlockPos pos) {
            return pos.getX() >= this.position.getX() && pos.getY() >= this.position.getY() && pos.getZ() >= this.position.getZ() &&
                pos.getX() < this.position.getX() + this.width && pos.getY() < this.position.getY() + this.height + 1 && pos.getZ() < this.position.getZ() + this.depth;
        }

        public boolean containsInternal(BlockPos pos) {
            return pos.getX() >= this.position.getX() + 1 && pos.getY() >= this.position.getY() + 1 && pos.getZ() >= this.position.getZ() + 1 &&
                pos.getX() < this.position.getX() + this.width - 1 && pos.getY() < this.position.getY() + this.height && pos.getZ() < this.position.getZ() + this.depth - 1;
        }

        public BoundingBox toBoundingBox() {
            return BoundingBox.fromCorners(this.position, this.position.offset(this.width() - 1, this.height() - 1, this.depth() - 1));
        }
    }
}
