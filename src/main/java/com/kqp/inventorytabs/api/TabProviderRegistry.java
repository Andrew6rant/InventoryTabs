package com.kqp.inventorytabs.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kqp.inventorytabs.init.InventoryTabs;
import com.kqp.inventorytabs.tabs.provider.*;

import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Registry for tab providers.
 */
public class TabProviderRegistry {
    private static final Map<Identifier, TabProvider> TAB_PROVIDERS = new HashMap<>();

    public static final PlayerInventoryTabProvider PLAYER_INVENTORY_TAB_PROVIDER = (PlayerInventoryTabProvider) register(
            InventoryTabs.id("player_inventory_tab_provider"), new PlayerInventoryTabProvider());
    public static final SimpleBlockTabProvider SIMPLE_BLOCK_TAB_PROVIDER = (SimpleBlockTabProvider) register(
            InventoryTabs.id("simple_block_tab_provider"), new SimpleBlockTabProvider());
    public static final ChestTabProvider CHEST_TAB_PROVIDER = (ChestTabProvider) register(
            InventoryTabs.id("chest_tab_provider"), new ChestTabProvider());
    public static final EnderChestTabProvider ENDER_CHEST_TAB_PROVIDER = (EnderChestTabProvider) register(
            InventoryTabs.id("ender_chest_tab_provider"), new EnderChestTabProvider());
    public static final ShulkerBoxTabProvider SHULKER_BOX_TAB_PROVIDER = (ShulkerBoxTabProvider) register(
            InventoryTabs.id("shulker_box_tab_provider"), new ShulkerBoxTabProvider());
    //public static final CraftingTableTabProvider CRAFTING_TABLE_TAB_PROVIDER = (CraftingTableTabProvider) register(
    //        InventoryTabs.id("crafting_table_tab_provider"), new CraftingTableTabProvider());
    public static final LecternTabProvider LECTERN_TAB_PROVIDER = (LecternTabProvider) register(
            InventoryTabs.id("lectern_tab_provider"), new LecternTabProvider());


    public static void init() {
        Registry.BLOCK.forEach(block -> {
            if (block instanceof BlockWithEntity) {
                if (block instanceof AbstractChestBlock) {
                    registerChest(block);
                } else if (!(block instanceof AbstractBannerBlock) && !(block instanceof AbstractSignBlock) && !(block instanceof AbstractSkullBlock) && !(block instanceof BeehiveBlock) && !(block instanceof BellBlock) && !(block instanceof CampfireBlock) && !(block instanceof ConduitBlock) && !(block instanceof DaylightDetectorBlock) && !(block instanceof EndGatewayBlock) && !(block instanceof EndPortalBlock) && !(block instanceof JukeboxBlock) && !(block instanceof PistonExtensionBlock) && !(block instanceof SculkSensorBlock) && !(block instanceof SpawnerBlock)) {
                    registerSimpleBlock(block);
                }
            } else if ((block instanceof CraftingTableBlock && !(block instanceof FletchingTableBlock)) || block instanceof AnvilBlock || block instanceof CartographyTableBlock || block instanceof GrindstoneBlock || block instanceof LoomBlock || block instanceof StonecutterBlock) {
                registerSimpleBlock(block);
            }
        });
    }

    //private static void addSimpleBlockTabProviders() {
    //}

    //private static void addModSimpleBlockTabProviders() {
    //}

    /**
     * Used to register a block with the simple block tab provider.
     *
     * @param block
     */
    public static void registerSimpleBlock(Block block) {
        SIMPLE_BLOCK_TAB_PROVIDER.addBlock(block);
    }

    /**
     * Used to register a block identifier with the simple block tab provider.
     *
     * @param blockId
     */
    public static void registerSimpleBlock(Identifier blockId) {
        SIMPLE_BLOCK_TAB_PROVIDER.addBlock(blockId);
    }

    /**
     * Used to register a chest with the chest tab provider.
     *
     * @param block
     */
    public static void registerChest(Block block) {
        CHEST_TAB_PROVIDER.addChestBlock(block);
    }

    /**
     * Used to register a chest with the chest tab provider.
     *
     * @param blockId
     */
    public static void registerChest(Identifier blockId) {
        CHEST_TAB_PROVIDER.addChestBlock(blockId);
    }

    public static TabProvider register(Identifier id, TabProvider tabProvider) {
        TAB_PROVIDERS.put(id, tabProvider);

        return tabProvider;
    }

    public static List<TabProvider> getTabProviders() {
        return new ArrayList<>(TAB_PROVIDERS.values());
    }
}
