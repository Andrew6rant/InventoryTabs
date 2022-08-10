package com.kqp.inventorytabs.tabs.provider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.kqp.inventorytabs.tabs.tab.ChestTab;
import com.kqp.inventorytabs.tabs.tab.Tab;
import com.kqp.inventorytabs.util.ChestUtil;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * Provides tabs for chests. Limits double chests to having only one tab and
 * takes into account if it's blocked.
 */
public class ChestTabProvider extends BlockTabProvider {
    private final Set<Identifier> chestBlocks = new HashSet<>();

    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        super.addAvailableTabs(player, tabs);

        Set<ChestTab> tabsToRemove = new HashSet<>();

        List<ChestTab> chestTabs = tabs.stream().filter(tab -> tab instanceof ChestTab).map(tab -> (ChestTab) tab)
                .filter(tab -> chestBlocks.contains(tab.blockId)).collect(Collectors.toList());

        World world = player.world;

        // Add any chests that are blocked
        chestTabs.stream().filter(tab -> ChestBlock.isChestBlocked(world, tab.blockPos)).forEach(tabsToRemove::add);

        for (ChestTab tab : chestTabs) {
            if (!tabsToRemove.contains(tab)) {
                if (ChestUtil.isDouble(world, tab.blockPos)) {
                    tabsToRemove.add(new ChestTab(tab.blockId, ChestUtil.getOtherChestBlockPos(world, tab.blockPos)));
                }
            }
        }

        tabs.removeAll(tabsToRemove);
    }

    public void addChestBlock(Block block) {
        chestBlocks.add(Registry.BLOCK.getId(block));
    }

    public void addChestBlock(Identifier blockId) {
        chestBlocks.add(blockId);
    }

    public void removeChestBlockId(Identifier blockId) {
        chestBlocks.remove(blockId);
    }

    public Set<Identifier> getChestBlockIds() {
        return this.chestBlocks;
    }

    @Override
    public boolean matches(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();

        return chestBlocks.contains(Registry.BLOCK.getId(block));
    }

    @Override
    public Tab createTab(World world, BlockPos pos) {
        return new ChestTab(Registry.BLOCK.getId(world.getBlockState(pos).getBlock()), pos);
    }
}
