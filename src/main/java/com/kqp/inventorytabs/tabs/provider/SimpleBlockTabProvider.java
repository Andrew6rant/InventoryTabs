package com.kqp.inventorytabs.tabs.provider;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.kqp.inventorytabs.tabs.tab.SimpleBlockTab;
import com.kqp.inventorytabs.tabs.tab.Tab;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * Provides tabs for simple blocks.
 */
public class SimpleBlockTabProvider extends BlockTabProvider {
    private final Set<Identifier> blockIds = new HashSet<>();

    public SimpleBlockTabProvider() {
    }

    public void addBlock(Block block) {
        blockIds.add(Registry.BLOCK.getId(block));
    }

    public void addBlock(Identifier identifier) {
        blockIds.add(identifier);
    }

    public void removeBlock(Block block) {
        blockIds.remove(Registry.BLOCK.getId(block));
    }

    public void removeBlock(Identifier identifier) {
        blockIds.remove(identifier);
    }

    public Set<Identifier> getBlockIds() {
        return this.blockIds;
    }

    public Set<Block> getBlocks() {
        return this.blockIds.stream().map(Registry.BLOCK::get).collect(Collectors.toSet());
    }

    @Override
    public boolean matches(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);

        if (blockIds.contains(Registry.BLOCK.getId(blockState.getBlock()))) {
            return true;
        }

        return false;
    }

    @Override
    public Tab createTab(World world, BlockPos pos) {
        return new SimpleBlockTab(Registry.BLOCK.getId(world.getBlockState(pos).getBlock()), pos);
    }
}
