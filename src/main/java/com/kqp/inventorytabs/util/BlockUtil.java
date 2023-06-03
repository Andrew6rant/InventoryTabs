package com.kqp.inventorytabs.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static com.kqp.inventorytabs.util.ChestUtil.getOtherChestBlockPos;

public class BlockUtil {
    public static boolean inRange(BlockPos pos, PlayerEntity player, double distance) {
        double distanceSquared = distance * distance;

        Vec3d playerHead = player.getPos().add(0D, player.getEyeHeight(player.getPose()), 0D);
        Vec3d blockVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

        for (Vec3d sightOffset : SIGHT_OFFSETS) {
            if (blockVec.add(sightOffset).squaredDistanceTo(playerHead) <= distanceSquared) {
                return true;
            }
        }

        return false;
    }
    public static BlockHitResult getLineOfSight(BlockPos pos, PlayerEntity player, double distance) {
        World world = player.world;
        BlockState blockState = world.getBlockState(pos);
        double distanceSquared = distance * distance;

        Vec3d playerHead = player.getPos().add(0D, player.getEyeHeight(player.getPose()), 0D);
        Vec3d blockVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

        for (Vec3d sightOffset : SIGHT_OFFSETS) {
            Vec3d blockPosCheck = blockVec.add(sightOffset);

            BlockHitResult result = getBlockHitResult(playerHead, blockPosCheck, distanceSquared, world, pos,
                    blockState);

            if (result != null) {
                if (result.getBlockPos().equals(pos)) {
                    return result;
                }
            }
        }

        return null;
    }

    private static BlockHitResult getBlockHitResult(Vec3d playerHead, Vec3d blockVec, double distanceSquared,
            World world, BlockPos pos, BlockState blockState) {
        if (blockVec.subtract(playerHead).lengthSquared() >= distanceSquared) {
            return null;
        }

        BlockHitResult result = world.raycast(new RaycastContext(playerHead, blockVec, RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE, MinecraftClient.getInstance().player));

        if (result != null && result.getType() == HitResult.Type.BLOCK && result.getBlockPos().equals(pos)) {
            return result;
        }

        return null;
    }

    public static List<Text> getSignText(BlockPos blockPos) {
        return getSignText(blockPos, false);
    }

