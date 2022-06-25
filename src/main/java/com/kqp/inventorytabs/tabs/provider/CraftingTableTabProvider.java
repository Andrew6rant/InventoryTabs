package com.kqp.inventorytabs.tabs.provider;

import com.kqp.inventorytabs.tabs.tab.SimpleBlockTab;
import com.kqp.inventorytabs.tabs.tab.Tab;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CraftingTableTabProvider extends BlockTabProvider {
    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        super.addAvailableTabs(player, tabs);
        Set<SimpleBlockTab> tabsToRemove = new HashSet<>();
        List<SimpleBlockTab> craftingTableTabs = tabs.stream().filter(tab -> tab instanceof SimpleBlockTab).map(tab -> (SimpleBlockTab) tab)
                .filter(tab -> tab.blockId == Registry.BLOCK.getId(Blocks.CRAFTING_TABLE)).collect(Collectors.toList());
        boolean found = false;
        for (SimpleBlockTab tab : craftingTableTabs) {
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
        return world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE;
    }

    @Override
    public Tab createTab(World world, BlockPos pos) {
        return new SimpleBlockTab(Registry.BLOCK.getId(Blocks.CRAFTING_TABLE), pos);
    }
}
