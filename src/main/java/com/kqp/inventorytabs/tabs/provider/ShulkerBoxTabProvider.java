package com.kqp.inventorytabs.tabs.provider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.kqp.inventorytabs.mixin.ShulkerBoxBlockInvoker;
import com.kqp.inventorytabs.tabs.tab.SimpleBlockTab;
import com.kqp.inventorytabs.tabs.tab.Tab;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Provides tabs for shulker boxes. Takes into account if it's blocked.
 */
public class ShulkerBoxTabProvider extends BlockTabProvider {
    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        super.addAvailableTabs(player, tabs);

        Set<SimpleBlockTab> tabsToRemove = new HashSet<>();

        List<SimpleBlockTab> shulkerTabs = tabs.stream().filter(tab -> tab instanceof SimpleBlockTab)
                .map(tab -> (SimpleBlockTab) tab)
                .filter(tab -> Registries.BLOCK.get(tab.blockId) instanceof ShulkerBoxBlock).toList();

        // Add any chests that are blocked
        shulkerTabs.stream().filter(tab -> {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(tab.blockPos);

            if (blockEntity instanceof ShulkerBoxBlockEntity) {
                BlockState blockState = player.getWorld().getBlockState(tab.blockPos);

                return !ShulkerBoxBlockInvoker.invokeCanOpen(blockState, player.getWorld(), tab.blockPos,
                        (ShulkerBoxBlockEntity) blockEntity);
            }

            return false;
        }).forEach(tabsToRemove::add);

        tabs.removeAll(tabsToRemove);
    }

    @Override
    public boolean matches(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof ShulkerBoxBlock;
    }

    @Override
    public Tab createTab(World world, BlockPos pos) {
        return new SimpleBlockTab(Registries.BLOCK.getId(world.getBlockState(pos).getBlock()), pos);
    }
}