    public static List<Text> getSignText(BlockPos blockPos, boolean isChest) {
        World world = MinecraftClient.getInstance().player.world;
        ArrayList<Text> signTextLines = new ArrayList<>();

        // If the dye color of the sign should be used to format the label
        // True by default unless the sign has custom formatting applied to it (via commands)
        // If false, the sign's custom formatting is used instead
        boolean applySignDyeColor = true;

        // Check if this is a double chest
        boolean isDoubleChest = isChest && ChestUtil.isDouble(world, blockPos);
        BlockPos doubleChestPos = isDoubleChest ? getOtherChestBlockPos(world, blockPos) : blockPos;

        // Positions to check around the container
        ArrayList<BlockPos> positionsToCheck = new ArrayList<>();
        positionsToCheck.add(blockPos.add(1, 0, 0));
        positionsToCheck.add(blockPos.add(0, 0, 1));
        positionsToCheck.add(blockPos.add(-1, 0, 0));
        positionsToCheck.add(blockPos.add(0, 0, -1));

        // If this is a double chest, also check positions around the other side of the chest
        // (and don't check positions inside the chests)
        if(isDoubleChest) {
            positionsToCheck.add(doubleChestPos.add(1, 0, 0));
            positionsToCheck.add(doubleChestPos.add(0, 0, 1));
            positionsToCheck.add(doubleChestPos.add(-1, 0, 0));
            positionsToCheck.add(doubleChestPos.add(0, 0, -1));
            positionsToCheck.remove(doubleChestPos);
            positionsToCheck.remove(blockPos);
        }

        // Check each position needed
        BlockEntity blockEntity = null;
        int positionToCheckIndex = -1;
        for(BlockPos positionToCheck : positionsToCheck) {
            blockEntity = world.getBlockEntity(positionToCheck);

            // Keep track of which position is being checked
            // if this is a double chest, the other chest position needs to be used for the comparison
            // after all three sides of the first chest were checked
            positionToCheckIndex++;

            // If a sign was found around the container
            if(blockEntity instanceof SignBlockEntity) {

                // Check if it is a sign that could be attached to the container
                BlockState blockState = world.getBlockState(positionToCheck);
                if (blockState.contains(Properties.HORIZONTAL_FACING)) {
                    Direction direction = blockState.get(Properties.HORIZONTAL_FACING);

                    // If the block this sign is attached to sign is the container, then use it as the label for this container
                    if(positionToCheck.add(direction.getOpposite().getVector()).equals(positionToCheckIndex < 3 ? blockPos : doubleChestPos)) {
                        break;
                    }
                }
            }
        }

        // If a suitable sign was found, read the NBT data from it
        if (blockEntity != null) {
            NbtCompound tag = new NbtCompound();
            blockEntity.writeNbt(tag);

            // Check all 4 lines of text
            for (int lineNumber = 1; lineNumber <= 4; lineNumber++) {

                // If the current line is not empty
                if(!tag.getString("Text" + lineNumber).equals("{\"text\":\"\"}")) {

                    // Get the text on this line and add it to the list of texts to be processed
                    MutableText currentLineText = Text.Serializer.fromJson(tag.getString("Text" + lineNumber));
                    if(currentLineText == null) { continue; }
                    signTextLines.add(currentLineText);

                    // Check if this line contains any custom formatting (determine if the dye color should be used or not)
                    // For some reason Text.getStyle().isEmpty() always seems to return false
                    // even on simple un-formatted signs so this had to be done instead
                    if (currentLineText.getStyle() != null) {
                        if (currentLineText.getStyle().isBold() ||
                                currentLineText.getStyle().isItalic() ||
                                currentLineText.getStyle().isObfuscated() ||
                                currentLineText.getStyle().isStrikethrough() ||
                                currentLineText.getStyle().isUnderlined() ||
                                currentLineText.getStyle().getColor() != null) {
                            applySignDyeColor = false;
                        }
                    }
                }
            }

            // Apply the dye color if need be, replacing the default text on signs with a gray so the tooltip is visible
            if(applySignDyeColor) {
                String signDyeColorToUse = tag.getString("Color").equals("black") ? "gray" : tag.getString("Color");
                for (int currentLineInxed = 0; currentLineInxed < signTextLines.size(); currentLineInxed++) {
                    MutableText currentText = ((MutableText) signTextLines.get(currentLineInxed));
                    signTextLines.set(currentLineInxed, currentText.setStyle(Style.EMPTY.withColor(DyeColor.byName(signDyeColorToUse, DyeColor.GRAY).getSignColor())));
                }
            }

        }

        return signTextLines;
    }

    private static final Vec3d[] SIGHT_OFFSETS = {
            // Center
            new Vec3d(0.5D, 0.5D, 0.5D),

            // Corners
            new Vec3d(0.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, 0.0D),
            new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D),
            new Vec3d(1.0D, 0.0D, 1.0D), new Vec3d(1.0D, 1.0D, 1.0D),

            // Side centers
            new Vec3d(0.5D, 0D, 0.5D), new Vec3d(0.5D, 1D, 0.5D), new Vec3d(0.0D, 0.5D, 0.5D),
            new Vec3d(1.0D, 0.5D, 0.5D), new Vec3d(0.5D, 0.5D, 0.0D), new Vec3d(0.5D, 0.5D, 1.0D),

            // Corners, slightly in
            new Vec3d(0.2D, 0.2D, 0.2D), new Vec3d(0.8D, 0.2D, 0.2D), new Vec3d(0.2D, 0.8D, 0.2D),
            new Vec3d(0.2D, 0.2D, 0.8D), new Vec3d(0.8D, 0.8D, 0.2D), new Vec3d(0.2D, 0.8D, 0.8D),
            new Vec3d(0.8D, 0.2D, 0.8D), new Vec3d(0.8D, 0.8D, 0.8D),

            // Side centers, slightly in
            new Vec3d(0.5D, 0.2D, 0.5D), new Vec3d(0.5D, 0.8D, 0.5D), new Vec3d(0.2D, 0.5D, 0.5D),
            new Vec3d(0.8D, 0.5D, 0.5D), new Vec3d(0.5D, 0.5D, 0.2D), new Vec3d(0.5D, 0.5D, 0.8D), };
}
