package com.kqp.inventorytabs.tabs.provider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.kqp.inventorytabs.tabs.tab.ChestTab;
import com.kqp.inventorytabs.tabs.tab.Tab;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Provides tabs for ender chests. Limits amount of ender chest tabs to only one
 * and takes into account if it's blocked.
 */
public class EnderChestTabProvider extends BlockTabProvider {
    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        super.addAvailableTabs(player, tabs);

        Set<ChestTab> tabsToRemove = new HashSet<>();

        List<ChestTab> chestTabs = tabs.stream().filter(tab -> tab instanceof ChestTab).map(tab -> (ChestTab) tab)
                .filter(tab -> tab.blockId == Registries.BLOCK.getId(Blocks.ENDER_CHEST)).toList();

        World world = player.getWorld();

        // Add any chests that are blocked
        chestTabs.stream().filter(tab -> ChestBlock.isChestBlocked(world, tab.blockPos)).forEach(tabsToRemove::add);

        boolean found = false;

        for (ChestTab tab : chestTabs) {
            if (!tabsToRemove.contains(tab)) {
                if (!found) {
                    found = true;
                } else {
                    tabsToRemove.add(tab);
                }
            }
        }

        tabs.removeAll(tabsToRemove);
    }

    @Override
    public boolean matches(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST;
    }

    @Override
    public Tab createTab(World world, BlockPos pos) {
        return new ChestTab(Registries.BLOCK.getId(Blocks.ENDER_CHEST), pos);
    }
}
