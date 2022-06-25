package com.kqp.inventorytabs.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

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
