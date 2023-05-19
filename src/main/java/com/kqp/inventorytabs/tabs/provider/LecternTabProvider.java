package com.kqp.inventorytabs.tabs.provider;

import com.kqp.inventorytabs.tabs.tab.SimpleBlockTab;
import com.kqp.inventorytabs.tabs.tab.Tab;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.block.LecternBlock.HAS_BOOK;
//import static net.minecraft.state.property.Properties.HAS_BOOK;

public class LecternTabProvider extends BlockTabProvider {
    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        super.addAvailableTabs(player, tabs);
        Set<SimpleBlockTab> tabsToRemove = new HashSet<>();
        List<SimpleBlockTab> lecternTabs = tabs.stream().filter(tab -> tab instanceof SimpleBlockTab).map(tab -> (SimpleBlockTab) tab)
                .filter(tab -> tab.blockId == Registries.BLOCK.getId(Blocks.LECTERN)).toList();
        lecternTabs.stream().filter(tab -> {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(tab.blockPos);

            if (blockEntity instanceof LecternBlockEntity) {
                BlockState blockState = player.getWorld().getBlockState(tab.blockPos);

                return !blockState.get(HAS_BOOK);
            }

            return false;
        }).forEach(tabsToRemove::add);

        tabs.removeAll(tabsToRemove);
    }

    @Override
    public boolean matches(World world, BlockPos pos) {
        return false;
    }

    @Override
    public Tab createTab(World world, BlockPos pos) {
        return null;
    }
}
