package com.kqp.inventorytabs.tabs.provider;

import java.util.List;

import com.kqp.inventorytabs.init.InventoryTabs;
import com.kqp.inventorytabs.tabs.tab.Tab;
import com.kqp.inventorytabs.util.BlockUtil;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Tab provider that exposes tabs based on nearby blocks.
 */
public abstract class BlockTabProvider implements TabProvider {
    public static final int SEARCH_DISTANCE = 5;

    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        World world = player.getWorld();

        // TODO: make this better and check line of sight
        for (int x = -SEARCH_DISTANCE; x <= SEARCH_DISTANCE; x++) {
            for (int y = -SEARCH_DISTANCE; y <= SEARCH_DISTANCE; y++) {
                for (int z = -SEARCH_DISTANCE; z <= SEARCH_DISTANCE; z++) {
                    BlockPos blockPos = player.getBlockPos().add(x, y, z);

                    if (matches(world, blockPos)) {
                        boolean add = false;

                        if (InventoryTabs.getConfig().doSightChecksFlag) {
                            BlockHitResult hitResult = BlockUtil.getLineOfSight(blockPos, player, 5D);

                            if (hitResult != null) {
                                add = true;
                            }
                        } else {
                            Vec3d playerHead = player.getPos().add(0D, player.getEyeHeight(player.getPose()), 0D);
                            Vec3d blockVec = new Vec3d(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D,
                                    blockPos.getZ() + 0.5D);

                            if (blockVec.subtract(playerHead).lengthSquared() <= SEARCH_DISTANCE * SEARCH_DISTANCE) {
                                add = true;
                            }
                        }

                        if (add) {
                            Tab tab = createTab(world, blockPos);

                            if (!tabs.contains(tab)) {
                                tabs.add(tab);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks to see if block at passsed block position matches criteria.
     *
     * @param world
     * @param pos
     * @return
     */
    public abstract boolean matches(World world, BlockPos pos);

    /**
     * Method to create tabs.
     *
     * @param world
     * @param pos
     * @return
     */
    public abstract Tab createTab(World world, BlockPos pos);
}
